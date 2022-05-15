package com.fantasyunlimited.items.entity;

public enum CombatSkill {
	DODGE("Dodge"),
	CRITICAL("Critical"),
	BLOCK("Block"),
	PARRY("Parry"),
	SPELLPOWER("Spellpower"),
	HEALPOWER("Healpower");
	
	private final String value;
	private CombatSkill(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
