package com.fantasyunlimited.discord.commands;

import com.fantasyunlimited.discord.FantasyUnlimited;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class UnknownCommandHandler extends CommandHandler {

	public UnknownCommandHandler() {
		super(null, null);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IChannel channel = event.getChannel();
		IUser author = event.getAuthor();
		FantasyUnlimited.getInstance().sendMessage(channel, author.getDisplayName(channel.getGuild()) + ": unknown command.");	
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public Type getType() {
		return null;
	}

}
