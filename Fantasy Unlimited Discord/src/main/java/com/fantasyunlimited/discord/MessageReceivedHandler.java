package com.fantasyunlimited.discord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fantasyunlimited.discord.commands.CommandHandler;
import com.fantasyunlimited.discord.commands.PingCommandHandler;
import com.fantasyunlimited.discord.commands.UnknownCommandHandler;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MessageReceivedHandler extends EventHandler implements IListener<MessageReceivedEvent> {

	private Map<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
	private UnknownCommandHandler unknown;
	
	public MessageReceivedHandler(IDiscordClient discordClient, Properties properties) {
		super(discordClient, properties);
		setupCommands();
	}
	
	private void setupCommands() {
		commands.put(PingCommandHandler.CMD, new PingCommandHandler(client, properties)); //ping command
		
		//handler for unknown commands
		unknown = new UnknownCommandHandler(client);
	}
	
	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		if(message.getContent().startsWith(properties.getProperty(FantasyUnlimited.PREFIX_KEY)) == false) {
			return;
		}	
		
		String content = message.getContent().substring(properties.getProperty(FantasyUnlimited.PREFIX_KEY).length()); //Strip the prefix
		if(content.trim().isEmpty()) { return; }
		
		String command = content.split(" ")[0];
		if(command == null || command.isEmpty()) { return; }
		
		command = command.toLowerCase();
		CommandHandler handler = commands.get(command);
		if(handler != null) {
			handler.handle(event);
		}
		else {
			unknown.handle(event);
		}
	}
}
