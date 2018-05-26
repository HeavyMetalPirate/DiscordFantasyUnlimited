package com.fantasyunlimited.discord.xml;

import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;

public class AttackResourceBonus {
	private EnergyType energyType;
	private int bonus;
	public EnergyType getSkill() {
		return energyType;
	}
	public void setSkill(EnergyType energyType) {
		this.energyType = energyType;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
