package com.fantasyunlimited.discord.xml;

import com.fantasyunlimited.discord.xml.Attributes.Attribute;

public class AttributeBonus {
	private Attribute attribute;
	private int bonus;
	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
