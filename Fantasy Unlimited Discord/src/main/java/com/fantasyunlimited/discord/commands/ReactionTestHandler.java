package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Command for testing purposes. Puts a message up, places a reaction on it and
 * queues the message for "awaiting a reaction by the origin user of the
 * command"
 * 
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

		IMessage message = FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
				"I should only react to any reaction " + event.getAuthor().getDisplayName(event.getGuild())
						+ " puts up on this message.");
		ReactionEmoji reaction = ReactionEmoji.of("test", 435013539535519755L);
		FantasyUnlimited.getInstance().addReactions(message, reaction);

		final String[] numNames = { "\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3", "\u0035\u20E3",
				"\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3", "\u0030\u20E3" };

		FantasyUnlimited.getInstance().addReactions(message, numNames);
		
		MessageInformation information = new MessageInformation();
		information.setCanBeRemoved(false);
		information.setOriginDate(event.getMessage().getTimestamp());
		information.setOriginator(event.getMessage().getAuthor());
		information.setStatus(new MessageStatus());
		information.setMessage(message);

		FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(message.getLongID(), information);
	}

	@Override
	public String getDescription() {
		return "Fuck off, chicken little!";
	}

	@Override
	public Type getType() {
		return Type.OTHERS;
	}

}
