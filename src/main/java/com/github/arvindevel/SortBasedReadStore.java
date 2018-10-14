package com.github.arvindevel;

import com.sun.tools.javac.util.Pair;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class SortBasedReadStore extends AbstractReadStore {

    private final int chunkNum = 1;
    private final String suffix = "_chunk";
    // TODO: size based
    private final int entryIndexNum = 1000;
    // entry:fileOffset map
    private final Entry[][] memEntries;
    private final Long[][] memEntryPositions;
    private final String[] sortedFile = new String[chunkNum];
    private final String[] unsortedFile = new String[chunkNum];

    public SortBasedReadStore(String filePath) throws Exception {
        super(filePath);
        memEntries = new Entry[chunkNum][];
        memEntryPositions = new Long[chunkNum][];
        for (int i = 0; i < chunkNum; i++) {
            memEntries[i] = new Entry[entryIndexNum];
            memEntryPositions[i] = new Long[entryIndexNum];
        }
        constructStore();
    }

    // binary search used to search in the mem index
    Entry getFromMem(byte[] key, int threadId) {
        int left = 0, right = memEntries[threadId].length;

        long scanPosition = 0;
        while (left < right) {
            int middle = left + (right - left) / 2;
            if (Entry.compare(memEntries[threadId][middle].key, key) == 0) {
                return memEntries[threadId][middle];
            } else if (Entry.compare(memEntries[threadId][middle].key, key) < 0) {
                left = middle + 1;
                scanPosition = memEntryPositions[threadId][middle];
            } else if (Entry.compare(memEntries[threadId][middle].key, key) > 0) {
                right = middle - 1;
            }
        }
        // scan from disk
        return scanFromDisk(key, threadId, scanPosition);
    }

    Entry scanFromDisk(byte[] key, int threadId, long scanPosition) {
        System.out.println(scanPosition);
        try {
            FileInputStream fileInputStream = new FileInputStream(sortedFile[threadId]);
            fileInputStream.skip(scanPosition);
            Iterator<Entry> iterator = EntryStream.readValues(fileInputStream);
            while (iterator.hasNext()) {
                Entry entry = iterator.next();
                if (Entry.compare(entry.key, key) == 0) {
                    return entry;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    // split the original file to 8 small file and sort, and use 8 thread to query respectively
    void constructStore() throws Exception {
        splitFile();

        // external sort and output
        for (int i = 0; i < chunkNum; i++) {
            int entriesInChunkNum = 0;
            File file = File.createTempFile(i + "sorted", suffix);
            OutputStream outputStream = new FileOutputStream(file);
            sortedFile[i] = file.getPath();
            Iterator<Entry> iterator = ExternalSort.sort(unsortedFile[i]);
            while (iterator.hasNext()) {
                outputStream.write(iterator.next().toBytes());
                entriesInChunkNum++;
            }
            outputStream.flush();
            outputStream.close();

            // construct index in mem
            int interval = entriesInChunkNum / entryIndexNum;
            if (interval < 1) {
                interval = 1;
            }

            Iterator<Pair<Entry, Long>> iter = EntryStream.readValueAndPositions(new FileInputStream(sortedFile[i]));
            int counter = 1;
            int arrayCounter = 0;
            while (iter.hasNext()) {
                if (counter == interval) {
                    Pair<Entry, Long> entryLongPair = iter.next();
                    memEntries[i][arrayCounter] = entryLongPair.fst;
                    memEntryPositions[i][arrayCounter] = entryLongPair.snd;
                    counter = 1;
                    arrayCounter++;
                } else {
                    counter++;
                    iter.next();
                }
            }
        }
    }


    // split the one big file into smaller file
    void splitFile() throws Exception {
        RandomAccessFile raf;
        long sourceSize;
        try {
            raf = new RandomAccessFile(filePath, "r");
            sourceSize = raf.length();
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("file not found error");
        } catch (IOException ioe) {
            throw new RuntimeException("file io error");
        }

        long bytesPerSplit = sourceSize / chunkNum;

        // key/value size buffer
        byte[] size = new byte[4];

        for (int destIx = 0; destIx < chunkNum; destIx++) {
            File file = File.createTempFile(destIx + "unsorted", suffix);
            unsortedFile[destIx] = file.getPath();
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(file));
            long currentSize = 0;
            while (currentSize < bytesPerSplit) {
                // key size, assuming 4 byte
                int val = raf.read(size);
                // end of file
                if (val != 4) {
                    break;
                } else {
                    currentSize += ByteBuffer.wrap(size).getInt();
                    byte[] keyContent = new byte[ByteBuffer.wrap(size).getInt()];
                    raf.read(keyContent);
                    // write key size and key content
                    bw.write(size);
                    bw.write(keyContent);

                    // write value size, value content
                    raf.read(size);
                    currentSize += ByteBuffer.wrap(size).getInt();
                    byte[] valueContent = new byte[ByteBuffer.wrap(size).getInt()];
                    raf.read(valueContent);
                    bw.write(size);
                    bw.write(valueContent);

                    // two size space
                    currentSize += 8;
                }
            }
            bw.flush();
            bw.close();
        }
        raf.close();
    }

    byte[] read(byte[] key) {
        return getFromMem(key, 0).value;
    }

    // read from memIndices first, if not found, then read disk, use 8 threads to do parallel
}
