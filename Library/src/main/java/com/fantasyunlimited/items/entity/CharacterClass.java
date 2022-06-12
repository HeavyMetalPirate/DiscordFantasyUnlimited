package com.fantasyunlimited.items.entity;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.ArrayList;
import java.util.List;

public class CharacterClass extends GenericItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4076926648887811462L;

	private String lore;
	private boolean humanPlayable;

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
	private List<String> skills = new ArrayList<>();
	@XStreamOmitField
	private List<Skill> skillInstances = new ArrayList<>();

	@XStreamOmitField
	private Weapon startingMainhandInstance;
	@XStreamOmitField
	private Weapon startingOffhandInstance;
	@XStreamOmitField
	private Equipment startingHelmetInstance;
	@XStreamOmitField
	private Equipment startingChestInstance;
	@XStreamOmitField
	private Equipment startingGlovesInstance;
	@XStreamOmitField
	private Equipment startingPantsInstance;
	@XStreamOmitField
	private Equipment startingBootsInstance;
	@XStreamOmitField
	private Equipment startingRing1Instance;
	@XStreamOmitField
	private Equipment startingRing2Instance;
	@XStreamOmitField
	private Equipment startingNeckInstance;

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

	public List<String> getSkills() {
		return skills;
	}

	public void setSkills(List<String> skills) {
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

	public boolean isHumanPlayable() {
		return humanPlayable;
	}

	public void setHumanPlayable(boolean humanPlayable) {
		this.humanPlayable = humanPlayable;
	}

	public List<Skill> getSkillInstances() {
		return skillInstances;
	}

	public void setSkillInstances(List<Skill> skillInstances) {
		this.skillInstances = skillInstances;
	}

	public Weapon getStartingMainhandInstance() {
		return startingMainhandInstance;
	}

	public void setStartingMainhandInstance(Weapon startingMainhandInstance) {
		this.startingMainhandInstance = startingMainhandInstance;
	}

	public Weapon getStartingOffhandInstance() {
		return startingOffhandInstance;
	}

	public void setStartingOffhandInstance(Weapon startingOffhandInstance) {
		this.startingOffhandInstance = startingOffhandInstance;
	}

	public Equipment getStartingHelmetInstance() {
		return startingHelmetInstance;
	}

	public void setStartingHelmetInstance(Equipment startingHelmetInstance) {
		this.startingHelmetInstance = startingHelmetInstance;
	}

	public Equipment getStartingChestInstance() {
		return startingChestInstance;
	}

	public void setStartingChestInstance(Equipment startingChestInstance) {
		this.startingChestInstance = startingChestInstance;
	}

	public Equipment getStartingGlovesInstance() {
		return startingGlovesInstance;
	}

	public void setStartingGlovesInstance(Equipment startingGlovesInstance) {
		this.startingGlovesInstance = startingGlovesInstance;
	}

	public Equipment getStartingPantsInstance() {
		return startingPantsInstance;
	}

	public void setStartingPantsInstance(Equipment startingPantsInstance) {
		this.startingPantsInstance = startingPantsInstance;
	}

	public Equipment getStartingBootsInstance() {
		return startingBootsInstance;
	}

	public void setStartingBootsInstance(Equipment startingBootsInstance) {
		this.startingBootsInstance = startingBootsInstance;
	}

	public Equipment getStartingRing1Instance() {
		return startingRing1Instance;
	}

	public void setStartingRing1Instance(Equipment startingRing1Instance) {
		this.startingRing1Instance = startingRing1Instance;
	}

	public Equipment getStartingRing2Instance() {
		return startingRing2Instance;
	}

	public void setStartingRing2Instance(Equipment startingRing2Instance) {
		this.startingRing2Instance = startingRing2Instance;
	}

	public Equipment getStartingNeckInstance() {
		return startingNeckInstance;
	}

	public void setStartingNeckInstance(Equipment startingNeckInstance) {
		this.startingNeckInstance = startingNeckInstance;
	}
}
