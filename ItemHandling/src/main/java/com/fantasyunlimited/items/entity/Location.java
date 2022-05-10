package com.fantasyunlimited.items.entity;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location extends GenericItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6683436248656781739L;
	private boolean marketAccess;
	private boolean globalMarketAccess;
	
	private Map<SecondarySkill, Integer> allowedSecondarySkills = new HashMap<>();
	
	private List<TravelConnection> connections = new ArrayList<>();
	private List<String> npcIds = new ArrayList<>();
	private List<String> hostileNPCIds = new ArrayList<>();

	@XStreamOmitField
	private int minimumLevel;
	@XStreamOmitField
	private int maximumLevel;
	@XStreamOmitField
	private List<NPC> npcs = new ArrayList<>();
	@XStreamOmitField
	private List<HostileNPC> hostileNPCs = new ArrayList<>();

	public boolean isMarketAccess() {
		return marketAccess;
	}
	public void setMarketAccess(boolean marketAccess) {
		this.marketAccess = marketAccess;
	}
	public boolean isGlobalMarketAccess() {
		return globalMarketAccess;
	}
	public void setGlobalMarketAccess(boolean globalMarketAccess) {
		this.globalMarketAccess = globalMarketAccess;
	}
	public List<TravelConnection> getConnections() {
		return connections;
	}
	public void setConnections(List<TravelConnection> connections) {
		this.connections = connections;
	}
	public List<String> getNpcIds() {
		return npcIds;
	}
	public void setNpcIds(List<String> npcIds) {
		this.npcIds = npcIds;
	}
	public List<String> getHostileNPCIds() {
		return hostileNPCIds;
	}
	public void setHostileNPCIds(List<String> hostileNPCIds) {
		this.hostileNPCIds = hostileNPCIds;
	}
	public Map<SecondarySkill, Integer> getAllowedSecondarySkills() {
		return allowedSecondarySkills;
	}
	public void setAllowedSecondarySkills(Map<SecondarySkill, Integer> allowedSecondarySkills) {
		this.allowedSecondarySkills = allowedSecondarySkills;
	}

	public int getMinimumLevel() {
		return minimumLevel;
	}

	public void setMinimumLevel(int minimumLevel) {
		this.minimumLevel = minimumLevel;
	}

	public int getMaximumLevel() {
		return maximumLevel;
	}

	public void setMaximumLevel(int maximumLevel) {
		this.maximumLevel = maximumLevel;
	}

	public List<NPC> getNpcs() {
		return npcs;
	}
	public void setNpcs(List<NPC> npcs) {
		this.npcs = npcs;
	}
	public List<HostileNPC> getHostileNPCs() {
		return hostileNPCs;
	}
	public void setHostileNPCs(List<HostileNPC> hostileNPCs) {
		this.hostileNPCs = hostileNPCs;
	}
}
