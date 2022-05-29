package com.fantasyunlimited.battle.entity;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.fantasyunlimited.battle.entity.BattleStatus.ModifierType;
import com.fantasyunlimited.data.converter.CharacterClassConverter;
import com.fantasyunlimited.data.converter.RaceConverter;
import com.fantasyunlimited.data.entity.Attributes;
import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.CombatSkill;
import com.fantasyunlimited.items.entity.Gear;
import com.fantasyunlimited.items.entity.Race;
import com.fantasyunlimited.items.entity.Weapon;
import com.fantasyunlimited.items.entity.Attributes.Attribute;
import com.fantasyunlimited.items.entity.Weapon.WeaponType;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@MappedSuperclass
public abstract class BattleParticipant implements Serializable {

	@Id
	@GeneratedValue
	@Type(type="org.hibernate.type.UUIDCharType")
	private UUID id;

	@ManyToOne
	private BattleInformation battleInformation;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4019039035977061367L;

	@Convert(converter = RaceConverter.class)
	protected Race raceId;
	@Convert(converter = CharacterClassConverter.class)
	protected CharacterClass charClassId;

	protected int level;
	protected int currentHealth;
	protected int maxHealth;

	protected int currentAtkResource;
	protected int maxAtkResource;

	protected float regenPercentage;
	protected float levelBonus;

	@Embedded
	protected Attributes attributes;
	@OneToOne(cascade = CascadeType.ALL)
	protected BattleEquipment equipment;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BattleStatus> statusModifiers = new ArrayList<>();

	public abstract String getName();

	public void calculateLevelBonus() {
		// Level bonus (y=(5*2^-x/15) * 2)
		levelBonus = (float) (5 * Math.pow(2, (level / 15)) * 2);
	}

	public void calculateRegeneration() {
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
		switch (charClassId.getEnergyType()) {
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
		switch (charClassId.getEnergyType()) {
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

	public List<Gear> getCurrentGear() {
		List<Gear> equip = new ArrayList<>(
				Arrays.asList(equipment.getMainhand(), equipment.getOffhand(),
						equipment.getHelmet(), equipment.getChest(),
						equipment.getGloves(), equipment.getBoots(),
						equipment.getPants(), equipment.getRing1(),
						equipment.getRing2(), equipment.getNeck()));
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

		raceId.getBonuses().stream().filter(bon -> bon.getAttribute() == type)
				.forEach(bon -> bonus.addAndGet(bon.getModifier()));
		charClassId.getBonuses().stream().filter(bon -> bon.getAttribute() == type)
				.forEach(bon -> bonus.addAndGet(bon.getModifier()));

		return attribute * bonus.get() / 100;
	}

	public int getAttributeBonus(Attribute attribute) {
		AtomicInteger bonus = new AtomicInteger(0);
		for (Gear equ : getCurrentGear()) {
			if (equ.getAttributeBonuses() == null) {
				continue;
			}
			equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
					.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}

		return bonus.get();
	}

	public Race getRaceId() {
		return raceId;
	}

	public CharacterClass getCharClassId() {
		return charClassId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setRaceId(Race raceId) {
		this.raceId = raceId;
	}

	public void setCharClassId(CharacterClass charClassId) {
		this.charClassId = charClassId;
	}

	public void setRegenPercentage(float regenPercentage) {
		this.regenPercentage = regenPercentage;
	}

	public float getLevelBonus() {
		return levelBonus;
	}

	public void setLevelBonus(float levelBonus) {
		this.levelBonus = levelBonus;
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

	public BattleInformation getBattleInformation() {
		return battleInformation;
	}

	public void setBattleInformation(BattleInformation battleInformation) {
		this.battleInformation = battleInformation;
	}
}
