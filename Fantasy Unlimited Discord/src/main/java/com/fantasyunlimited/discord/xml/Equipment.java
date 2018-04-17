package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public class Equipment extends RarityClassifiedItem {
	
	private EquipmentType type;
	
	private int armor;
	
	private List<CombatSkillBonus> skillBonuses = new ArrayList<>();
	private List<AttributeBonus> attributeBonuses = new ArrayList<>();
	private List<SecondarySkill> secondarySkillBonuses = new ArrayList<>();

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
}
