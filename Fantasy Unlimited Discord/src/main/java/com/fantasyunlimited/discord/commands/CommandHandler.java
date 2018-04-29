package com.fantasyunlimited.discord.commands;

import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.event.EventHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public abstract class CommandHandler extends EventHandler<MessageReceivedEvent> {
	protected final String command;
	protected final Properties properties;
	
	public abstract String getDescription();
	public abstract Type getType();
	
	public CommandHandler(Properties properties, String command) {
		super(properties);
		this.command = command;
		this.properties = properties;
	}
		
	protected String stripCommandFromMessage(IMessage message) {
		String content = message.getContent().substring(properties.getProperty(FantasyUnlimited.PREFIX_KEY).length()); //Strip the prefix
		return content.startsWith(command) ? content.substring(command.length()).trim() : content; //Strip the command with leading and trailing whitespace removed
	}
	
	protected String stripOptionFromMessage(String message) {
		if(message == null) return "";
		return message.split(" ")[0];
	}
	
	protected String stripParameterFromMessage(IMessage message, String optionName) {
		String content = stripCommandFromMessage(message);
		return content.startsWith(optionName) ? content.substring(optionName.length()).trim() : content;
	}
	
	protected String getDisplayNameForAuthor(MessageReceivedEvent event) {
		return event.getAuthor().getDisplayName(event.getGuild());
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
