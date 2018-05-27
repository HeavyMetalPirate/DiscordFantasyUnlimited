package com.fantasyunlimited.discord.xml;

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
}
