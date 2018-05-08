package com.fantasyunlimited.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;

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

	@Embedded
	private Attributes attributes;

	public PlayerCharacter() {
		attributes = new Attributes();
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
		// TODO equipment bonus
		return attributes.getEndurance() * 10 + currentLevel * 15;
	}

	public int getMaxAtkResource() {
		// TODO equipment bonus
		if (FantasyUnlimited.getInstance().getClassBag().getItem(classId).getEnergyType() == EnergyType.MANA) {
			return attributes.getIntelligence() * 15 + currentLevel * 20;
		} else {
			return 100;
		}
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
		return currentAtkResource;
	}

	public void setCurrentAtkResource(int currentAtkResource) {
		this.currentAtkResource = currentAtkResource;
	}

}
