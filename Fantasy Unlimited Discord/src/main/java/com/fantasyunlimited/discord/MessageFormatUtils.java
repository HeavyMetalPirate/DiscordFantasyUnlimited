package com.fantasyunlimited.discord;

public class MessageFormatUtils {
	public static String fillStringPrefix(String string, int totalLength) {
		return String.format("%1$-" + (totalLength - string.length()) + "s", string);
	}
	public static String fillStringSuffix(String string, int totalLength) {
		return String.format("%1$" + (totalLength - string.length()) + "s", string);
	}
	
}
