package com.github.arvindevel;

public abstract class AbstractReadStore {
    String filePath;

    AbstractReadStore(String filePath) throws Exception{
        this.filePath = filePath;
//        constructStore();
    }
    abstract void constructStore() throws Exception;
    abstract byte[] read(byte[] key);
}
