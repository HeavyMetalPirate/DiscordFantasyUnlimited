package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.event.EventHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public abstract class ReactionsHandler extends EventHandler<ReactionAddEvent> {

	public ReactionsHandler(Properties properties) {
		super(properties);
	}

	protected MessageInformation getInformationSecure(ReactionAddEvent event) {
		MessageInformation info = FantasyUnlimited.getInstance().getMessagesAwaitingReactions()
				.get(event.getMessageID());
		if (info == null) {
			IllegalStateException exception = new IllegalStateException(
					"Message handled by ReactionsHandler but wasn't found waiting. Message: " + event.getMessage());
			FantasyUnlimited.getInstance().sendExceptionMessage(exception);
			throw exception;
		}
		return info;
	}

	protected String getEmojiName(ReactionAddEvent event) {
		return event.getReaction().getEmoji().getName();
	}
}
