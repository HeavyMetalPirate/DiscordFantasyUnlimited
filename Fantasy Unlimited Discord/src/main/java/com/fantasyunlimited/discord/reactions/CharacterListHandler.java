package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class CharacterListHandler extends PaginationHandler {

	public CharacterListHandler(Properties properties) {
		super(properties);
	}

	@Override
	public Triple<Boolean, String[], Boolean> doDelegate(ReactionAddEvent event) {
		// No op
		return Triple.of(true, null, false);
	}
}
