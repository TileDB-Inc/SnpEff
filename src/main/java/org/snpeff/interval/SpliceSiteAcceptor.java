package org.snpeff.interval;

import org.snpeff.snpEffect.EffectType;

/**
 * Interval for a splice site acceptor
 * <p>
 * Note: Splice sites donnor are defined as the last 2 bases of an intron
 * Reference: http://en.wikipedia.org/wiki/RNA_splicing
 *
 * @author pcingola
 */
public class SpliceSiteAcceptor extends SpliceSite {

    private static final long serialVersionUID = -7416687954435361328L;

    public SpliceSiteAcceptor() {
        super();
        type = EffectType.SPLICE_SITE_ACCEPTOR;
    }

    public SpliceSiteAcceptor(Intron parent, int start, int end, boolean strandMinus, String id) {
        super(parent, start, end, strandMinus, id);
        type = EffectType.SPLICE_SITE_ACCEPTOR;
    }

    @Override
    public boolean intersectsCoreSpliceSite(Marker marker) {
        if (size() <= CORE_SPLICE_SITE_SIZE) return true;

        if (!getChromosomeName().equals(marker.getChromosomeName())) return false; // Not in the same chromosome? They do not intersect

        int coreStart, coreEnd;
        if (isStrandPlus()) {
            coreEnd = getEndClosed();
            coreStart = coreEnd - CORE_SPLICE_SITE_SIZE + 1;
        } else {
            coreStart = getStart();
            coreEnd = coreStart + CORE_SPLICE_SITE_SIZE - 1;
        }

        return marker.intersects(coreStart, coreEnd);
    }
}
