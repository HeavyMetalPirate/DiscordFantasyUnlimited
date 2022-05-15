package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public enum SecondarySkill implements Serializable {
	WOODCUTTING("secondary.skills.woodcutting"),
	FISHING("secondary.skills.fishing"),
	MINING("secondary.skills.mining"),
	ALCHEMY("secondary.skills.alchemy"),
	ENCHANTING("secondary.skills.enchanting");
	
	private final String value;
	private SecondarySkill(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
