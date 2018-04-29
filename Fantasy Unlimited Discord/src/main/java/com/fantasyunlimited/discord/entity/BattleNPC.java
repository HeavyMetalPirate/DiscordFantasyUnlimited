package com.fantasyunlimited.discord.entity;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.Attributes;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Race;

public class BattleNPC {
	private final Race race;
	private final CharacterClass charClass;
	private final int level;
	private int currentHealth;
	private final int maxHealth;
	
	private int currentAtkResource;
	private final int maxAtkResource;
	
	private final HostileNPC base;
	private final Attributes attributes;

	public BattleNPC(HostileNPC base) {
		this.base = base;
		this.race = FantasyUnlimited.getInstance().getRaceBag().getItem(base.getRaceId());
		this.charClass = FantasyUnlimited.getInstance().getClassBag().getItem(base.getClassId());
		
		this.level = base.getLevel();
		this.attributes = new Attributes();
		
		int defense = charClass.getAttributes().getDefense() + (charClass.getAttributes().getDefenseGrowth() * level);
		int dexterity = charClass.getAttributes().getDexterity() + (charClass.getAttributes().getDexterityGrowth() * level);
		int endurance = charClass.getAttributes().getEndurance() + (charClass.getAttributes().getEnduranceGrowth() * level);
		int luck = charClass.getAttributes().getLuck() + (charClass.getAttributes().getLuckGrowth() * level);
		int strength = charClass.getAttributes().getStrength() + (charClass.getAttributes().getStrengthGrowth() * level);
		int intelligence = charClass.getAttributes().getIntelligence() + (charClass.getAttributes().getIntelligenceGrowth() * level);
		int wisdom = charClass.getAttributes().getWisdom() + (charClass.getAttributes().getWisdomGrowth() * level);
		
		attributes.setDefense(defense);
		attributes.setDexterity(dexterity);
		attributes.setIntelligence(intelligence);
		attributes.setLuck(luck);
		attributes.setStrength(strength);
		attributes.setWisdom(wisdom);
		attributes.setEndurance(endurance);
				
		//TODO equipment bonus
		//TODO max atk resource tied to maximum if rage/focus class
		this.maxHealth = endurance * 10 + level * 15;
		this.maxAtkResource = wisdom * 15 + level * 20;
		this.currentHealth = maxHealth;
		this.currentAtkResource = maxAtkResource;
	}
	
	public Race getRace() {
		return race;
	}
	public CharacterClass getCharClass() {
		return charClass;
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
	public HostileNPC getBase() {
		return base;
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
}
