package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public class Race extends GenericItem {
	
	private String lore;
	
	private List<RacialBonus> bonuses = new ArrayList<>();
	
	public List<RacialBonus> getBonuses() {
		return bonuses;
	}
	public void setBonuses(List<RacialBonus> bonuses) {
		this.bonuses = bonuses;
	}
	public String getLore() {
		return lore;
	}
	public void setLore(String lore) {
		this.lore = lore;
	}
}
