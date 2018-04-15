package com.fantasyunlimited.discord.xml;

import com.fantasyunlimited.discord.xml.Attributes.Attribute;

public class Skill {

	private String id;
	private String name;
	private String description;
	private String iconName;
	private String iconId;
	
	private Attribute attribute;
	private int playerLevelToUnlock;
	private int skillLevelToUnlock;
	private SkillType type;
	private TargetType targetType;
	
	private SkillWeaponModifier weaponModifier;
	private int minDamage;
	private int maxDamage;
	
	private Attribute buffModifiesAttribute;
	private int buffModifier;
	private int durationInTurns;
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public int getPlayerLevelToUnlock() {
		return playerLevelToUnlock;
	}
	public void setPlayerLevelToUnlock(int playerLevelToUnlock) {
		this.playerLevelToUnlock = playerLevelToUnlock;
	}
	public int getSkillLevelToUnlock() {
		return skillLevelToUnlock;
	}
	public void setSkillLevelToUnlock(int skillLevelToUnlock) {
		this.skillLevelToUnlock = skillLevelToUnlock;
	}
	public SkillType getType() {
		return type;
	}
	public void setType(SkillType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
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

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconId() {
		return iconId;
	}
	public void setIconId(String iconId) {
		this.iconId = iconId;
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
