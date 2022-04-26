package com.fantasyunlimited.items.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPC extends GenericItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1753068605912809748L;

	private String raceId;
	private String classId;
	private int level;

	private String title;
	
	private boolean vending;
	private Map<String, Integer> selling = new HashMap<>();
	
	private List<String> genericDialogue = new ArrayList<>();
	private List<String> questIds = new ArrayList<>();
	
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isVending() {
		return vending;
	}
	public void setVending(boolean vending) {
		this.vending = vending;
	}
	public Map<String, Integer> getSelling() {
		return selling;
	}
	public void setSelling(Map<String, Integer> selling) {
		this.selling = selling;
	}
	public List<String> getGenericDialogue() {
		return genericDialogue;
	}
	public void setGenericDialogue(List<String> genericDialogue) {
		this.genericDialogue = genericDialogue;
	}
	public List<String> getQuestIds() {
		return questIds;
	}
	public void setQuestIds(List<String> questIds) {
		this.questIds = questIds;
	}
}
