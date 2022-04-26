package com.fantasyunlimited.items.entity;

public class CombatSkillBonus {
	private CombatSkill skill;
	private int bonus;
	public CombatSkill getSkill() {
		return skill;
	}
	public void setSkill(CombatSkill skill) {
		this.skill = skill;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
