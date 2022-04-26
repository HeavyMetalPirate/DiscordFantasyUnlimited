package com.fantasyunlimited.items.entity;

public abstract class Dropable extends GenericItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4811187959940148290L;
	protected int value;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
