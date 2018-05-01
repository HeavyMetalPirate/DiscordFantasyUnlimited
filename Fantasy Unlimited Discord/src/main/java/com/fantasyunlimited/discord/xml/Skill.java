package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

import com.fantasyunlimited.discord.xml.Attributes.Attribute;

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
	
	private Attribute buffModifiesAttribute;
	private int buffModifier;
	private int durationInTurns;
		
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
	public Attribute getBuffModifiesAttribute() {
		return buffModifiesAttribute;
	}
	public void setBuffModifiesAttribute(Attribute buffModifiesAttribute) {
		this.buffModifiesAttribute = buffModifiesAttribute;
	}
	public int getBuffModifier() {
		return buffModifier;
	}
	public void setBuffModifier(int buffModifier) {
		this.buffModifier = buffModifier;
	}
	public int getDurationInTurns() {
		return durationInTurns;
	}
	public void setDurationInTurns(int durationInTurns) {
		this.durationInTurns = durationInTurns;
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



	public enum SkillWeaponModifier {
		WEAPON_MAINHAND,
		WEAPON_OFFHAND,
		NONE
	}
	public enum SkillType {
		OFFENSIVE,
		DEFENSIVE,
		BUFF,
		DEBUFF
	}
	public enum TargetType {
		ENEMY, //only opponents
		FRIEND, //only friends plus self
		OWN, //only self
		ANY, //anyone
		AREA //area of effect, all opponents at the same time
	}
}
