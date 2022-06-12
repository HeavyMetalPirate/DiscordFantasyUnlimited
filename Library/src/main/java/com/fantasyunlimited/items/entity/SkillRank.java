package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class SkillRank implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5631369788392391235L;
	private int rank;
	private int damageModifier;
	private int costModifier;
	private int requiredAttributeValue;
	private int requiredPlayerLevel;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getDamageModifier() {
		return damageModifier;
	}

	public void setDamageModifier(int damageModifier) {
		this.damageModifier = damageModifier;
	}

	public int getCostModifier() {
		return costModifier;
	}

	public void setCostModifier(int costModifier) {
		this.costModifier = costModifier;
	}

	public int getRequiredAttributeValue() {
		return requiredAttributeValue;
	}

	public void setRequiredAttributeValue(int requiredAttributeValue) {
		this.requiredAttributeValue = requiredAttributeValue;
	}

	public int getRequiredPlayerLevel() {
		return requiredPlayerLevel;
	}

	public void setRequiredPlayerLevel(int requiredPlayerLevel) {
		this.requiredPlayerLevel = requiredPlayerLevel;
	}
}
