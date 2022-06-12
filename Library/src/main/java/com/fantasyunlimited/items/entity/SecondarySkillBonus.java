package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class SecondarySkillBonus implements Serializable {
	private SecondarySkill skill;
	private int bonus;
	public SecondarySkill getSkill() {
		return skill;
	}
	public void setSkill(SecondarySkill skill) {
		this.skill = skill;
	}
	public int getBonus() {
		return bonus;
	}
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
