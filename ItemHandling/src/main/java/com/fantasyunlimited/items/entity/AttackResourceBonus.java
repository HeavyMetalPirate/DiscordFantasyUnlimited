package com.fantasyunlimited.items.entity;

public class AttackResourceBonus {
	private CharacterClass.EnergyType energyType;
	private int bonus;
	public CharacterClass.EnergyType getSkill() {
		return energyType;
	}
	public void setSkill(CharacterClass.EnergyType energyType) {
		this.energyType = energyType;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
