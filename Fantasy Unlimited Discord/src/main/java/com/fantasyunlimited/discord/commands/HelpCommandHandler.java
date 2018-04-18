package com.fantasyunlimited.discord.commands;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fantasyunlimited.discord.FantasyUnlimited;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

public class HelpCommandHandler extends CommandHandler {
	private static final Logger logger = Logger.getLogger(HelpCommandHandler.class);
	
	private Map<Type, List<CommandHandler>> commands;
	public static final String CMD = "help";

	public HelpCommandHandler(Properties properties) {
		super(properties, CMD);
		commands = null;
	}
	
	public void setupCommands() {
		
		commands = new LinkedHashMap<>();
		commands.put(Type.ACCOUNT, new LinkedList<>());
		commands.put(Type.CHARACTER, new LinkedList<>());
		commands.put(Type.COMBAT, new LinkedList<>());
		commands.put(Type.MARKET, new LinkedList<>());
		commands.put(Type.GUILD, new LinkedList<>());
		commands.put(Type.ADMINISTRATION, new LinkedList<>());
		commands.put(Type.OTHERS, new LinkedList<>());

		for (CommandHandler handler : FantasyUnlimited.getInstance().getMessageReceivedHandler().getCommandHandlers()) {
			commands.get(handler.getType()).add(handler);
		}
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		if(commands == null) setupCommands();
		
		logger.debug(commands);
		
		EmbedBuilder builder = new EmbedBuilder();
		for(Type type: commands.keySet()) {
			String content = "";
			for(CommandHandler handler: commands.get(type)) {
				content += handler.command + ":\t" + handler.getDescription() + "\n";
			}
			if(content == null || content.isEmpty()) content = "none (yet)";
			builder.appendField(type.getName(), content, false);
		}
		FantasyUnlimited.getInstance().sendMessage(event.getChannel(), builder.build());
	}

	@Override
	public String getDescription() {
		return "Displays this help dialog";
	}

	@Override
	public Type getType() {
		return Type.OTHERS;
	}

}
