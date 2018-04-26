package com.fantasyunlimited.discord.reactions;

import java.util.Properties;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class BasicPaginationDelegate extends PaginationHandler {

	public BasicPaginationDelegate(Properties properties) {
		super(properties);
	}

	@Override
	public void doDelegate(ReactionAddEvent event) {
		// No op
	}
}
