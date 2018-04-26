package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class CharacterListHandler extends PaginationHandler {

	public CharacterListHandler(Properties properties) {
		super(properties);
	}

	@Override
	public Pair<Boolean, String[]> doDelegate(ReactionAddEvent event) {
		// No op
		return Pair.of(true, null);
	}
}
