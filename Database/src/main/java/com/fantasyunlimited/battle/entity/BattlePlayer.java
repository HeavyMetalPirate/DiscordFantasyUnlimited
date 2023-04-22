package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.data.converter.PlayerCharacterConverter;
import com.fantasyunlimited.data.entity.PlayerCharacter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;

@Entity
public class BattlePlayer extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;
	private String name;
	private Long characterId;

	@Convert(converter = PlayerCharacterConverter.class)
	private PlayerCharacter playerCharacter;

	public String getName() {
		return name;
	}

	public Long getCharacterId() {
		return characterId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCharacterId(Long characterId) {
		this.characterId = characterId;
	}

	public PlayerCharacter getPlayerCharacter() {
		return playerCharacter;
	}

	public void setPlayerCharacter(PlayerCharacter playerCharacter) {
		this.playerCharacter = playerCharacter;
	}
}
