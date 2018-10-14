package com.github.arvindevel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;
import org.junit.Test;

public class EntryTest {

    @Test
    public void generateFile() throws Exception {
        Random random = new Random(System.currentTimeMillis());
        File inputFile = new File("/tmp/input");
        inputFile.createNewFile();
        BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(inputFile));
        for (int i = 0; i < 1; i++) {
            bw.write(new Entry("test", "foo").toBytes());
        }
        bw.write(new Entry("sest", "foo").toBytes());
        bw.write(new Entry("aest", "foo").toBytes());
        bw.flush();
        bw.close();
    }
}