package com.fantasyunlimited.items.entity;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.ArrayList;
import java.util.List;

public class Race extends GenericItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -173288658621969325L;

	private String lore;
	private String startingLocationId;
	private boolean humanPlayable;
	
	private List<RacialBonus> bonuses = new ArrayList<>();

	@XStreamOmitField
	private Location startingLocation;
	
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
	public boolean isHumanPlayable() {
		return humanPlayable;
	}
	public void setHumanPlayable(boolean humanPlayable) {
		this.humanPlayable = humanPlayable;
	}

	public String getStartingLocationId() {
		return startingLocationId;
	}

	public void setStartingLocationId(String startingLocationId) {
		this.startingLocationId = startingLocationId;
	}

	public Location getStartingLocation() {
		return startingLocation;
	}

	public void setStartingLocation(Location startingLocation) {
		this.startingLocation = startingLocation;
	}
}
