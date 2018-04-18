package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

public abstract class CommandHandler implements IListener<MessageReceivedEvent> {
	protected final String command;
	protected final Properties properties;
	protected final EmbedBuilder embedBuilder;
	
	public abstract String getDescription();
	public abstract Type getType();
	
	public CommandHandler(Properties properties, String command) {
		this.command = command;
		this.properties = properties;
		embedBuilder = new EmbedBuilder();
	}
		
	protected String stripCommandFromMessage(IMessage message) {
		String content = message.getContent().substring(properties.getProperty(FantasyUnlimited.PREFIX_KEY).length()); //Strip the prefix
		return content.substring(command.length()).trim(); //Strip the command with leading and trailing whitespace removed
	}
	
	protected String stripOptionFromMessage(String message) {
		if(message == null) return "";
		return message.split(" ")[0];
	}
	
	public enum Type {
		ACCOUNT("Account"),
		CHARACTER("Character Management"),
		COMBAT("Combat & Exploring"),
		MARKET("Market"),
		GUILD("Guild"),
		ADMINISTRATION("Administration"),
		OTHERS("Others");
		
		private String name;
		private Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}
