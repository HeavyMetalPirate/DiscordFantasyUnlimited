package com.fantasyunlimited.discord.xml;

import java.util.HashMap;
import java.util.Map;

public class HostileNPC extends GenericItem {
	private String raceId;
	private String classId;
	private boolean unique;
	private int level;
	
	private Map<Double, String> loottable = new HashMap<>();

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

	public Map<Double, String> getLoottable() {
		return loottable;
	}

	public void setLoottable(Map<Double, String> loottable) {
		this.loottable = loottable;
	}
}
