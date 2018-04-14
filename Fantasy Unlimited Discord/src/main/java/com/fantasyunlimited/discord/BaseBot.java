package com.fantasyunlimited.discord;

import sx.blah.discord.api.IDiscordClient;

/**
 * This represents a SUPER basic bot (literally all it does is login).
 * This is used as a base for all example bots.
 */
public abstract class BaseBot {
	public IDiscordClient client; // The instance of the discord client.

	public BaseBot(IDiscordClient client) {
		this.client = client; // Sets the client instance to the one provided
	}
	
	public void logout() {
		this.client.logout();
	}
}
