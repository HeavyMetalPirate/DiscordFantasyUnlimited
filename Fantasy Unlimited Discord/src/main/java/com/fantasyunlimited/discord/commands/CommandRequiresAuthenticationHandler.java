package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.obj.IUser;

public abstract class CommandRequiresAuthenticationHandler extends CommandHandler {
	private static final Logger logger = Logger.getLogger(CommandRequiresAuthenticationHandler.class);
	
	@Autowired
	private DiscordPlayerLogic userLogic;
	
	public CommandRequiresAuthenticationHandler(Properties properties, String command) {
		super(properties, command);
		FantasyUnlimited.autowire(this);
	}

	public boolean isAuthenticated(IUser user) {
		DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache().get(user.getLongID());
		if(player == null) {
			//no cache hit
			logger.trace("No cache hit for user: " + user.getStringID());
			player = userLogic.findByDiscordId(user.getStringID());
			if(player == null) {
				return false;
			}
			FantasyUnlimited.getInstance().getRegisteredUserCache().put(user.getLongID(), player);
		}		
		return player != null;
	}
}
