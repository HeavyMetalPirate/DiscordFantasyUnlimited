package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class PingCommandHandler extends CommandHandler {
	public static final String CMD = "ping";
	
	public PingCommandHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "Pong!");
	}

	@Override
	public String getDescription() {
		return "Simple test command to see if the bot is alive and well.";
	}

	@Override
	public Type getType() {
		return Type.OTHERS;
	}

}
