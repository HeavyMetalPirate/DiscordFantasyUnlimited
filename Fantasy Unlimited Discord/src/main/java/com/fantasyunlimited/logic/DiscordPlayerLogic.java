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

	public void addExperience(Long characterId, int amount);

	public void addExperience(PlayerCharacter character, int amount);

	public void addItemsToInventory(Long characterId,
			@SuppressWarnings("unchecked") Pair<String, Integer>... itemAndAmount);

	public void addItemsToInventory(PlayerCharacter character,
			@SuppressWarnings("unchecked") Pair<String, Integer>... itemAndAmount);
}
