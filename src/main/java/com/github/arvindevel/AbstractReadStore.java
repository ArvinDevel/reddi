package com.github.arvindevel;

public abstract class AbstractReadStore {
    String filePath;

    AbstractReadStore(String filePath) {
        this.filePath = filePath;
        constructStore();
    }
    abstract void constructStore();
    abstract byte[] read(byte[] key);
}
