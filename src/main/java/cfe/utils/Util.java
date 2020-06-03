package cfe.utils;

import java.text.NumberFormat;

public class Util {
	
	public static long getFreeMemoryBytes() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        return freeMemory;
	}
	
	public static String getFreeMemory() {
		long freeMem = Util.getFreeMemoryBytes();
		NumberFormat format = NumberFormat.getInstance();
        String freeMemory = "Free memory: " + format.format(freeMem) + " bytes";
        return freeMemory;
	}
}
