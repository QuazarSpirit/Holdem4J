package org.quazarspirit.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {
    public static Class<?> getCallerClass() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String clazzName = stackTrace[4].getClassName();
        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static boolean isTesting() {
        return (System.getProperty("TEST") != null && System.getProperty("TEST").equals("true"));
    }

    public static class CircularArrayList<E> extends ArrayList<E> {
        public CircularArrayList(ArrayList<E> arrayList) {
            super(arrayList);
        }

        public CircularArrayList() {
            super();
        }
        public E get(int index) {
            int i = index % size();
            if (i == -1) {
                i = size() - 1;
            }

            return super.get(i);
        }
    }
}
