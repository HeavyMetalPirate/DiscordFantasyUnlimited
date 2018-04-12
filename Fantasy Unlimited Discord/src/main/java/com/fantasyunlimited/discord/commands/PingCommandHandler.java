package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class PingCommandHandler extends CommandHandler {
	public static final String CMD = "ping";
	
	public PingCommandHandler(IDiscordClient client, Properties properties) {
		super(client, properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		sendMessage(event.getChannel(), "Pong!");
	}

}
