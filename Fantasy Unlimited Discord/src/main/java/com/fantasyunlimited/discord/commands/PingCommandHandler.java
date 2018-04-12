package com.fantasyunlimited.discord.commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class PingCommandHandler extends CommandHandler {

	public PingCommandHandler(IDiscordClient client) {
		super(client);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		sendMessage(event.getChannel(), "Pong!");
	}

}
