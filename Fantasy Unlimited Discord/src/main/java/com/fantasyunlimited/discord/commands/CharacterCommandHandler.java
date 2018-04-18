package com.fantasyunlimited.discord.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import com.fantasyunlimited.discord.FantasyUnlimited;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CharacterCommandHandler extends CommandHandler {

	public static final String CMD = "character";

	private Map<String, Consumer<MessageReceivedEvent>> options;

	public CharacterCommandHandler(Properties properties) {
		super(properties, CMD);
		options = new LinkedHashMap<String, Consumer<MessageReceivedEvent>>();
		options.put("create", new HandleCreate());
	}

	@Override
	public String getDescription() {
		return "Character management - type " + properties.getProperty(FantasyUnlimited.PREFIX_KEY) + CMD
				+ " to get a complete list of options and descriptions.";
	}
	
	@Override
	public Type getType() {
		return Type.CHARACTER;
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		String stripped = stripCommandFromMessage(event.getMessage());
		// Get option next
		String option = stripOptionFromMessage(stripped).toLowerCase();

		if (option == null || option.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("```Options:\n");
			for (String available : options.keySet()) {
				OptionDescription desc = (OptionDescription) options.get(available);
				builder.append("" + available + ":\t" + desc.getDescription());
			}
			builder.append("```");
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(), builder.toString());
			return;
		}

		Consumer<MessageReceivedEvent> consumer = options.get(option);
		if (consumer != null) {
			consumer.accept(event);
		} else {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
					"Unknown option `" + option + "` for command `" + CMD + "`.");
		}
	}

	private class HandleCreate implements OptionDescription, Consumer<MessageReceivedEvent> {
		@Override
		public void accept(MessageReceivedEvent t) {
			FantasyUnlimited.getInstance().sendMessage(t.getChannel(), "Fuck off, chickenlittle!");
		}

		@Override
		public String getDescription() {
			return "Starts the character creation process.";
		}
	}
}
