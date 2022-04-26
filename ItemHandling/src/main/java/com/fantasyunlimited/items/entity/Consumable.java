package com.fantasyunlimited.items.entity;

import java.util.ArrayList;
import java.util.List;

public class Consumable extends RarityClassifiedItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1702797342795811772L;

	private boolean duringBattle;
	private List<CombatSkillBonus> combatSkillModifiers = new ArrayList<>();
	private List<AttributeBonus> attributeModifiers = new ArrayList<>();
	
	private int healthRestored;
	private int atkResourceRestored;
	public boolean isDuringBattle() {
		return duringBattle;
	}
	public void setDuringBattle(boolean duringBattle) {
		this.duringBattle = duringBattle;
	}
	public List<CombatSkillBonus> getCombatSkillModifiers() {
		return combatSkillModifiers;
	}
	public void setCombatSkillModifiers(List<CombatSkillBonus> combatSkillModifiers) {
		this.combatSkillModifiers = combatSkillModifiers;
	}
	public List<AttributeBonus> getAttributeModifiers() {
		return attributeModifiers;
	}
	public void setAttributeModifiers(List<AttributeBonus> attributeModifiers) {
		this.attributeModifiers = attributeModifiers;
	}
	public int getHealthRestored() {
		return healthRestored;
	}
	public void setHealthRestored(int healthRestored) {
		this.healthRestored = healthRestored;
	}
	public int getAtkResourceRestored() {
		return atkResourceRestored;
	}
	public void setAtkResourceRestored(int atkResourceRestored) {
		this.atkResourceRestored = atkResourceRestored;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
