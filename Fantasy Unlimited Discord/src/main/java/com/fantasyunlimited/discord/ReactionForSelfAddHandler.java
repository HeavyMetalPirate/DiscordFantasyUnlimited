package com.fantasyunlimited.discord;

import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class ReactionForSelfAddHandler extends EventHandler implements IListener<ReactionAddEvent> {
	private static final Logger logger = Logger.getLogger(ReactionForSelfAddHandler.class);

	public ReactionForSelfAddHandler(IDiscordClient discordClient, Properties properties) {
		super(discordClient, properties);
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

		logger.debug(event.getUser().getLongID());
		logger.debug(information.getOriginator().getLongID());

		if (information.getOriginator().getLongID() != event.getUser().getLongID()) {
			return;
		}

		information.getMessage().edit("You reacted, " + event.getUser().getDisplayName(event.getGuild())
				+ ", last time on " + new Date().toString());
		information.setCanBeRemoved(true);

		/*
		 * Go on from here: check what the message was about, then pick a
		 * handler for it
		 */
	}
}
