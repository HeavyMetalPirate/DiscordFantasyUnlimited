package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Command for testing purposes. Puts a message up, places a reaction on it and queues 
 * the message for "awaiting a reaction by the origin user of the command"
 * @author HeavyMetalPirate
 *
 */
public class ReactionTestHandler extends CommandHandler {
	public static final String CMD = "testReaction";
	
	public ReactionTestHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		
		IMessage message = FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "I should only react to any reaction " + event.getAuthor().getDisplayName(event.getGuild()) + " puts up on this message.");	
		ReactionEmoji reaction = ReactionEmoji.of("test", 435013539535519755L);
		message.addReaction(reaction);
		
		MessageInformation information = new MessageInformation();
		information.setCanBeRemoved(false);
		information.setOriginDate(event.getMessage().getTimestamp());
		information.setOriginator(event.getMessage().getAuthor());
		information.setStatus("test");
		information.setMessage(message);
		
		FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(message.getLongID(), information);
	}

}
