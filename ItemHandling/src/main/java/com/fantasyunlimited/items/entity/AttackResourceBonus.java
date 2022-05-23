package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class AttackResourceBonus implements Serializable {
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
