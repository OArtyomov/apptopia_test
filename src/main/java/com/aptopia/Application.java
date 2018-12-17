package com.aptopia;

import java.io.IOException;
import java.text.ParseException;

public class Application {

    public static void main(String args[]) throws IOException, ParseException {
        String fileName1 = "C:\\Temp\\TestMergeBigFiles\\fileA";
        String fileName2 = "C:\\Temp\\TestMergeBigFiles\\fileB";
        String fileName3 = "C:\\Temp\\TestMergeBigFiles\\fileC";
        new FileMerger().mergeFiles(fileName1, fileName2, fileName3);
    }
}