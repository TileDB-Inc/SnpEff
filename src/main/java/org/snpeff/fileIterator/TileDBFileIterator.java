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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.apache.commons.io.FileUtils;

import static io.tiledb.java.api.Layout.TILEDB_GLOBAL_ORDER;
import static io.tiledb.java.api.QueryType.TILEDB_READ;

import io.tiledb.java.api.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class TileDBFileIterator {
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