package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class CombatSkillBonus extends AbstractStatus implements Serializable {

	private CombatSkill skill;
	public CombatSkill getSkill() {
		return skill;
	}
	public void setSkill(CombatSkill skill) {
		this.skill = skill;
	}
}
