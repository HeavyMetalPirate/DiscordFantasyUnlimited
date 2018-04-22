package com.fantasyunlimited.discord.commands;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.xml.Race;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class CharacterCommandHandler extends CommandRequiresAuthenticationHandler {

	public static final String CMD = "character";

	private Map<String, Consumer<MessageReceivedEvent>> options;

	public CharacterCommandHandler(Properties properties) {
		super(properties, CMD);
		options = new LinkedHashMap<String, Consumer<MessageReceivedEvent>>();
		options.put(HandleCreate.OPTION, new HandleCreate());
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
				builder.append(available + (desc.getParameter().isEmpty() ? "" : " <" + desc.getParameter()) + ">:\t"
						+ desc.getDescription());
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
		protected static final String OPTION = "create";

		@Override
		public void accept(MessageReceivedEvent t) {

			String stripped = stripParameterFromMessage(t.getMessage(), OPTION);
			if (stripped.trim().isEmpty()) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(), "Usage: " + OPTION + " <" + getParameter() + ">" );
				return;
			}

			StringBuilder builder = new StringBuilder();
			int raceCounter = 0;

			MessageInformation information = new MessageInformation();
			information.getVars().put("characterName", stripped);
			information.setCanBeRemoved(false);
			information.setOriginDate(t.getMessage().getTimestamp());
			information.setOriginator(t.getMessage().getAuthor());

			for (Race race : FantasyUnlimited.getInstance().getRaceBag().getItems()) {
				information.getVars().put(Unicodes.numNames[raceCounter], race); // add first for correct access
				raceCounter++; // then increment the counter for display
				builder.append(raceCounter + ": " + race.getName() + " (ID: " + race.getId() + ")\n");
			}
			embedBuilder
					.withFooterText("For a description of races type '"
							+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "race <name/id>'.")
					.appendField("Choose a race for " + stripped + ", " + t.getAuthor().getDisplayName(t.getGuild()), builder.toString(),
							false);
			IMessage message = FantasyUnlimited.getInstance().sendMessage(t.getChannel(), embedBuilder.build());

			String[] usedNumbers = Arrays.copyOf(Unicodes.numNames, raceCounter);
			information.getVars().put("usedNumbers", Arrays.asList(usedNumbers));
			FantasyUnlimited.getInstance().addReactions(message, usedNumbers);

			information.setMessage(message);

			MessageStatus status = new MessageStatus();
			status.setName(Name.CREATE_CHAR_RACE_SELECTION);
			status.setPaginator(raceCounter > 5);
			status.setCurrentPage(1);
			int maxPage = (int) Math.ceil(raceCounter / 5);
			status.setMaxPage(maxPage);
			information.setStatus(status);

			FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(message.getLongID(), information);
		}

		@Override
		public String getDescription() {
			return "Starts the character creation process.";
		}

		@Override
		public String getParameter() {
			return "name of the character";
		}
	}
}
