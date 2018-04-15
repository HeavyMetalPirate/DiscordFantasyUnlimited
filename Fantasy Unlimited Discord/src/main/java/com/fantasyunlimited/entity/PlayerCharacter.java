package com.fantasyunlimited.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PlayerCharacter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(optional = false)
	private DiscordPlayer player;
	
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
	
	@Embedded
	private Attributes attributes;
		
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
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	@Embeddable
	protected class Attributes {
		private int endurance;
		private int strength;
		private int dexterity;
		private int wisdom;
		private int intelligence;
		private int defense;
		private int luck;
		
		private int unspent;

		public int getEndurance() {
			return endurance;
		}

		public void setEndurance(int endurance) {
			this.endurance = endurance;
		}

		public int getStrength() {
			return strength;
		}

		public void setStrength(int strength) {
			this.strength = strength;
		}

		public int getDexterity() {
			return dexterity;
		}

		public void setDexterity(int dexterity) {
			this.dexterity = dexterity;
		}

		public int getWisdom() {
			return wisdom;
		}

		public void setWisdom(int wisdom) {
			this.wisdom = wisdom;
		}

		public int getIntelligence() {
			return intelligence;
		}

		public void setIntelligence(int intelligence) {
			this.intelligence = intelligence;
		}

		public int getDefense() {
			return defense;
		}

		public void setDefense(int defense) {
			this.defense = defense;
		}

		public int getLuck() {
			return luck;
		}

		public void setLuck(int luck) {
			this.luck = luck;
		}

		public int getUnspent() {
			return unspent;
		}

		public void setUnspent(int unspent) {
			this.unspent = unspent;
		}
	}
}
