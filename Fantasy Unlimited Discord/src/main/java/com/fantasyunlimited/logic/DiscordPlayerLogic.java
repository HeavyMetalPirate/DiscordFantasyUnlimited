package com.fantasyunlimited.logic;

import java.util.List;

import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;

public interface DiscordPlayerLogic extends CrudLogic<DiscordPlayer>{
	public DiscordPlayer findByDiscordId(String discordId);
	public DiscordPlayer addCharacter(DiscordPlayer player, PlayerCharacter character);
	
	public List<PlayerCharacter> getCharactersForPlayer(DiscordPlayer player);
	public PlayerCharacter getCharacterForPlayer(DiscordPlayer player, String name);
	public boolean isNameAvailable(String name);
	
	public DiscordPlayer selectActiveCharacter(DiscordPlayer player, PlayerCharacter character);
}
