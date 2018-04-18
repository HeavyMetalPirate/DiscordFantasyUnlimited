package com.fantasyunlimited.discord.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.Race;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

public class CharacterCommandHandler extends CommandHandler {

	public static final String CMD = "character";

	private Map<String, Consumer<MessageReceivedEvent>> options;
	private static final String[] numNames = {
		    "\u0031\u20E3",
		    "\u0032\u20E3",
		    "\u0033\u20E3",
		    "\u0034\u20E3",
		    "\u0035\u20E3",
		    "\u0036\u20E3",
		    "\u0037\u20E3",
		    "\u0038\u20E3",
		    "\u0039\u20E3"};

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
			StringBuilder builder = new StringBuilder();
			int raceCounter = 0;
			for (Race race : FantasyUnlimited.getInstance().getRaceBag().getItems()) {
				raceCounter++;
				builder.append(raceCounter + ": " + race.getName() + " (ID: " + race.getId() + ")\n");
			}
			embedBuilder
					.withFooterText("For a description of races type `"
							+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "race <name/id>`.")
					.appendField("Choose your race", builder.toString(), false);
			IMessage message = FantasyUnlimited.getInstance().sendMessage(t.getChannel(), embedBuilder.build());

			for (int i = 0; i < raceCounter; i++) {
				final int access = i;
				RequestBuffer.request(() -> {
					message.addReaction(ReactionEmoji.of(numNames[access]));
				});
			}

		}

		@Override
		public String getDescription() {
			return "Starts the character creation process.";
		}
	}
}
