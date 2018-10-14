package com.github.arvindevel;

public class TreeBasedReadStore extends AbstractReadStore {

    public TreeBasedReadStore(String filePath) throws Exception{
        super(filePath);
    }

    void constructStore() {

    }

    byte[] read(byte[] key) {
        return new byte[0];
    }
}
