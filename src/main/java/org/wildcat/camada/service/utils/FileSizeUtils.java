package org.wildcat.camada.service.utils;

public class FileSizeUtils {

    private final static String[] fileSizeUnits = {"bytes", "Kb", "Mb", "Gb", "Tb", "Pb", "Eb", "Zb", "Yb"};

    public static String getFormattedBytes(long noOfBytes) {
        double bytes = noOfBytes;
        int index;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        return String.format("%.2f", bytes) + " " + fileSizeUnits[index];
    }
}
