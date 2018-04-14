package com.fantasyunlimited.discord;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.logic.TestingLogic;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;

public class ReactionAddHandler extends EventHandler implements IListener<ReactionAddEvent>{

	@Autowired
	private TestingLogic testLogic;
	
	public ReactionAddHandler(IDiscordClient discordClient, Properties properties) {
		super(discordClient, properties);
		FantasyUnlimited.autowire(this);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		testLogic.storeDummyEntityToDB();
		FantasyUnlimited.sendMessage(client, event.getChannel(), "Your reaction makes me moist.");		
	}

}
