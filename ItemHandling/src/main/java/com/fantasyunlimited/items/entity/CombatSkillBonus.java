package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class CombatSkillBonus implements Serializable {
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
