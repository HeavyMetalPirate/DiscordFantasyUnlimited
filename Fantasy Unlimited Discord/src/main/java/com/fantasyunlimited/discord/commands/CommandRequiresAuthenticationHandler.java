package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import sx.blah.discord.handle.obj.IUser;

public abstract class CommandRequiresAuthenticationHandler extends CommandHandler {

	public CommandRequiresAuthenticationHandler(Properties properties, String command) {
		super(properties, command);
	}

	public boolean isAuthenticated(IUser user) {
		return true; //TODO
	}
}
