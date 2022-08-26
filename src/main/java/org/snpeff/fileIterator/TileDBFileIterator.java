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

import io.tiledb.libvcfnative.VCFReader;
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


//        filter_ids: Attr<filter_ids,TILEDB_INT32,VAR>
//        end_pos: Attr<end_pos,TILEDB_UINT32,1>
//        real_start_pos: Attr<real_start_pos,TILEDB_UINT32,1>
//        qual: Attr<qual,TILEDB_FLOAT32,1>
//        fmt_DP: Attr<fmt_DP,TILEDB_UINT8,VAR>
//        fmt_PL: Attr<fmt_PL,TILEDB_UINT8,VAR>
//        id: Attr<id,TILEDB_CHAR,VAR>
//        fmt_GT: Attr<fmt_GT,TILEDB_UINT8,VAR>
//        fmt: Attr<fmt,TILEDB_UINT8,VAR>
//        alleles: Attr<alleles,TILEDB_CHAR,VAR>
//        info: Attr<info,TILEDB_UINT8,VAR>
        for (String name: schema.getAttributes().keySet()) {
            String key = name.toString();
            String value = schema.getAttributes().get(name).toString();
            System.out.println(key + ": " + value);
        }

        Path VCFuri = Paths.get("examples","hg002_tiledb");
        String VCFUriString =  "file://".concat(arraysPath.toAbsolutePath().toString());
        VCFReader myVCFReader = new VCFReader(VCFUriString, null, null, null); //, String[] samples);, Optional< URI > samplesURI, Optional<String> config) {

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