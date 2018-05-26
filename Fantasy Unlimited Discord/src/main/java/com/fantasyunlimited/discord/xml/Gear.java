package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public abstract class Gear extends RarityClassifiedItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7619515846737944068L;
	private List<CombatSkillBonus> skillBonuses = new ArrayList<>();
	private List<AttributeBonus> attributeBonuses = new ArrayList<>();
	private List<SecondarySkill> secondarySkillBonuses = new ArrayList<>();
	private List<AttackResourceBonus> atkResourceBonuses = new ArrayList<>();

	private String classExclusive;
	private String raceExclusive;

	public List<CombatSkillBonus> getSkillBonuses() {
		return skillBonuses;
	}

	public void setSkillBonuses(List<CombatSkillBonus> skillBonuses) {
		this.skillBonuses = skillBonuses;
	}

	public List<AttributeBonus> getAttributeBonuses() {
		return attributeBonuses;
	}

	public void setAttributeBonuses(List<AttributeBonus> attributeBonuses) {
		this.attributeBonuses = attributeBonuses;
	}

	public List<SecondarySkill> getSecondarySkillBonuses() {
		return secondarySkillBonuses;
	}

	public void setSecondarySkillBonuses(List<SecondarySkill> secondarySkillBonuses) {
		this.secondarySkillBonuses = secondarySkillBonuses;
	}

	public List<AttackResourceBonus> getAtkResourceBonuses() {
		return atkResourceBonuses;
	}

	public void setAtkResourceBonuses(List<AttackResourceBonus> atkResourceBonuses) {
		this.atkResourceBonuses = atkResourceBonuses;
	}

	public String getClassExclusive() {
		return classExclusive;
	}

	public void setClassExclusive(String classExclusive) {
		this.classExclusive = classExclusive;
	}

	public String getRaceExclusive() {
		return raceExclusive;
	}

	public void setRaceExclusive(String raceExclusive) {
		this.raceExclusive = raceExclusive;
	}

}
