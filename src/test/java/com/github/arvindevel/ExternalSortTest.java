package com.github.arvindevel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

public class ExternalSortTest {
    @Test
    public void testSort() throws Exception{
        File inputFile = new File("/tmp/input");
        inputFile.createNewFile();
        BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(inputFile));
        for (int i = 0; i < 100000; i++) {
            bw.write(new Entry((100000 - i) + "test", "foo").toBytes());
        }
        bw.flush();
        bw.close();

        Iterator<Entry> iterator = ExternalSort.sort("/tmp/input");
        if(iterator.hasNext()) {
            assertArrayEquals(iterator.next().toBytes(), new Entry(1 + "test", "foo").toBytes());
        }
    }
}
