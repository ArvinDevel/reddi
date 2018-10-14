package com.github.arvindevel;

import com.sun.tools.javac.util.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import static com.github.arvindevel.Entry.toInt;

/**
 * Output {@link Entry} from file as input.
 */
public class EntryStream {

    public static Iterator<Entry> readValues(InputStream inputStream) throws IOException {

        return new EntryIterator(inputStream);
    }

    public static Iterator<Pair<Entry,Long>> readValueAndPositions(InputStream inputStream) throws IOException {

        return new EntryPositionIterator(inputStream);
    }

    static class EntryIterator implements Iterator<Entry> {
        // key/value Size buffer
        private byte[] keySize = new byte[4];
        private byte[] valueSize = new byte[4];

        private InputStream inputStream;

        public EntryIterator(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public boolean hasNext() {
            try {
                // key keySize, assuming 4 byte
                int val = inputStream.read(keySize);
                // end of file
                if (val != 4) {
                    return false;
                } else {
                    return true;
                }
            } catch (IOException ioe) {

            }
            return false;
        }

        public Entry next() {
            try {
                byte[] keyContent = new byte[toInt(keySize)];
                inputStream.read(keyContent);

                // write value keySize, value content
                inputStream.read(valueSize);
                byte[] valueContent = new byte[toInt(valueSize)];
                inputStream.read(valueContent);
                System.out.println(toInt(keySize) + " : " + new String(keyContent));
                return new Entry(keySize, keyContent, valueSize, valueContent);
            } catch (IOException ioe) {

            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    static class EntryPositionIterator implements Iterator<Pair<Entry,Long>> {
        // key/value Size buffer
        private byte[] keySize = new byte[4];
        private byte[] valueSize = new byte[4];
        private long currentPosition = 0;
        private int lastLen = 0;

        private InputStream inputStream;

        public EntryPositionIterator(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public boolean hasNext() {
            try {
                // key keySize, assuming 4 byte
                int val = inputStream.read(keySize);
                // end of file
                if (val != 4) {
                    return false;
                } else {
                    currentPosition += lastLen;
                    return true;
                }
            } catch (IOException ioe) {

            }
            return false;
        }

        public Pair<Entry,Long> next() {
            try {
                byte[] keyContent = new byte[toInt(keySize)];
                inputStream.read(keyContent);

                // write value keySize, value content
                inputStream.read(valueSize);
                byte[] valueContent = new byte[toInt(valueSize)];
                inputStream.read(valueContent);
                lastLen = toInt(keySize) + toInt(valueSize) + 8;
//                System.out.println(toInt(keySize) + " : " + new String(keyContent));
                return new Pair<Entry,Long>(new Entry(keySize, keyContent, valueSize, valueContent), currentPosition);
            } catch (IOException ioe) {

            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
