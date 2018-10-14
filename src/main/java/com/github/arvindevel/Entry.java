package com.github.arvindevel;

import java.nio.ByteBuffer;

/**
 * Entry used to represent the key and value
 */
public class Entry implements Comparable<Entry> {
    final byte[] keySize;
    final byte[] key;
    final byte[] valueSize;
    final byte[] value;

    public Entry(byte[] keySize, byte[] key, byte[] valueSize, byte[] value) {
        this.keySize = keySize;
        this.key = key;
        this.value = value;
        this.valueSize = valueSize;
    }

    public Entry(String key, String value) {
        this.keySize = toBytes(key.getBytes().length);
        this.key = key.getBytes();
        this.valueSize = toBytes(value.getBytes().length);
        this.value = value.getBytes();
    }

    byte[] toBytes() {
        byte[] totalBytes = new byte[toInt(keySize) + toInt(valueSize) + 8];
        System.arraycopy(keySize, 0, totalBytes, 0, 4);
        System.arraycopy(key, 0, totalBytes, 4, ByteBuffer.wrap(keySize).getInt());
        System.arraycopy(valueSize, 0, totalBytes, ByteBuffer.wrap(keySize).getInt() + 4, 4);
        System.arraycopy(value, 0, totalBytes, ByteBuffer.wrap(keySize).getInt() + 8, ByteBuffer.wrap(valueSize).getInt());
        return totalBytes;
    }

    /**
     * Convert a int number to a bytes array.
     *
     * @param value the int number
     * @return the bytes array
     */
    public static byte[] toBytes(int value) {
        byte[] memory = new byte[4];
        memory[0] = (byte) (value >>> 24);
        memory[1] = (byte) (value >>> 16);
        memory[2] = (byte) (value >>> 8);
        memory[3] = (byte) value;
        return memory;
    }

    /**
     * Convert a bytes array to a int number.
     *
     * @param memory bytes array
     * @return value the int number
     */
    public static int toInt(byte[] memory) {
        return ((int) memory[0] & 0xff) << 24
                | ((int) memory[1] & 0xff) << 16
                | ((int) memory[2] & 0xff) << 8
                | ((int) memory[3] & 0xff);
    }

    @Override
    public String toString() {
        return new String(key) + ":" + new String(value);
    }

    public static int compare(Entry entry1, Entry entry2) {
        return compare(entry1.key, entry2.key);
    }

    public static int compare(byte[] key1, byte[] key2) {
        if (key1.length < key2.length) {
            return -1;
        } else if (key1.length > key2.length) {
            return 1;
        } else {
            for (int i = 0; i < key1.length; i++) {
                if (key1[i] < key2[i]) {
                    return -1;
                } else if (key1[i] > key2[i]) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Entry o) {
        return compare(key, o.key);
    }
}
