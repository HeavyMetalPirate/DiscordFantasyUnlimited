package com.fantasyunlimited.data.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.*;

import com.fantasyunlimited.data.converter.CharacterClassConverter;
import com.fantasyunlimited.data.converter.LocationConverter;
import com.fantasyunlimited.data.converter.RaceConverter;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.items.entity.Attributes.Attribute;

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
	@Convert(converter = CharacterClassConverter.class)
	private CharacterClass classId;

	@Column
	@Convert(converter = RaceConverter.class)
	private Race raceId;

	@Column
	@Convert(converter = LocationConverter.class)
	private Location locationId;

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

	@Embedded
	private SecondarySkills secondarySkills;

	public PlayerCharacter() {
		attributes = new Attributes();
		secondarySkills = new SecondarySkills();
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

	public CharacterClass getClassId() {
		return classId;
	}

	public void setClassId(CharacterClass classId) {
		this.classId = classId;
	}

	public Race getRaceId() {
		return raceId;
	}

	public void setRaceId(Race raceId) {
		this.raceId = raceId;
	}

	public Location getLocationId() {
		return locationId;
	}

	public void setLocationId(Location locationId) {
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

	public SecondarySkills getSecondarySkills() {
		return secondarySkills;
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

	public int getSecondarySkill(SecondarySkill skill) {
		AtomicInteger skillBonus = new AtomicInteger(0);

		equipment.getGear().stream()
				.filter(gear -> gear.getSecondarySkillBonuses().stream().anyMatch(bonus -> bonus.getSkill() == skill))
				.forEach(gear -> {
					gear.getSecondarySkillBonuses().stream().
							filter(bonus -> bonus.getSkill() == skill).
							forEach(bonus -> skillBonus.getAndAdd(bonus.getBonus()));
				});

		AtomicInteger skillMultiplier = new AtomicInteger(0);
		raceId.getBonuses().stream()
				.filter(bonus -> bonus.getSecondarySkill() == skill)
				.forEach(bonus -> skillMultiplier.getAndAdd(bonus.getModifier()));

		int totalBonus = ((int)(skillBonus.get() * (1 + (skillMultiplier.get() / 100))));
		return totalBonus;
	}

	public int getAttributeBonus(Attribute attribute) {
		AtomicInteger bonus = new AtomicInteger(0);

		if (equipment.getMainhand() != null) {
			Weapon weapon = equipment.getMainhand();
			if (weapon != null)
				weapon.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getOffhand() != null) {
			Weapon weapon = equipment.getOffhand();
			if (weapon != null)
				weapon.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getHelmet() != null) {
			Equipment equ = equipment.getHelmet();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getChest() != null) {
			Equipment equ = equipment.getChest();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getGloves() != null) {
			Equipment equ = equipment.getGloves();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getPants() != null) {
			Equipment equ = equipment.getPants();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getBoots() != null) {
			Equipment equ = equipment.getBoots();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getRing1() != null) {
			Equipment equ = equipment.getRing1();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getRing2() != null) {
			Equipment equ = equipment.getRing2();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}
		if (equipment.getNeck() != null) {
			Equipment equ = equipment.getNeck();
			if (equ != null)
				equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
						.forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
		}

		// TODO class and racial bonus

		return bonus.get();
	}

	@Override
	public String toString() {
		return "PlayerCharacter [id=" + id + ", name=" + name + ", classId=" + classId.getId() + ", raceId=" + raceId.getId()
				+ ", locationId=" + locationId.getId() + ", currentLevel=" + currentLevel + ", currentXp=" + currentXp + "]";
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}


	public int getCurrentAtkResource() {
		return currentAtkResource;
//		if (classBag.getItem(classId).getEnergyType() == EnergyType.RAGE) {
//			return 0;
//		} else {
//			return getMaxAtkResource(classBag, weaponBag, equipmentBag);
//		}
	}

	public int getMaxHealth() {
		int base = attributes.getEndurance() + getAttributeBonus(Attribute.ENDURANCE);
		return base * 10 + currentLevel * 15;
	}

	public int getMaxAtkResource() {
		AtomicInteger extra = new AtomicInteger(0);
		equipment.getGear().stream().filter(equipment -> equipment != null)
				.filter(equipment -> equipment.getAtkResourceBonuses() != null)
				.forEach(equipment -> equipment.getAtkResourceBonuses().stream()
						.filter(bonus -> bonus.getSkill() == classId.getEnergyType())
						.forEach(bonus -> extra.getAndAdd(bonus.getBonus())));

		if (classId.getEnergyType() == EnergyType.MANA) {
			int base = attributes.getIntelligence() + getAttributeBonus(Attribute.INTELLIGENCE);
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
