package com.fantasyunlimited.data.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.*;

import com.fantasyunlimited.items.bags.ClassBag;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.Attributes.Attribute;
import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.CharacterClass.EnergyType;
import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Weapon;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class PlayerCharacter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2618920221246608898L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private FantasyUnlimitedUser user;

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
	private int gold;

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

	public FantasyUnlimitedUser getUser() {
		return user;
	}

	public void setUser(FantasyUnlimitedUser user) {
		this.user = user;
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

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public CharacterEquipment getEquipment() {
		return equipment;
	}

	public void addGold(int amount) {
		this.gold += amount;
	}

	public void removeGold(int amount) {
		if (amount > this.gold) {
			throw new IllegalStateException(
					"Cannot remove more gold than the character is owning! " + this.gold + " (owned) vs. " + amount);
		}
		this.gold -= amount;
	}
	public int getAttributeBonus(Attribute attribute, WeaponBag weaponBag, EquipmentBag equipmentBag) {
		AtomicInteger bonus = new AtomicInteger(0);

		if (equipment.getMainhand() != null) {
			Weapon weapon = weaponBag.getItem(equipment.getMainhand());
			if (weapon != null)
				weapon.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getOffhand() != null) {
			Weapon weapon = weaponBag.getItem(equipment.getOffhand());
			if (weapon != null)
				weapon.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getHelmet() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getHelmet());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getChest() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getChest());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getGloves() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getGloves());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getPants() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getPants());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getBoots() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getBoots());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getRing1() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getRing1());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getRing2() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getRing2());
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getNeck() != null) {
			Equipment equ = equipmentBag.getItem(equipment.getNeck());
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


	public int getCurrentAtkResource(ClassBag classBag, WeaponBag weaponBag, EquipmentBag equipmentBag) {
		if (classBag.getItem(classId).getEnergyType() == EnergyType.RAGE) {
			return 0;
		} else {
			return getMaxAtkResource(classBag, weaponBag, equipmentBag);
		}
	}
	public CharacterClass getCharacterClass(ClassBag classBag) {
		return classBag.getItem(classId);
	}

	public int getMaxHealth(WeaponBag weaponBag, EquipmentBag equipmentBag) {
		int base = attributes.getEndurance() + getAttributeBonus(Attribute.ENDURANCE, weaponBag, equipmentBag);
		return base * 10 + currentLevel * 15;
	}

	public int getMaxAtkResource(ClassBag classBag, WeaponBag weaponBag, EquipmentBag equipmentBag) {
		AtomicInteger extra = new AtomicInteger(0);
		equipment.getGear(weaponBag, equipmentBag).stream().filter(equipment -> equipment != null)
				.filter(equipment -> equipment.getAtkResourceBonuses() != null)
				.forEach(equipment -> equipment.getAtkResourceBonuses().stream()
						.filter(bonus -> bonus.getSkill() == getCharacterClass(classBag).getEnergyType())
						.forEach(bonus -> extra.getAndAdd(bonus.getBonus())));

		if (classBag.getItem(classId).getEnergyType() == EnergyType.MANA) {
			int base = attributes.getIntelligence() + getAttributeBonus(Attribute.INTELLIGENCE, weaponBag, equipmentBag);
			base += extra.get();
			return base * 15 + currentLevel * 20;
		} else {
			return 100 + extra.get();
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
