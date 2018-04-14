package com.fantasyunlimited.logic;

import com.fantasyunlimited.entity.DiscordPlayer;

public interface DiscordPlayerLogic extends CrudLogic<DiscordPlayer>{
	public DiscordPlayer findByDiscordId(String discordId);
}
