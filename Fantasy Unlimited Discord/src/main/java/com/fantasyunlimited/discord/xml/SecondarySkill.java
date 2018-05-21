package com.fantasyunlimited.discord.xml;

import java.io.Serializable;

public enum SecondarySkill implements Serializable {
	WOODCUTTING("Woodcutting"),
	FISHING("Fishing"),
	MINING("Mining"),
	ALCHEMY("Alchemy"),
	ENCHANTING("Enchanting");
	
	private final String value;
	private SecondarySkill(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
