package com.fantasyunlimited.discord.xml;

public abstract class Dropable extends GenericItem {
	protected int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
