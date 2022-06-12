package com.fantasyunlimited.items.entity;

public class Equipment extends Gear {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1702797342795811772L;

	private EquipmentType type;

	private int armor;

	public EquipmentType getType() {
		return type;
	}

	public void setType(EquipmentType type) {
		this.type = type;
	}

	public int getArmor() {
		return armor;
	}

	public void setArmor(int armor) {
		this.armor = armor;
	}
}
