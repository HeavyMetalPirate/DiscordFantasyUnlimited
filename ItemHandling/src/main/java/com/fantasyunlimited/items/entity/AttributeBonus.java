package com.fantasyunlimited.items.entity;

public class AttributeBonus {
	private Attributes.Attribute attribute;
	private int bonus;
	public Attributes.Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attributes.Attribute attribute) {
		this.attribute = attribute;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
