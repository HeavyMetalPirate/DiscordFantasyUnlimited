package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class AttributeBonus implements Serializable {
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
