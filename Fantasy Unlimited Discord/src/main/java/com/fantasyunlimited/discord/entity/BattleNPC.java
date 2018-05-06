package com.fantasyunlimited.discord.entity;

import java.util.HashMap;
import java.util.Map;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.entity.Attributes;

public class BattleNPC extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;

	private String baseId;

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
		int dexterity = charClass.getAttributes().getDexterity()
				+ (charClass.getAttributes().getDexterityGrowth() * level);
		int endurance = charClass.getAttributes().getEndurance()
				+ (charClass.getAttributes().getEnduranceGrowth() * level);
		int luck = charClass.getAttributes().getLuck() + (charClass.getAttributes().getLuckGrowth() * level);
		int strength = charClass.getAttributes().getStrength()
				+ (charClass.getAttributes().getStrengthGrowth() * level);
		int intelligence = charClass.getAttributes().getIntelligence()
				+ (charClass.getAttributes().getIntelligenceGrowth() * level);
		int wisdom = charClass.getAttributes().getWisdom() + (charClass.getAttributes().getWisdomGrowth() * level);

		attributes.setDefense(defense);
		attributes.setDexterity(dexterity);
		attributes.setIntelligence(intelligence);
		attributes.setLuck(luck);
		attributes.setStrength(strength);
		attributes.setWisdom(wisdom);
		attributes.setEndurance(endurance);

		// TODO equipment bonus
		// TODO max atk resource tied to maximum if rage/focus class
		this.maxHealth = endurance * 10 + level * 15;
		this.maxAtkResource = wisdom * 15 + level * 20;
		this.currentHealth = maxHealth;
		this.currentAtkResource = maxAtkResource;
	}

	public HostileNPC getBase() {
		return FantasyUnlimited.getInstance().getHostileNPCBag().getItem(baseId);
	}

	public Map<BattlePlayer, Integer> getAggroMap() {
		return aggroMap;
	}

	public void setAggroMap(Map<BattlePlayer, Integer> aggroMap) {
		this.aggroMap = aggroMap;
	}
}
