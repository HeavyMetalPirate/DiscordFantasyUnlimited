package com.fantasyunlimited.discord.entity;

import java.util.HashMap;
import java.util.Map;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Attributes.Attribute;
import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;
import com.fantasyunlimited.entity.Attributes;

public class BattleNPC extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;

	private String baseId;

	private Map<BattlePlayer, Integer> aggroMap = new HashMap<>();

	public BattleNPC() {
		super();
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

		equipment = new BattleEquipment();
		equipment.setMainhand(base.getMainhand());
		equipment.setOffhand(base.getOffhand());
		equipment.setHelmet(base.getHelmet());
		equipment.setChest(base.getChest());
		equipment.setGloves(base.getGloves());
		equipment.setPants(base.getPants());
		equipment.setBoots(base.getBoots());
		equipment.setRing1(base.getRing1());
		equipment.setRing2(base.getRing2());
		equipment.setNeck(base.getNeck());
		
		int enduraceBase = endurance + getAttributeBonus(Attribute.ENDURANCE);
		this.maxHealth = enduraceBase * 10 + level * 15;
		
		if (charClass.getEnergyType() == EnergyType.MANA) {
			int intelligenceBase = intelligence + getAttributeBonus(Attribute.INTELLIGENCE);
			this.maxAtkResource = intelligenceBase * 15 + level * 20;
		} else {
			this.maxAtkResource = 100;
		}
		
		this.currentHealth = maxHealth;
		if(charClass.getEnergyType() == EnergyType.RAGE) {
			this.currentAtkResource = 0;
		}
		else {
			this.currentAtkResource = maxAtkResource;
		}
		
		calculateRegeneration();
	}

	public String getName() {
		return getBase().getName();
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
