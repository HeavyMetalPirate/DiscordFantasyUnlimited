package com.fantasyunlimited.discord.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.entity.Attributes;

public class BattleNPC implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;
	private String raceId;
	private String charClassId;
//	private Race race;
//	private CharacterClass charClass;
	private int level;
	private int currentHealth;
	private int maxHealth;
	
	private int currentAtkResource;
	private int maxAtkResource;
	
	private String baseId;
//	private HostileNPC base;
	private Attributes attributes;
	
	private Map<BattlePlayer, Integer> aggroMap = new HashMap<>();

	public BattleNPC() {
		
	}
	
	public BattleNPC(HostileNPC base) {
		this.baseId = base.getId();
		this.raceId = base.getRaceId();
		this.charClassId = base.getClassId();
		
		this.level = base.getLevel();
		this.attributes = new Attributes();
		
		CharacterClass charClass = FantasyUnlimited.getInstance().getClassBag().getItem(charClassId);
		
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
	public HostileNPC getBase() {
		return FantasyUnlimited.getInstance().getHostileNPCBag().getItem(baseId);
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

	public Map<BattlePlayer, Integer> getAggroMap() {
		return aggroMap;
	}

	public void setAggroMap(Map<BattlePlayer, Integer> aggroMap) {
		this.aggroMap = aggroMap;
	}
}
