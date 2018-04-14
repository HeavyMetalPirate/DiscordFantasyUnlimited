package com.fantasyunlimited.discord.xml;

import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.Weapon.WeaponType;

public class RacialBonus {
	private String id;
	private String name;
	private String description;
	private String iconName;
	private Attribute attribute;
	private WeaponType weaponType;
	private CombatSkill combatSkill;
	private SecondarySkill secondarySkill;
	private int modifier;
	
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
	public Attribute getAttribute() {
		return attribute;
	}
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	public WeaponType getWeaponType() {
		return weaponType;
	}
	public void setWeaponType(WeaponType weaponType) {
		this.weaponType = weaponType;
	}
	public CombatSkill getCombatSkill() {
		return combatSkill;
	}
	public void setCombatSkill(CombatSkill combatSkill) {
		this.combatSkill = combatSkill;
	}
	public SecondarySkill getSecondarySkill() {
		return secondarySkill;
	}
	public void setSecondarySkill(SecondarySkill secondarySkill) {
		this.secondarySkill = secondarySkill;
	}
	public int getModifier() {
		return modifier;
	}
	public void setModifier(int modifier) {
		this.modifier = modifier;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
