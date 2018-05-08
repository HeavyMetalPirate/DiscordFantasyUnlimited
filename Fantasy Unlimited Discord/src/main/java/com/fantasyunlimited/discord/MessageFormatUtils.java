package com.fantasyunlimited.discord;

public class MessageFormatUtils {
	public static String fillStringSuffix(String string, int length) {
		return String.format("%1$-" + length + "s", string);
	}
	public static String fillStringPrefix(String string, int length) {
		return String.format("%1$" + length + "s", string);
	}
	
}
