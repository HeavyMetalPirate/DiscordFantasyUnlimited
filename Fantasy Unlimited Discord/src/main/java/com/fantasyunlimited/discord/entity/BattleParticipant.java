package com.fantasyunlimited.discord.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.fantasyunlimited.discord.BattleStatus;
import com.fantasyunlimited.discord.BattleStatus.ModifierType;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.CombatSkill;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.Weapon;
import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.Weapon.WeaponType;
import com.fantasyunlimited.entity.Attributes;

public abstract class BattleParticipant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4019039035977061367L;

	protected String raceId;
	protected String charClassId;

	protected int level;
	protected int currentHealth;
	protected int maxHealth;

	protected int currentAtkResource;
	protected int maxAtkResource;

	protected float regenPercentage;
	protected float levelBonus;

	protected Attributes attributes;
	protected BattleEquipment equipment;

	private List<BattleStatus> statusModifiers = new ArrayList<>();

	public BattleParticipant() {
	}

	public abstract String getName();

	protected void calculateLevelBonus() {
		// Level bonus (y=(5*2^-x/15) * 2)
		levelBonus = (float) (5 * Math.pow(2, (level / 15)) * 2);
	}

	protected void calculateRegeneration() {
		// Y = X ^(1/4) + level bonus
		calculateLevelBonus();
		int base = attributes.getWisdom() + getAttributeBonus(Attribute.WISDOM);
		float regen = (float) Math.pow(base, 0.25);
		regenPercentage = regen + levelBonus;
	}

	public void applyDamage(int damage) {
		this.currentHealth -= damage;
		if (this.currentHealth < 0) {
			this.currentHealth = 0;
		}
	}

	public void applyHeal(int amount) {
		this.currentHealth += amount;
		if (this.currentHealth > this.maxHealth) {
			this.currentHealth = this.maxHealth;
		}
	}

	public void consumeAtkResource(int amount) {
		this.currentAtkResource -= amount;
		if (this.currentAtkResource < 0) {
			this.currentAtkResource = 0;
		}
	}

	public void regenAtkResource() {
		switch (getCharClass().getEnergyType()) {
		case FOCUS:
			// TODO equipment bonus
			this.currentAtkResource += 20;
			break;
		case MANA:
			double regenamount = regenPercentage * getMaxAtkResource() / 100;
			this.currentAtkResource += (int) Math.ceil(regenamount);
			break;
		case RAGE:
			// should not use this method
			throw new UnsupportedOperationException("Rage users should use generateRage(int) instead!");
		}

		if (this.currentAtkResource > this.maxAtkResource) {
			this.currentAtkResource = this.maxAtkResource;
		}
	}

	public void generateRage(int damage) {
		switch (getCharClass().getEnergyType()) {
		case FOCUS:
		case MANA:
			throw new UnsupportedOperationException("Mana/Focus users should use regenAtkResource() instead!");
		case RAGE:
			break;
		}

		double regen = damage / level + levelBonus;
		this.currentAtkResource += Math.ceil(regen);
		if (this.currentAtkResource > this.maxAtkResource) {
			this.currentAtkResource = this.maxAtkResource;
		}
	}

	public List<Equipment> getCurrentEquipment() {
		List<Equipment> equip = new ArrayList<>(
				Arrays.asList(FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getHelmet()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getChest()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getGloves()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getBoots()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getPants()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getRing1()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getRing2()),
						FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getNeck())));
		equip.removeAll(Collections.singleton(null));
		return equip;
	}

	/**
	 * Class and race bonus aren't applied linear, as they are % values
	 * 
	 * @param attribute
	 * @param type
	 * @return
	 */
	public int applyAttributeClassAndRaceBonus(int attribute, Attribute type) {
		AtomicInteger bonus = new AtomicInteger(100);

		getRace().getBonuses().stream().filter(bon -> bon.getAttribute() == type)
				.forEach(bon -> bonus.addAndGet(bon.getModifier()));
		getCharClass().getBonuses().stream().filter(bon -> bon.getAttribute() == type)
				.forEach(bon -> bonus.addAndGet(bon.getModifier()));

		return attribute * bonus.get() / 100;
	}

	public int getAttributeBonus(Attribute attribute) {
		AtomicInteger bonus = new AtomicInteger(0);

		if (equipment.getMainhand() != null) {
			Weapon weapon = FantasyUnlimited.getInstance().getWeaponBag().getItem(equipment.getMainhand());
			if (weapon != null)
				weapon.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getOffhand() != null) {
			Weapon weapon = FantasyUnlimited.getInstance().getWeaponBag().getItem(equipment.getOffhand());
			if (weapon != null)
				weapon.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		for (Equipment equ : getCurrentEquipment()) {
			equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
					.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}

		return bonus.get();
	}

	public int getCombatSkillBonus(CombatSkill combatSkill) {
		AtomicInteger bonus = new AtomicInteger(0);

		if (equipment.getMainhand() != null) {
			Weapon weapon = FantasyUnlimited.getInstance().getWeaponBag().getItem(equipment.getMainhand());
			if (weapon != null)
				weapon.getSkillBonuses().stream().filter(skillBon -> skillBon.getSkill() == combatSkill)
						.forEach(skillBon -> bonus.addAndGet(skillBon.getBonus()));
		}
		if (equipment.getOffhand() != null) {
			Weapon weapon = FantasyUnlimited.getInstance().getWeaponBag().getItem(equipment.getOffhand());
			if (weapon != null)
				weapon.getSkillBonuses().stream().filter(skillBon -> skillBon.getSkill() == combatSkill)
						.forEach(skillBon -> bonus.addAndGet(skillBon.getBonus()));
		}

		for (Equipment equ : getCurrentEquipment()) {
			equ.getSkillBonuses().stream().filter(skillBon -> skillBon.getSkill() == combatSkill)
					.forEach(skillBon -> bonus.addAndGet(skillBon.getBonus()));
		}

		for (BattleStatus status : statusModifiers) {
			if (status.getModifiedSkill() != null && status.getModifiedSkill() == combatSkill) {
				if (status.getModifierType() == ModifierType.RAISE) {
					bonus.addAndGet(status.getAmountModifier());
				} else {
					bonus.addAndGet(-status.getAmountModifier());
				}
			}
		}

		getRace().getBonuses().stream().filter(bon -> bon.getCombatSkill() == combatSkill)
				.forEach(bon -> bonus.addAndGet(bon.getModifier()));
		getCharClass().getBonuses().stream().filter(bon -> bon.getCombatSkill() == combatSkill)
				.forEach(bon -> bonus.addAndGet(bon.getModifier()));

		return bonus.get();
	}

	public float calculateDodgeChance() {
		float chance = levelBonus; // base
		chance += getCombatSkillBonus(CombatSkill.DODGE);
		// TODO attribute calculations
		return chance >= 0 ? chance : 0;
	}

	public float calculateCritChance() {
		float chance = levelBonus; // base
		chance += getCombatSkillBonus(CombatSkill.CRITICAL);
		// TODO attribute calculations
		return chance >= 0 ? chance : 0;
	}

	public float calculateBlockChance() {
		Weapon weapon = FantasyUnlimited.getInstance().getWeaponBag().getItem(equipment.getOffhand());
		if (weapon == null || (weapon != null && weapon.getType() != null && weapon.getType() != WeaponType.SHIELD)) {
			// no block if no shield in offhand!
			return 0f;
		}

		float chance = levelBonus; // base
		chance += getCombatSkillBonus(CombatSkill.BLOCK);
		// TODO stats modifier
		return chance >= 0 ? chance : 0;
	}

	public float calculateParryChance() {
		float chance = levelBonus; // base
		chance += getCombatSkillBonus(CombatSkill.PARRY);
		// TODO stats modifier
		return chance >= 0 ? chance : 0;
	}

	public Race getRace() {
		return FantasyUnlimited.getInstance().getRaceBag().getItem(raceId);
	}

	public CharacterClass getCharClass() {
		return FantasyUnlimited.getInstance().getClassBag().getItem(charClassId);
	}

	public int getLevel() {
		return level;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public boolean isDefeated() {
		return currentHealth <= 0;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public int getCurrentAtkResource() {
		return currentAtkResource;
	}

	public void setCurrentAtkResource(int currentAtkResource) {
		this.currentAtkResource = currentAtkResource;
	}

	public int getMaxAtkResource() {
		return maxAtkResource;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setMaxAtkResource(int maxAtkResource) {
		this.maxAtkResource = maxAtkResource;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public BattleEquipment getEquipment() {
		return equipment;
	}

	public void setEquipment(BattleEquipment equipment) {
		this.equipment = equipment;
	}

	public float getRegenPercentage() {
		return regenPercentage;
	}

	public List<BattleStatus> getStatusModifiers() {
		return statusModifiers;
	}

	public void setStatusModifiers(List<BattleStatus> statusModifiers) {
		this.statusModifiers = statusModifiers;
	}
}
