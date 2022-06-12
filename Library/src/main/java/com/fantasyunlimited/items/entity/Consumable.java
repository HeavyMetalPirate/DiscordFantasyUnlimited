package com.fantasyunlimited.items.entity;

import java.util.ArrayList;
import java.util.List;

public class Consumable extends RarityClassifiedItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1702797342795811772L;

	private boolean duringBattle;
	private boolean fromInventory;
	private List<CombatSkillBonus> combatSkillModifiers = new ArrayList<>();
	private List<AttributeBonus> attributeModifiers = new ArrayList<>();
	private int durationRounds;
	private int healthRestored;
	private int atkResourceRestored;
	private EnergyType resourceType;
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

	public EnergyType getResourceType() {
		return resourceType;
	}

	public void setResourceType(EnergyType resourceType) {
		this.resourceType = resourceType;
	}

	public int getDurationRounds() {
		return durationRounds;
	}

	public void setDurationRounds(int durationRounds) {
		this.durationRounds = durationRounds;
	}

	public boolean isFromInventory() {
		return fromInventory;
	}

	public void setFromInventory(boolean fromInventory) {
		this.fromInventory = fromInventory;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
