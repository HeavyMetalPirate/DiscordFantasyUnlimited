package com.fantasyunlimited.logic;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;

public interface DiscordPlayerLogic extends CrudLogic<DiscordPlayer> {
	public DiscordPlayer findByDiscordId(String discordId);

	public DiscordPlayer addCharacter(DiscordPlayer player, PlayerCharacter character);

	public List<PlayerCharacter> getCharactersForPlayer(DiscordPlayer player);

	public PlayerCharacter getCharacterForPlayer(DiscordPlayer player, String name);

	public PlayerCharacter getCharacter(String name);

	public boolean isNameAvailable(String name);

	public DiscordPlayer selectActiveCharacter(DiscordPlayer player, PlayerCharacter character);
	
	public void saveNewHealth(Long characterId, int currentHealth);
	public void saveNewHealth(PlayerCharacter character, int currentHealth);
	

	/**
	 * returns true if the character leveled up
	 * @param characterId
	 * @param amount
	 * @return
	 */
	public boolean addExperience(Long characterId, int amount);

	/**
	 * returns true if the character leveled up
	 * @param character
	 * @param amount
	 * @return
	 */
	public boolean addExperience(PlayerCharacter character, int amount);

	public void addItemsToInventory(Long characterId,
			@SuppressWarnings("unchecked") Pair<String, Integer>... itemAndAmount);

	public void addItemsToInventory(PlayerCharacter character,
			@SuppressWarnings("unchecked") Pair<String, Integer>... itemAndAmount);
}
