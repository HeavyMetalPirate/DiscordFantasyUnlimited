package com.fantasyunlimited.discord;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.xml.Location;

import sx.blah.discord.handle.obj.IEmbed.IEmbedField;
import sx.blah.discord.handle.obj.IEmbed;
import sx.blah.discord.handle.obj.IMessage;

public class BattleInformation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5616626735379540290L;
	private LocalDateTime begin = LocalDateTime.now();
	private Location location;

	private transient IMessage message;
	private long guildId;
	private long channelId;
	private long messageId;

	private transient IEmbedField battlelog;

	private Map<Long, BattlePlayerInformation> players = new HashMap<>();
	private Map<Integer, BattleNPC> hostiles = new HashMap<>();

	private int currentRound = 1;
	private Map<Integer, List<BattleAction>> rounds = new HashMap<>();
	
	public void flee() {
		players.clear();
		hostiles.clear();
	}
	
	public int getAliveEnemyCount() {
		int count = 0;
		for(BattleNPC hostile: hostiles.values()) {
			if(hostile.getCurrentHealth() > 0) 
				count++;
		}
		return count;
	}
	
	public int getAlivePlayerCount() {
		int count = 0;
		for(BattlePlayerInformation playerinfo: players.values()) {
			if(playerinfo.getCharacter().getCurrentHealth() > 0) 
				count++;
		}
		return count;
	}
	
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
		return getAliveEnemyCount() == 0 || getAlivePlayerCount() == 0;
	}

	public IMessage getMessage() {
		if (message == null) {
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

	public IEmbedField getBattlelog() {
		if (battlelog == null) {
			IEmbed embed = getMessage().getEmbeds().iterator().next();

			for (IEmbedField field : embed.getEmbedFields()) {
				if (field.getName().equals("Battle Log")) {
					this.battlelog = field;
					break;
				}
			}
		}
		return battlelog;
	}

	public void setBattlelog(IEmbedField battlelog) {
		this.battlelog = battlelog;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public Map<Integer, List<BattleAction>> getRounds() {
		return rounds;
	}

	public void setRounds(Map<Integer, List<BattleAction>> rounds) {
		this.rounds = rounds;
	}
}
