package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IUser;

public class RegisterCommandHandler extends CommandHandler {
	public static final String CMD = "register";
		
	@Autowired
	private DiscordPlayerLogic discordPlayerLogic;
	
	public RegisterCommandHandler(Properties properties) {
		super(properties, CMD);
		FantasyUnlimited.autowire(this);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IUser author = event.getAuthor();
		
		if(discordPlayerLogic.findByDiscordId(Long.toString(author.getLongID())) != null) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "Player with Discord ID " + author.getLongID() + " already registered!");
			return;
		}
		
		DiscordPlayer player = new DiscordPlayer();
		player.setDiscordId(Long.toString(author.getLongID()));
		player.setName(author.getName());
		player = discordPlayerLogic.save(player);
		FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "Welcome, " + player.getName() + "! Your player ID is: " + player.getId());
	}

}
