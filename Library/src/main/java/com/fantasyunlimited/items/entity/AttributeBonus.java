package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class AttributeBonus extends AbstractStatus  implements Serializable {

	private Attributes.Attribute attribute;

	public Attributes.Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attributes.Attribute attribute) {
		this.attribute = attribute;
	}
}
