package com.github.arvindevel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * ExternalSort used to sort {@link Entry}.
 */
public class ExternalSort {
    // TODO: add other policy, such as size-based, because num maybe not adequate
    private static int memSortEntryNum = 10000;

    public static Iterator<Entry> sort(List<String> sortedChunkFiles) throws Exception {
        int size = sortedChunkFiles.size();
        List<Iterator<Entry>> inputs = new ArrayList<>(size);
        for (String path : sortedChunkFiles) {
            inputs.add(EntryStream.readValues(new FileInputStream(path)));
        }
        Entry[] heads = new Entry[size];
        return new Iterator<Entry>() {
            boolean flag = false;
            int currentMinIndex = 0;
            Entry currentEntry = null;

            @Override
            public boolean hasNext() {
                if (!flag) {
                    for (int i = 0; i < size; i++) {
                        if (heads[i] == null) {
                            if (inputs.get(i).hasNext()) {
                                flag = true;
                                heads[i] = inputs.get(i).next();
                            }
                        }
                    }
                }
                return flag;
            }

            @Override
            public Entry next() {
                currentEntry = null;
                for (int i = 0; i < size; i++) {
                    if (heads[i] != null) {
                        currentEntry = heads[i];
                        break;
                    }
                }

                // find min
                for (int i = 0; i < size; i++) {
                    if (heads[i] != null) {
                        if (Entry.compare(currentEntry, heads[i]) > 0) {
                            currentEntry = heads[i];
                            currentMinIndex = i;
                        }
                    }
                }

                // min index data update
                if (inputs.get(currentMinIndex).hasNext()) {
                    heads[currentMinIndex] = inputs.get(currentMinIndex).next();
                } else {
                    flag = false;
                }

                return currentEntry;
            }
        };
    }

    public static Iterator<Entry> sort(String unsortedInputFile) throws Exception {
        List<String> sortedChunkFiles = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        Entry[] tmp;
        int id = 0;
        int fileId = 0;
        Iterator<Entry> iterator = EntryStream.readValues(new FileInputStream(unsortedInputFile));
        Comparator<Entry> comparator = new Comparator<Entry>() {
            public int compare(Entry entry1, Entry entry2) {
                return Entry.compare(entry1, entry2);
            }
        };
        while (iterator.hasNext()) {
            if (id < memSortEntryNum) {
                entries.add(iterator.next());
                id++;
            } else {
                tmp = new Entry[entries.size()];
                entries.toArray(tmp);
                Arrays.parallelSort(tmp, comparator);
                // output sorted entry
                OutputStream outputStream = new FileOutputStream(new File("/tmp/output" + fileId));
                for (Entry entry : tmp) {
                    outputStream.write(entry.toBytes());
                }
                outputStream.flush();
                outputStream.close();
                sortedChunkFiles.add("/tmp/output" + fileId);
                fileId++;

                // reset
                entries.clear();
                entries.add(iterator.next());
                id = 1;
            }
        }
        // deal last one
        if (entries.size() > 0) {
            tmp = new Entry[entries.size()];
            entries.toArray(tmp);
            Arrays.parallelSort(tmp, comparator);
            // output sorted entry
            OutputStream outputStream = new FileOutputStream(new File("/tmp/output" + fileId));
            for (Entry entry : tmp) {
                outputStream.write(entry.toBytes());
            }
            outputStream.flush();
            outputStream.close();
            sortedChunkFiles.add("/tmp/output" + fileId);
        }
        return sort(sortedChunkFiles);
    }
}
