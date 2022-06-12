package com.fantasyunlimited.items.entity;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.HashMap;
import java.util.Map;

public class HostileNPC extends GenericItem {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3184442803761033166L;
	private String raceId;
	private String classId;
	private boolean unique;
	private int level;
	
	private String mainhand;
	private String offhand;
	
	private String helmet;
	private String chest;
	private String gloves;
	private String pants;
	private String boots;
	
	private String ring1;
	private String ring2;
	private String neck;	
	
	private Map<String, Double> loottable = new HashMap<>();
	private int minimumGold;
	private int maximumGold;

	@XStreamOmitField
	private CharacterClass characterClass;
	@XStreamOmitField
	private Race race;

	@XStreamOmitField
	private Weapon mainhandInstance;
	@XStreamOmitField
	private Weapon offhandInstance;
	@XStreamOmitField
	private Equipment helmetInstance;
	@XStreamOmitField
	private Equipment gloveInstance;
	@XStreamOmitField
	private Equipment chestInstance;
	@XStreamOmitField
	private Equipment pantsInstance;
	@XStreamOmitField
	private Equipment bootsInstance;
	@XStreamOmitField
	private Equipment ring1Instance;
	@XStreamOmitField
	private Equipment ring2Instance;
	@XStreamOmitField
	private Equipment neckInstance;

	public String getRaceId() {
		return raceId;
	}

	public void setRaceId(String raceId) {
		this.raceId = raceId;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getMainhand() {
		return mainhand;
	}

	public void setMainhand(String mainhand) {
		this.mainhand = mainhand;
	}

	public String getOffhand() {
		return offhand;
	}

	public void setOffhand(String offhand) {
		this.offhand = offhand;
	}

	public String getHelmet() {
		return helmet;
	}

	public void setHelmet(String helmet) {
		this.helmet = helmet;
	}

	public String getChest() {
		return chest;
	}

	public void setChest(String chest) {
		this.chest = chest;
	}

	public String getGloves() {
		return gloves;
	}

	public void setGloves(String gloves) {
		this.gloves = gloves;
	}

	public String getPants() {
		return pants;
	}

	public void setPants(String pants) {
		this.pants = pants;
	}

	public String getBoots() {
		return boots;
	}

	public void setBoots(String boots) {
		this.boots = boots;
	}

	public String getRing1() {
		return ring1;
	}

	public void setRing1(String ring1) {
		this.ring1 = ring1;
	}

	public String getRing2() {
		return ring2;
	}

	public void setRing2(String ring2) {
		this.ring2 = ring2;
	}

	public String getNeck() {
		return neck;
	}

	public void setNeck(String neck) {
		this.neck = neck;
	}

	public Map<String, Double> getLoottable() {
		return loottable;
	}

	public void setLoottable(Map<String, Double> loottable) {
		this.loottable = loottable;
	}

	public int getMinimumGold() {
		return minimumGold;
	}

	public void setMinimumGold(int minimumGold) {
		this.minimumGold = minimumGold;
	}

	public int getMaximumGold() {
		return maximumGold;
	}

	public void setMaximumGold(int maximumGold) {
		this.maximumGold = maximumGold;
	}

	public CharacterClass getCharacterClass() {
		return characterClass;
	}

	public void setCharacterClass(CharacterClass characterClass) {
		this.characterClass = characterClass;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Weapon getMainhandInstance() {
		return mainhandInstance;
	}

	public void setMainhandInstance(Weapon mainhandInstance) {
		this.mainhandInstance = mainhandInstance;
	}

	public Weapon getOffhandInstance() {
		return offhandInstance;
	}

	public void setOffhandInstance(Weapon offhandInstance) {
		this.offhandInstance = offhandInstance;
	}

	public Equipment getHelmetInstance() {
		return helmetInstance;
	}

	public void setHelmetInstance(Equipment helmetInstance) {
		this.helmetInstance = helmetInstance;
	}

	public Equipment getGloveInstance() {
		return gloveInstance;
	}

	public void setGloveInstance(Equipment gloveInstance) {
		this.gloveInstance = gloveInstance;
	}

	public Equipment getChestInstance() {
		return chestInstance;
	}

	public void setChestInstance(Equipment chestInstance) {
		this.chestInstance = chestInstance;
	}

	public Equipment getPantsInstance() {
		return pantsInstance;
	}

	public void setPantsInstance(Equipment pantsInstance) {
		this.pantsInstance = pantsInstance;
	}

	public Equipment getBootsInstance() {
		return bootsInstance;
	}

	public void setBootsInstance(Equipment bootsInstance) {
		this.bootsInstance = bootsInstance;
	}

	public Equipment getRing1Instance() {
		return ring1Instance;
	}

	public void setRing1Instance(Equipment ring1Instance) {
		this.ring1Instance = ring1Instance;
	}

	public Equipment getRing2Instance() {
		return ring2Instance;
	}

	public void setRing2Instance(Equipment ring2Instance) {
		this.ring2Instance = ring2Instance;
	}

	public Equipment getNeckInstance() {
		return neckInstance;
	}

	public void setNeckInstance(Equipment neckInstance) {
		this.neckInstance = neckInstance;
	}
}
