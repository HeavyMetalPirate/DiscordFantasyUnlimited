package com.fantasyunlimited.discord.commands;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class UnknownCommandHandler extends CommandHandler {

	public UnknownCommandHandler(IDiscordClient client) {
		super(client);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IChannel channel = event.getChannel();
		IUser author = event.getAuthor();
		sendMessage(channel, author.getDisplayName(channel.getGuild()) + ": unknown command.");	
	}

}
