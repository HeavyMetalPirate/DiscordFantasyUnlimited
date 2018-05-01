package com.fantasyunlimited.discord;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.xml.Location;

import sx.blah.discord.handle.obj.IMessage;

public class BattleInformation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5616626735379540290L;
	private LocalDateTime begin = LocalDateTime.now();
	private Location location;
	
	private boolean finished;
	
	private transient IMessage message;
	private long guildId;
	private long channelId;
	private long messageId;
	
	private Map<Long, BattlePlayerInformation> players = new HashMap<>();
	private Map<Integer, BattleNPC> hostiles = new HashMap<>();
	
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
	public Map<Long, BattlePlayerInformation> getPlayers() {
		return players;
	}
	public void setPlayers(Map<Long, BattlePlayerInformation> players) {
		this.players = players;
	}
	public Map<Integer, BattleNPC> getHostiles() {
		return hostiles;
	}
	public void setHostiles(Map<Integer, BattleNPC> hostiles) {
		this.hostiles = hostiles;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	public IMessage getMessage() {
		if(message == null) {
			message = FantasyUnlimited.getInstance().fetchMessage(guildId, channelId, messageId);
		}
		return message;
	}
	public void setMessage(IMessage message) {
		this.message = message;
		this.guildId = message.getGuild().getLongID();
		this.channelId = message.getChannel().getLongID();
		this.messageId = message.getLongID();
	}
}
