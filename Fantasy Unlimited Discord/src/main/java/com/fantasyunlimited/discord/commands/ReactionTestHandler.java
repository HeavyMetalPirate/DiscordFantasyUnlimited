package com.fantasyunlimited.discord.commands;

import java.util.List;
import java.util.Properties;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IMessage;

public class ReactionTestHandler extends CommandHandler {
	public static final String CMD = "testReaction";
	
	public ReactionTestHandler(IDiscordClient client, Properties properties) {
		super(client, properties, CMD);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		
		ReactionEmoji reaction = ReactionEmoji.of("test", 435013539535519755L);
		message.addReaction(reaction);
	}

}
