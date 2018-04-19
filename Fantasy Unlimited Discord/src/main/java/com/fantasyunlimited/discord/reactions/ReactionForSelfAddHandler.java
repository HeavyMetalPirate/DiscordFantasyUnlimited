package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.event.EventHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class ReactionForSelfAddHandler extends EventHandler<ReactionAddEvent> {

	public ReactionForSelfAddHandler(Properties properties) {
		super(properties);
		FantasyUnlimited.autowire(this);
	}

	@Override
	public void handle(ReactionAddEvent event) {

		MessageInformation information = FantasyUnlimited.getInstance().getMessagesAwaitingReactions()
				.get(event.getMessage().getLongID());
		if (information == null) {
			return;
		}
		if (information.isCanBeRemoved()) { //In case it hasn't been cleaned up yet
			FantasyUnlimited.getInstance().getMessagesAwaitingReactions().remove(event.getMessage().getLongID());
			return;
		}

		if (information.getOriginator().getLongID() != event.getUser().getLongID()) {
			return;
		}
		/*
		 * Go on from here: check what the message was about, then pick a
		 * handler for it
		 */
	}
}
