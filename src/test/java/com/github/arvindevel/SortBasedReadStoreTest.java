package com.github.arvindevel;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

public class SortBasedReadStoreTest {
    @Test
    public void testStore() throws Exception {
        File inputFile = new File("/tmp/input");
        inputFile.createNewFile();
        BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(inputFile));
        for (int i = 0; i < 10000; i++) {
            bw.write(new Entry((10000 - i) + "test", "foo").toBytes());
        }
        bw.flush();
        bw.close();

        SortBasedReadStore readStore = new SortBasedReadStore(inputFile.getPath());
        System.out.println(readStore.read((1000 + "test").getBytes()));
    }
}