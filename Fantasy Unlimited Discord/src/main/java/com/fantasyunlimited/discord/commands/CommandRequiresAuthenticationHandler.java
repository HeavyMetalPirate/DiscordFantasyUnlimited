package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.DiscordPlayer;

import sx.blah.discord.handle.obj.IUser;

public abstract class CommandRequiresAuthenticationHandler extends CommandHandler {
		
	public CommandRequiresAuthenticationHandler(Properties properties, String command) {
		super(properties, command);
		FantasyUnlimited.autowire(this);
	}

	public boolean isAuthenticated(IUser user) {
		DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache().get(user.getLongID());		
		return player != null;
	}
}
