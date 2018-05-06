package com.fantasyunlimited.discord.entity;

import com.fantasyunlimited.entity.PlayerCharacter;

public class BattlePlayer extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;

	private String name;
	private Long discordId;
	private Long characterId;

	public BattlePlayer() {
		// No op
	}

	public BattlePlayer(PlayerCharacter base) {
		this.discordId = Long.parseLong(base.getPlayer().getDiscordId());
		this.characterId = base.getId();

		this.raceId = base.getRaceId();
		this.charClassId = base.getClassId();

		this.name = base.getName();
		this.level = base.getCurrentLevel();
		this.attributes = base.getAttributes();

		this.maxHealth = base.getMaxHealth();
		this.maxAtkResource = base.getMaxAtkResource();
		this.currentHealth = base.getCurrentHealth();
		this.currentAtkResource = base.getCurrentAtkResource();
	}

	public String getName() {
		return name;
	}

	public Long getDiscordId() {
		return discordId;
	}

	public Long getCharacterId() {
		return characterId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDiscordId(Long discordId) {
		this.discordId = discordId;
	}

	public void setCharacterId(Long characterId) {
		this.characterId = characterId;
	}

}
