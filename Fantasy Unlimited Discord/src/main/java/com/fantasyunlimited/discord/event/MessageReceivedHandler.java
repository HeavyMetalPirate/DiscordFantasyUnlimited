package com.fantasyunlimited.discord.event;

import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.commands.BattleCommandHandler;
import com.fantasyunlimited.discord.commands.CharacterCommandHandler;
import com.fantasyunlimited.discord.commands.CommandHandler;
import com.fantasyunlimited.discord.commands.CommandRequiresAuthenticationHandler;
import com.fantasyunlimited.discord.commands.HelpCommandHandler;
import com.fantasyunlimited.discord.commands.InventoryCommandHandler;
import com.fantasyunlimited.discord.commands.PaginatorCommandHandler;
import com.fantasyunlimited.discord.commands.PingCommandHandler;
import com.fantasyunlimited.discord.commands.RaceCommandHandler;
import com.fantasyunlimited.discord.commands.ReactionTestHandler;
import com.fantasyunlimited.discord.commands.RegisterCommandHandler;
import com.fantasyunlimited.discord.commands.UnknownCommandHandler;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MessageReceivedHandler extends EventHandler<MessageReceivedEvent> {

	private Map<String, CommandHandler> commands = new LinkedHashMap<String, CommandHandler>();
	private UnknownCommandHandler unknown;
	
	public MessageReceivedHandler(Properties properties) {
		super(properties);
		setupCommands();
	}
	
	public List<CommandHandler> getCommandHandlers() {
		return new LinkedList<>(commands.values());
	}
	
	private void setupCommands() {
		commands.put(PingCommandHandler.CMD.toLowerCase(), new PingCommandHandler(properties)); //ping command
		commands.put(RegisterCommandHandler.CMD.toLowerCase(), new RegisterCommandHandler(properties)); //register command
		commands.put(ReactionTestHandler.CMD.toLowerCase(), new ReactionTestHandler(properties));
		commands.put(CharacterCommandHandler.CMD.toLowerCase(), new CharacterCommandHandler(properties));
		commands.put(InventoryCommandHandler.CMD, new InventoryCommandHandler(properties));
		commands.put(PaginatorCommandHandler.CMD.toLowerCase(), new PaginatorCommandHandler(properties));
		commands.put(BattleCommandHandler.CMD, new BattleCommandHandler(properties));
		commands.put(RaceCommandHandler.CMD, new RaceCommandHandler(properties));
		//Needs to be last always because it loads all previous commands to print
		commands.put(HelpCommandHandler.CMD.toLowerCase(), new HelpCommandHandler(properties));
		//handler for unknown commands
		unknown = new UnknownCommandHandler();
	}
	
	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		if(message.getAuthor().isBot()) {
			return;
		}
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
			if(handler instanceof CommandRequiresAuthenticationHandler &&
					((CommandRequiresAuthenticationHandler)handler).isAuthenticated(event.getAuthor()) == false) {
				FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "You need to be registered to do that.");
				return;
			}
			try {
				handler.handle(event);
			}
			catch(Exception e) {
				FantasyUnlimited.getInstance().sendExceptionMessage(e);
			}
		}
		else {
			unknown.handle(event);
		}
	}
}
