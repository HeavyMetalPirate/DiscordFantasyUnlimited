package com.fantasyunlimited.discord;

import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.CombatSkill;

public class BattleStatus {
	
	private String statusName;
	private int roundsRemaining;
	private int healthchangePerRound;
	private int healthchangeOnEnd;
	private boolean incapacitated;
	
	private Attribute modifiedAttribute;
	private CombatSkill modifiedSkill;
	private int amountModifier;
	private ModifierType modifierType;

	public int getRoundsRemaining() {
		return roundsRemaining;
	}

	public void setRoundsRemaining(int roundsRemaining) {
		this.roundsRemaining = roundsRemaining;
	}

	public int getHealthchangePerRound() {
		return healthchangePerRound;
	}

	public void setHealthchangePerRound(int healthchangePerRound) {
		this.healthchangePerRound = healthchangePerRound;
	}

	public Attribute getModifiedAttribute() {
		return modifiedAttribute;
	}

	public void setModifiedAttribute(Attribute modifiedAttribute) {
		this.modifiedAttribute = modifiedAttribute;
	}

	public CombatSkill getModifiedSkill() {
		return modifiedSkill;
	}

	public void setModifiedSkill(CombatSkill modifiedSkill) {
		this.modifiedSkill = modifiedSkill;
	}

	public int getAmountModifier() {
		return amountModifier;
	}

	public void setAmountModifier(int amountModifier) {
		this.amountModifier = amountModifier;
	}

	public ModifierType getModifierType() {
		return modifierType;
	}

	public void setModifierType(ModifierType modifierType) {
		this.modifierType = modifierType;
	}

	public int getHealthchangeOnEnd() {
		return healthchangeOnEnd;
	}

	public void setHealthchangeOnEnd(int healthchangeOnEnd) {
		this.healthchangeOnEnd = healthchangeOnEnd;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public boolean isIncapacitated() {
		return incapacitated;
	}

	public void setIncapacitated(boolean incapacitated) {
		this.incapacitated = incapacitated;
	}

	public enum ModifierType {
		RAISE, LOWER;
	}
}
