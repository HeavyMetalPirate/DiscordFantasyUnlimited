package com.fantasyunlimited.discord;

import java.util.Properties;

import sx.blah.discord.api.IDiscordClient;

public abstract class EventHandler {
	protected final Properties properties;
	protected final IDiscordClient client;
	
	public EventHandler(IDiscordClient discordClient, Properties properties) {
		this.properties = properties;
		this.client = discordClient;
	}
}
