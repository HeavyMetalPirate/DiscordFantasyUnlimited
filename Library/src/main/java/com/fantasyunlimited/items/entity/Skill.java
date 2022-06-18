package com.fantasyunlimited.items.entity;

import java.util.ArrayList;
import java.util.List;

import com.fantasyunlimited.items.entity.Attributes.Attribute;

public class Skill extends GenericItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5492717811895811050L;

	private String iconId;

	private Attribute attribute;
	private SkillType type;
	private TargetType targetType;

	private SkillWeaponModifier weaponModifier;
	private int minDamage;
	private int maxDamage;
	private int costOfExecution;
	private int preparationRounds;

	private List<SkillRank> ranks = new ArrayList<>();
	private List<SkillRequirement> requirements = new ArrayList<>();

	private List<StatusEffect> statusEffects = new ArrayList<>();

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public SkillType getType() {
		return type;
	}

	public void setType(SkillType type) {
		this.type = type;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public int getMinDamage() {
		return minDamage;
	}

	public void setMinDamage(int minDamage) {
		this.minDamage = minDamage;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public SkillWeaponModifier getWeaponModifier() {
		return weaponModifier;
	}

	public void setWeaponModifier(SkillWeaponModifier weaponModifier) {
		this.weaponModifier = weaponModifier;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public int getCostOfExecution() {
		return costOfExecution;
	}

	public void setCostOfExecution(int costOfExecution) {
		this.costOfExecution = costOfExecution;
	}

	public int getPreparationRounds() {
		return preparationRounds;
	}

	public void setPreparationRounds(int preparationRounds) {
		this.preparationRounds = preparationRounds;
	}

	public List<SkillRank> getRanks() {
		return ranks;
	}

	public void setRanks(List<SkillRank> ranks) {
		this.ranks = ranks;
	}

	public List<SkillRequirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<SkillRequirement> requirements) {
		this.requirements = requirements;
	}

	public List<StatusEffect> getStatusEffects() {
		return statusEffects;
	}

	public void setStatusEffects(List<StatusEffect> statusEffects) {
		this.statusEffects = statusEffects;
	}

	public enum SkillWeaponModifier {
		WEAPON_MAINHAND("Mainhand"), WEAPON_OFFHAND("Offhand"), NONE("None");

		private final String value;

		private SkillWeaponModifier(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}

	public enum SkillType {
		OFFENSIVE, DEFENSIVE
	}

	public enum TargetType {
		ENEMY, // only opponents
		FRIEND, // only friends plus self
		OWN, // only self
		AREA // area of effect, all opponents at the same time
	}
}
