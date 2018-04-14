package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public class Weapon {
	
	private String id;
	private String name;
	private String description;
	private String iconName;
	
	private WeaponType type;
	private Hand hand;
	
	private int minDamage;
	private int maxDamage;
	
	private List<CombatSkillBonus> skillBonuses = new ArrayList<>();
	private List<AttributeBonus> attributeBonuses = new ArrayList<>();
	private List<SecondarySkillBonus> secondarySkillBonuses = new ArrayList<>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public WeaponType getType() {
		return type;
	}

	public void setType(WeaponType type) {
		this.type = type;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
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

	public List<SecondarySkillBonus> getSecondarySkillBonuses() {
		return secondarySkillBonuses;
	}

	public void setSecondarySkillBonuses(List<SecondarySkillBonus> secondarySkillBonuses) {
		this.secondarySkillBonuses = secondarySkillBonuses;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public enum WeaponType {
		NONE,
		SWORD,
		AXE,
		DAGGER,
		POLEARM,
		GREATSWORD,
		GREATAXE,
		BOW,
		CROSSBOW,
		STAFF,
		WAND,
		SHIELD
	}
	
	public enum Hand {
		LEFT,
		RIGHT,
		BOTH,
		TWOHANDED
	}
}
