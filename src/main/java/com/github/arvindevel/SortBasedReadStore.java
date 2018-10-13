package com.github.arvindevel;

public class SortBasedReadStore extends AbstractReadStore {

    public SortBasedReadStore(String filePath) {
        super(filePath);
    }

    void constructStore() {

    }

    byte[] read(byte[] key) {
        return new byte[0];
    }
}
