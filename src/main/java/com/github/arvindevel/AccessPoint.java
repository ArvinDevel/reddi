package com.github.arvindevel;

import java.io.File;
import java.util.Scanner;

public class AccessPoint {
    private static AbstractReadStore readStore;

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException("Require file path as first argument");
        }
        String filePath = args[0];
        File file = new File(filePath);
        if (! file.exists()) {
            throw new RuntimeException("File path is not valid");
        }

        if (args.length ==2 ) {
            if("0".equals(args[1])) {

            } else {

            }
        }

        Scanner scanner = new Scanner(System.in);
        // loop to response query, assume the key is string
        String key;
        while (true) {
            key = scanner.next();
            System.out.println(new String(readStore.read(key.getBytes())));
        }
    }
}
