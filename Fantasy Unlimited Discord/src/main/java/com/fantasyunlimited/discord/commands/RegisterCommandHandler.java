package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.DiscordPlayer;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class RegisterCommandHandler extends CommandHandler {
	public static final String CMD = "register";
	
	public RegisterCommandHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IUser author = event.getAuthor();
		
		if(FantasyUnlimited.getInstance().getRegisteredUserCache().get(author.getLongID()) != null) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "Player with Discord ID " + author.getLongID() + " already registered!");
			return;
		}
		
		DiscordPlayer player = new DiscordPlayer();
		player.setDiscordId(Long.toString(author.getLongID()));
		player.setName(author.getName());
		FantasyUnlimited.getInstance().getRegisteredUserCache().put(author.getLongID(), player);
		FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "Welcome, " + player.getName() + "!");
	}

	@Override
	public String getDescription() {
		return "Registers your Discord account with the bot. Required before character creation - the game links your characters to your Discord ID.";
	}

	@Override
	public Type getType() {
		return Type.ACCOUNT;
	}
	
	
}
