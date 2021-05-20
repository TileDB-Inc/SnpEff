package org.snpeff.nextProt;

import java.util.ArrayList;
import java.util.List;

import org.snpeff.interval.Markers;
import org.snpeff.snpEffect.ErrorWarningType;
import org.snpeff.util.Gpr;
import org.snpeff.util.Log;
import org.xml.sax.Attributes;

/**
 * A controlled vocabulatry term
 */
class CvTerm {
	public String accession, terminology, description;

	public CvTerm(Attributes attributes) {
		accession = attributes.getValue("accession");
		terminology = attributes.getValue("terminology");
	}

	@Override
	public String toString() {
		return "CvTerm(" + accession + ", " + terminology + ", " + description + ")";
	}
}

/**
 * A location
 */
class Location {
	public String type;
	public int begin, end;

	Location() {
		this(null, -1, -1);
	}

	Location(String type) {
		this(type, -1, -1);
	}

	Location(String type, int begin, int end) {
		this.type = type;
		this.begin = begin;
		this.end = end;
	}

	public boolean isValid() {
		return begin >= 0 && end >= 0;
	}

	@Override
	public String toString() {
		return "Location(" + (type != null ? type + ", " : "") + begin + ", " + end + ")";
	}
}

/**
 * A location respect to an isoform
 */
class LocationTargetIsoform extends Location {
	public String accession;

	LocationTargetIsoform(String accession) {
		super();
		this.accession = accession;
	}

	@Override
	public String toString() {
		return "LocationTargetIsoform(" + accession + ", " + begin + ", " + end + ")";
	}

}

/**
 * Mimics the 'annotation' tag in a NextProt XML file
 *
 * @author Pablo Cingolani
 *
 */
public class NextProtXmlAnnotation extends NextProtXmlNode {

	CvTerm cvTerm;
	String description;
	NextProtXmlEntry entry;
	String category; // Annotation category
	List<Location> locations; // Locations associated with current annotation
	Location location; // Current location

	public NextProtXmlAnnotation(NextProtXmlEntry entry, String category) {
		super(null);
		this.entry = entry;
		this.category = category;
		init();
	}

	public String getCategory() {
		return category;
	}

	public CvTerm getCvTerm() {
		return cvTerm;
	}

	public String getDescription() {
		return description;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public boolean hasCvTerm() {
		return this.cvTerm != null;
	}

	void init() {
		switch (category) {
		// Ignore these types of annotations
		case "antibody-mapping":
		case "beta-strand":
		case "calcium-binding-region":
		case "coiled-coil-region":
		case "compositionally-biased-region":
		case "cross-link":
			// case "disulfide-bond": // TODO: Add the one base ones?
		case "dna-binding-region":
		case "domain":
		case "expression-info":
		case "expression-profile":
		case "function-info":
		case "go-biological-process":
		case "go-molecular-function":
		case "go-cellular-component":
		case "helix":
		case "interacting-region":
		case "initiator-methionine":
		case "interaction-info":
		case "interaction-mapping":
		case "intramembrane-region":
		case "mature-protein":
		case "miscellaneous-region":
		case "miscellaneous-site":
		case "mitochondrial-transit-peptide":
		case "mutagenesis":
		case "non-terminal-residue":
		case "nucleotide-phosphate-binding-region":
		case "pathway":
		case "pdb-mapping":
		case "peptide-mapping":
		case "peroxisome-transit-peptide":
		case "propeptide":
		case "repeat":
		case "sequence-conflict":
		case "short-sequence-motif":
		case "signal-peptide":
		case "srm-peptide-mapping":
		case "subcellular-location":
		case "topological-domain":
		case "transmembrane-region":
		case "turn":
		case "uniprot-keyword":
		case "variant":
		case "zinc-finger-region":
			break;

		// Only store locations for these types of annotations
		case "active-site":
		case "binding-site":
		case "cleavage-site":
		case "cysteines":
		case "disulfide-bond": // Note: Disulfide bonds are marked as start-end, even though they are not intervals
		case "glycosylation-site":
		case "lipidation-site":
		case "modified-residue":
		case "metal-binding-site":
		case "selenocysteine":
			locations = new ArrayList<>();
			break;

		default:
			entry.getHandler().countMissingCategory(category);
			locations = new ArrayList<>();
		}
	}

	public boolean isEmpty() {
		return locations == null || locations.isEmpty();
	}

	public void locationBeginPos(Attributes attributes) {
		if (location != null) location.begin = Gpr.parseIntSafe(attributes.getValue("position")) - 1; // Transform to zero-based
	}

	/**
	 * End of location tag
	 */
	public void locationEnd() {
		if (locations != null) {

			locations.add(location);
		}
		location = null;
	}

	public void locationEndPos(Attributes attributes) {
		if (location != null) location.end = Gpr.parseIntSafe(attributes.getValue("position")) - 1; // Transform to zero-based
	}

	public void locationIsoformStart(String accession) {
		location = new LocationTargetIsoform(accession);
	}

	public void locationStart(Attributes attributes) {
		if (location == null) {
			String type = attributes.getValue("type");
			location = new Location(type);
		}
	}

	public Markers markers(NextProtMarkerFactory markersFactory) {
		for (var l : locations) {
			// Note: This cast may not be always possible in future NextProt version, or when adding new categories
			var loc = (LocationTargetIsoform) l;

			// Get Isoform
			var iso = entry.getIsoform(loc.accession);
			if (iso == null) {
				Log.warning(ErrorWarningType.WARNING_TRANSCRIPT_NOT_FOUND, "Isoform '" + loc.accession + "' not found for entry '" + entry.getAccession() + "'");
				continue;
			}

			// Create markers
			for (var trId : iso.getTranscriptIds())
				markersFactory.markers(entry, iso, this, loc, trId);
		}
		return null;
	}

	/**
	 * Return an annotaion "name"
	 */
	public String name() {
		// Some annotations have control-vocabulary terms (e.g. "modified-residue")
		if (cvTerm != null) return category + ":" + cvTerm.description;

		// Some annotations do not have controlled vocabularies, but have a "desription" (e.g. "active-site")
		if (description != null) {
			// Sometimes a description can be split at ';'
			if (description.indexOf(';') > 0) return category + ":" + description.split(";")[0];
			return category + ":" + description;
		}

		// No additional information, just use category
		return category;
	}

	public void setCvTerm(CvTerm cvTerm) {
		this.cvTerm = cvTerm;
	}

	public void setDescription(String description) {
		if (this.description == null) this.description = description;
	}

	@Override
	public String toString() {
		return toString("");

	}

	public String toString(String prefix) {
		var sb = new StringBuilder();
		sb.append("Annotation '" + name() + "'\n");
		sb.append("\tDescription: " + description + "\n");
		sb.append("\t" + cvTerm + "\n");
		if (locations != null) {
			for (Location l : locations)
				sb.append(prefix + "\t" + l + "\n");
		}
		return sb.toString();
	}
}
