package com.fantasyunlimited.discord;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.entity.PlayerCharacter;

public class BattleInformation {
	private LocalDateTime begin = LocalDateTime.now();
	private Location location;
	
	private boolean finished;
	
	private Map<Long, PlayerCharacter> players = new HashMap<>();
	private Map<Integer, HostileNPC> hostiles = new HashMap<>();
	
	public LocalDateTime getBegin() {
		return begin;
	}
	public void setBegin(LocalDateTime begin) {
		this.begin = begin;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Map<Long, PlayerCharacter> getPlayers() {
		return players;
	}
	public void setPlayers(Map<Long, PlayerCharacter> players) {
		this.players = players;
	}
	public Map<Integer, HostileNPC> getHostiles() {
		return hostiles;
	}
	public void setHostiles(Map<Integer, HostileNPC> hostiles) {
		this.hostiles = hostiles;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
