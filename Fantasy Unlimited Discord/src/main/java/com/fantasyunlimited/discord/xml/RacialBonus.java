package com.fantasyunlimited.discord.xml;

import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.Weapon.WeaponType;

public class RacialBonus extends GenericItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5173274950821252520L;
	private Attribute attribute;
	private WeaponType weaponType;
	private CombatSkill combatSkill;
	private SecondarySkill secondarySkill;
	private int modifier;

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
}
