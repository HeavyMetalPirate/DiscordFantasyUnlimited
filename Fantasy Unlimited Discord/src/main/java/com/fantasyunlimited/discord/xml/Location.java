package com.fantasyunlimited.discord.xml;

import java.util.ArrayList;
import java.util.List;

public class Location extends GenericItem {
	
	private boolean marketAccess;
	private boolean globalMarketAccess;
	
	private List<SecondarySkill> allowedSecondarySkills = new ArrayList<>();
	
	private List<TravelConnection> connections = new ArrayList<>();
	private List<String> npcIds = new ArrayList<>();
	private List<String> hostileNPCIds = new ArrayList<>();
	
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
	public List<SecondarySkill> getAllowedSecondarySkills() {
		return allowedSecondarySkills;
	}
	public void setAllowedSecondarySkills(List<SecondarySkill> allowedSecondarySkills) {
		this.allowedSecondarySkills = allowedSecondarySkills;
	}
}
