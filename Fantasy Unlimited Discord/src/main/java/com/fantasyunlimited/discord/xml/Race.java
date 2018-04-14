package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public class Race {
	
	private String id;
	private String name;
	private String iconName;
	private String lore;
	
	private List<RacialBonus> bonuses = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RacialBonus> getBonuses() {
		return bonuses;
	}
	public void setBonuses(List<RacialBonus> bonuses) {
		this.bonuses = bonuses;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLore() {
		return lore;
	}
	public void setLore(String lore) {
		this.lore = lore;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
}
