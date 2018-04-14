package com.fantasyunlimited.discord;

import java.util.Properties;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class ReactionAddHandler extends EventHandler implements IListener<ReactionAddEvent>{

	public ReactionAddHandler(IDiscordClient discordClient, Properties properties) {
		super(discordClient, properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		FantasyUnlimited.sendMessage(client, event.getChannel(), "Your reaction makes me moist.");		
	}

}
