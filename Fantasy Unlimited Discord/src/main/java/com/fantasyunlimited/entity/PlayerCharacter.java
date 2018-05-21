package com.fantasyunlimited.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.Weapon;

@Entity
public class PlayerCharacter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2618920221246608898L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private DiscordPlayer player;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private CharacterEquipment equipment = new CharacterEquipment();

	@Column
	private String name;

	@Column
	private String classId;

	@Column
	private String raceId;

	@Column
	private String locationId;

	@Column
	private int currentLevel;

	@Column
	private int currentXp;

	@Column
	private int currentHealth;

	@Column
	private int currentAtkResource;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "Inventories")
	private Map<String, Integer> inventory;

	@Embedded
	private Attributes attributes;

	public PlayerCharacter() {
		attributes = new Attributes();
		setInventory(new HashMap<String, Integer>());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DiscordPlayer getPlayer() {
		return player;
	}

	public void setPlayer(DiscordPlayer player) {
		this.player = player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getRaceId() {
		return raceId;
	}

	public void setRaceId(String raceId) {
		this.raceId = raceId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getCurrentXp() {
		return currentXp;
	}

	public void setCurrentXp(int currentXp) {
		this.currentXp = currentXp;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public CharacterEquipment getEquipment() {
		return equipment;
	}

	public int getMaxHealth() {
		int base = attributes.getEndurance() + getAttributeBonus(Attribute.ENDURANCE);
		return base * 10 + currentLevel * 15;
	}

	public int getMaxAtkResource() {
		if (FantasyUnlimited.getInstance().getClassBag().getItem(classId).getEnergyType() == EnergyType.MANA) {
			int base = attributes.getIntelligence() + getAttributeBonus(Attribute.INTELLIGENCE);
			return base * 15 + currentLevel * 20;
		} else {
			return 100;
		}
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
		if (equipment.getHelmet() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getHelmet());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getChest() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getChest());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getGloves() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getGloves());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getPants() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getPants());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getBoots() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getBoots());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getRing1() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getRing1());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getRing2() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getRing2());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getNeck() != null) {
			Equipment equ = FantasyUnlimited.getInstance().getEquipmentBag().getItem(equipment.getNeck());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}

		return bonus.get();
	}

	@Override
	public String toString() {
		return "PlayerCharacter [id=" + id + ", name=" + name + ", classId=" + classId + ", raceId=" + raceId
				+ ", locationId=" + locationId + ", currentLevel=" + currentLevel + ", currentXp=" + currentXp + "]";
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getCurrentAtkResource() {
		if(FantasyUnlimited.getInstance().getClassBag().getItem(classId).getEnergyType() == EnergyType.RAGE) {
			return 0;
		}
		else {
			return getMaxAtkResource();
		}
	}

	public void setCurrentAtkResource(int currentAtkResource) {
		this.currentAtkResource = currentAtkResource;
	}

	public Map<String, Integer> getInventory() {
		return inventory;
	}

	public void setInventory(Map<String, Integer> inventory) {
		this.inventory = inventory;
	}

}
