package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public class CharacterClass extends GenericItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4076926648887811462L;

	private String lore;
	
	private EnergyType energyType;
	
	private Attributes attributes;
	
	private String startingMainhand;
	private String startingOffhand;
	
	private String startingHelmet;
	private String startingChest;
	private String startingGloves;
	private String startingPants;
	private String startingBoots;
	
	private String startingRing1;
	private String startingRing2;
	private String startingNeck;	
	
	private List<ClassBonus> bonuses = new ArrayList<>();
	private List<Skill> skills = new ArrayList<>();
	
	public List<Skill> getAvailableSkills(int level, com.fantasyunlimited.entity.Attributes attributes) {
		List<Skill> available = new ArrayList<>();
		
		for(Skill skill: skills) {
			if(skill.getHighestAvailable(level, attributes) == null) {
				continue;
			}
			available.add(skill);
		}
		
		return available;
	}
	
	public String getLore() {
		return lore;
	}
	public void setLore(String lore) {
		this.lore = lore;
	}
	public List<ClassBonus> getBonuses() {
		return bonuses;
	}
	public void setBonuses(List<ClassBonus> bonuses) {
		this.bonuses = bonuses;
	}
	public List<Skill> getSkills() {
		return skills;
	}
	public void setSkills(List<Skill> skills) {
		this.skills = skills;
	}
	public Attributes getAttributes() {
		return attributes;
	}
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
	public String getStartingMainhand() {
		return startingMainhand;
	}
	public void setStartingMainhand(String startingMainhand) {
		this.startingMainhand = startingMainhand;
	}
	public String getStartingOffhand() {
		return startingOffhand;
	}
	public void setStartingOffhand(String startingOffhand) {
		this.startingOffhand = startingOffhand;
	}
	public String getStartingHelmet() {
		return startingHelmet;
	}
	public void setStartingHelmet(String startingHelmet) {
		this.startingHelmet = startingHelmet;
	}
	public String getStartingChest() {
		return startingChest;
	}
	public void setStartingChest(String startingChest) {
		this.startingChest = startingChest;
	}
	public String getStartingGloves() {
		return startingGloves;
	}
	public void setStartingGloves(String startingGloves) {
		this.startingGloves = startingGloves;
	}
	public String getStartingPants() {
		return startingPants;
	}
	public void setStartingPants(String startingPants) {
		this.startingPants = startingPants;
	}
	public String getStartingBoots() {
		return startingBoots;
	}
	public void setStartingBoots(String startingBoots) {
		this.startingBoots = startingBoots;
	}
	public String getStartingRing1() {
		return startingRing1;
	}
	public void setStartingRing1(String startingRing1) {
		this.startingRing1 = startingRing1;
	}
	public String getStartingRing2() {
		return startingRing2;
	}
	public void setStartingRing2(String startingRing2) {
		this.startingRing2 = startingRing2;
	}
	public String getStartingNeck() {
		return startingNeck;
	}
	public void setStartingNeck(String startingNeck) {
		this.startingNeck = startingNeck;
	}
	
	public EnergyType getEnergyType() {
		return energyType;
	}
	public void setEnergyType(EnergyType energyType) {
		this.energyType = energyType;
	}

	public enum EnergyType {
		RAGE("Rage"),
		FOCUS("Focus"),
		MANA("Mana");
		
		private String label;
		private EnergyType(String label) {
			this.label = label;
		}
		
		 @Override
		public String toString() {
			return label;
		}
	}
}
