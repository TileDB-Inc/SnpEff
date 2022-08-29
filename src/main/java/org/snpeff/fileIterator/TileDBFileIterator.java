package org.snpeff.fileIterator;

import static io.tiledb.java.api.Datatype.*;
import static io.tiledb.java.api.Layout.TILEDB_ROW_MAJOR;
import static io.tiledb.java.api.Layout.TILEDB_UNORDERED;
import static io.tiledb.java.api.QueryType.TILEDB_READ;
import static io.tiledb.java.api.QueryType.TILEDB_WRITE;

import io.tiledb.java.api.TileDBError;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.commons.io.FileUtils;

import static io.tiledb.java.api.Layout.TILEDB_GLOBAL_ORDER;
import static io.tiledb.java.api.QueryType.TILEDB_READ;

import io.tiledb.java.api.*;

//giving me trouble
//import io.tiledb.libvcfnative.VCFReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class TileDBFileIterator {
    public static void main(String[] args) throws TileDBError, IOException {

        Context ctx = new Context();
        //Path arraysPath = Paths.get("src","test","resources","data","1.6","quickstart_sparse_array");
        //Path arraysPath = Paths.get("src","test","resources","data","ingested_2samples","data");
        Path arraysPath = Paths.get("examples","hg002_tiledb","data");
        String arrayString =  "file://".concat(arraysPath.toAbsolutePath().toString());

        System.out.println(arrayString);
        Array my_sparse_array = new Array(ctx, arrayString, TILEDB_READ);

        ArraySchema schema = my_sparse_array.getSchema();



        for (String name: schema.getAttributes().keySet()) {
            String key = name.toString();
            String value = schema.getAttributes().get(name).toString();
            System.out.println(key + ": " + value);
        }
        Context mycontext = my_sparse_array.getCtx();
        System.out.println(mycontext.toString());


        //NativeArray subarray = new NativeArray(ctx, new int[] {1, 4, 1, 4}, Integer.class);
        Query query = new Query(my_sparse_array, TILEDB_READ);
        query.setLayout(TILEDB_ROW_MAJOR);

        HashMap<String, Pair<Long, Long>> max_sizes = query.getResultEstimations();
        for (Map.Entry<String, Pair<Long, Long>> e : max_sizes.entrySet()) {
            System.out.println(
                    e.getKey() + " (" + e.getValue().getFirst() + ", " + e.getValue().getSecond() + ")");
        }

        //4 public signatures
        //NativeArray(Context ctx, int size, Datatype nativeType)
        //NativeArray(Context ctx, int size, Class javaType)
        //NativeArray(Context ctx, Object buffer, Class javaType)
        //NativeArray(Context ctx, Object buffer, Datatype nativeType)

        //Exception in thread "main" io.tiledb.java.api.TileDBError: [TileDB::Query] Error: Var-Sized input attribute/dimension 'alleles' is not set correctly.


        //https://github.com/TileDB-Inc/TileDB-Java/blob/master/src/main/java/examples/io/tiledb/java/api/SparseReadGlobal.java#L74-L77
        //In line 74: before we submit the READ query and because a2 is a var length attribute we need to initialize two
        // empty buffers for the query to hold the values after submission. The first buffer is used to hold the offset
        // uint64 values and the second buffer is used to hold the actual attribute data.

//        filter_ids: Attr<filter_ids,TILEDB_INT32,VAR>
//        end_pos: Attr<end_pos,TILEDB_UINT32,1>
//                real_start_pos: Attr<real_start_pos,TILEDB_UINT32,1>
//                qual: Attr<qual,TILEDB_FLOAT32,1>
//                id: Attr<id,TILEDB_STRING_ASCII,VAR>
//        fmt: Attr<fmt,TILEDB_UINT8,VAR>
//        alleles: Attr<alleles,TILEDB_STRING_ASCII,VAR>
//        info: Attr<info,TILEDB_UINT8,VAR>
        query.setBuffer(
                "filter_ids",
                new NativeArray(ctx, max_sizes.get("filter_ids").getFirst().intValue(), Datatype.TILEDB_UINT64),
                new NativeArray(ctx, max_sizes.get("filter_ids").getSecond().intValue(), Integer.class));

        query.setBuffer(
                "alleles",
                new NativeArray(ctx, max_sizes.get("alleles").getFirst().intValue(), Datatype.TILEDB_UINT64),
                new NativeArray(ctx, max_sizes.get("alleles").getSecond().intValue(), String.class));

        //Datatype.TILEDB_UINT64
        query.submit();

        int[] filter_ids_buffer = (int[]) query.getBuffer("filter_ids");
        long[] filter_ids_offsets = (long[]) query.getVarBuffer("filter_ids");

        byte[] allele_buffer = (byte[]) query.getBuffer("alleles");
        long[] allele_offsets = (long[]) query.getVarBuffer("alleles");

        //System.out.println("allele_buffer: " + allele_buffer.length);
        //System.out.println("allele_buffer: " + Arrays.toString(allele_buffer));
        System.out.println("allele_buffer: " + Arrays.toString(allele_buffer));
        //String.format("%11s", new String(Arrays.copyOfRange(allele_buffer, (int) allele_offsets[i], end)))


        int[] rows = (int[]) query.getBuffer("end_pos");
        int[] cols = (int[]) query.getBuffer("cols");

        for (int i = 0; i < a1_buff.length; i++) {
            int end = (i == a1_buff.length - 1) ? a2_data.length : (int) a2_offsets[i + 1];
            System.out.println(
                    String.format("%8s", "(" + d1_buff[2 * i] + ", " + d2_buff[2 * i] + ")")
                            + String.format("%9s", a1_buff[i])
                            + String.format(
                            "%11s", new String(Arrays.copyOfRange(a2_data, (int) a2_offsets[i], end)))
                            + String.format("%11s", a3_buff[2 * i])
                            + String.format("%10s", a3_buff[2 * i + 1]));
        }


        //Path VCFuri = Paths.get("examples","hg002_tiledb");
        //String VCFUriString =  "file://".concat(arraysPath.toAbsolutePath().toString());
        //VCFReader myVCFReader = new VCFReader(VCFUriString, null, null, null); //, String[] samples);, Optional< URI > samplesURI, Optional<String> config) {

        //query.setLayout(TILEDB_ROW_MAJOR);
    }
    public void readTileDBfile() throws TileDBError, IOException {

        // move old array to new temp folder for the upgrade.
        String source = "examples/ash";
        File srcDir = new File(source);

        Context ctx = new Context();
        Array my_sparse_array = new Array(ctx, "examples/tiledb_1sample/");
        HashMap<String, Pair> dom = my_sparse_array.nonEmptyDomain();
        for (Map.Entry<String, Pair> e : dom.entrySet()) {
            System.out.println(
                    e.getKey() + ": (" + e.getValue().getFirst() + ", " + e.getValue().getSecond() + ")");
        }
    }
}