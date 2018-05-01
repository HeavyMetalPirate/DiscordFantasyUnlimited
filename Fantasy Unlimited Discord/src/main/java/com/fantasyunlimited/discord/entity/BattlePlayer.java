package com.fantasyunlimited.discord.entity;

import java.io.Serializable;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.entity.Attributes;
import com.fantasyunlimited.entity.PlayerCharacter;

public class BattlePlayer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;
	private Race race;
	private CharacterClass charClass;
	private int level;
	private int currentHealth;
	private int maxHealth;

	private int currentAtkResource;
	private int maxAtkResource;

	private String name;
	private Attributes attributes;
	private Long discordId;
	private Long characterId;

	public BattlePlayer() {
		// No op
	}

	public BattlePlayer(PlayerCharacter base) {
		this.discordId = Long.parseLong(base.getPlayer().getDiscordId());
		this.characterId = base.getId();

		this.race = FantasyUnlimited.getInstance().getRaceBag().getItem(base.getRaceId());
		this.charClass = FantasyUnlimited.getInstance().getClassBag().getItem(base.getClassId());

		this.name = base.getName();
		this.level = base.getCurrentLevel();
		this.attributes = base.getAttributes();

		this.maxHealth = base.getMaxHealth();
		this.maxAtkResource = base.getMaxAtkResource();
		this.currentHealth = base.getCurrentHealth();
		this.currentAtkResource = base.getCurrentAtkResource();
	}

	public Race getRace() {
		return race;
	}

	public CharacterClass getCharClass() {
		return charClass;
	}

	public int getLevel() {
		return level;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public boolean isDefeated() {
		return currentHealth <= 0;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public int getCurrentAtkResource() {
		return currentAtkResource;
	}

	public void setCurrentAtkResource(int currentAtkResource) {
		this.currentAtkResource = currentAtkResource;
	}

	public int getMaxAtkResource() {
		return maxAtkResource;
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

	public void setRace(Race race) {
		this.race = race;
	}

	public void setCharClass(CharacterClass charClass) {
		this.charClass = charClass;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setMaxAtkResource(int maxAtkResource) {
		this.maxAtkResource = maxAtkResource;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public void setDiscordId(Long discordId) {
		this.discordId = discordId;
	}

	public void setCharacterId(Long characterId) {
		this.characterId = characterId;
	}

}
