package com.fantasyunlimited.discord.commands;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class CharacterCommandHandler extends CommandRequiresAuthenticationHandler {

	public static final String CMD = "character";

	private Map<String, Consumer<MessageReceivedEvent>> options;

	public CharacterCommandHandler(Properties properties) {
		super(properties, CMD);
		options = new LinkedHashMap<String, Consumer<MessageReceivedEvent>>();
		options.put(HandleCreate.OPTION, new HandleCreate());
		options.put(HandleList.OPTION, new HandleList());
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
				builder.append(available + (desc.getParameter().isEmpty() ? "" : " [" + desc.getParameter() + "]") + ": "
						+ desc.getDescription());
				builder.append("\n");
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

	private class HandleList implements OptionDescription, Consumer<MessageReceivedEvent> {
		protected static final String OPTION = "list";

		@Autowired
		private DiscordPlayerLogic playerLogic;

		public HandleList() {
			FantasyUnlimited.autowire(this);
		}

		@Override
		public void accept(MessageReceivedEvent t) {
			// TODO pagination
			DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache()
					.get(t.getAuthor().getLongID());
			List<PlayerCharacter> characters = playerLogic.getCharactersForPlayer(player);

			StringBuilder stringBuilder = new StringBuilder();
			int counter = 0;
			for(PlayerCharacter character: characters) {
				stringBuilder.append(++counter + ":\t");
				stringBuilder.append(character.getName());
				
				CharacterClass charClass = FantasyUnlimited.getInstance().getClassBag().getItem(character.getClassId());
				Race charRace = FantasyUnlimited.getInstance().getRaceBag().getItem(character.getRaceId());
				Location currentLocation = FantasyUnlimited.getInstance().getLocationsBag().getItem(character.getLocationId());
				stringBuilder.append(" (" + charRace.getName() + " " + charClass.getName() + ") - ");
				stringBuilder.append("Level: " + character.getCurrentLevel() + " - ");
				stringBuilder.append("Location: " + currentLocation.getName());
				stringBuilder.append("\n");
				if(counter == 10) {break;}
			}
			String charactersString = stringBuilder.toString();
			
			PlayerCharacter current = player.getCurrentCharacter();
			
			embedBuilder.withAuthorName(t.getAuthor().getDisplayName(t.getGuild()))
					.withAuthorIcon(t.getAuthor().getAvatarURL())
					.withFooterText("Your active character is '" + (current == null? "n/a" : current.getName()) + "'.")
					.appendField("Characters", charactersString.isEmpty()? "No characters created yet!" : charactersString, true);	
			FantasyUnlimited.getInstance().sendMessage(t.getChannel(), embedBuilder.build());
		}

		@Override
		public String getDescription() {
			return "Lists all characters created";
		}

		@Override
		public String getParameter() {
			return "";
		}

	}

	private class HandleCreate implements OptionDescription, Consumer<MessageReceivedEvent> {
		protected static final String OPTION = "create";

		@Override
		public void accept(MessageReceivedEvent t) {

			String stripped = stripParameterFromMessage(t.getMessage(), OPTION);
			if (stripped.trim().isEmpty()) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
						"Usage: " + OPTION + " <" + getParameter() + ">");
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
				information.getVars().put(Unicodes.numNames[raceCounter], race); // add
																					// first
																					// for
																					// correct
																					// access
				raceCounter++; // then increment the counter for display
				builder.append(raceCounter + ": " + race.getName() + " (ID: " + race.getId() + ")\n");
			}
			embedBuilder
					.withFooterText("For a description of races type '"
							+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "race <name/id>'.")
					.appendField("Choose a race for " + stripped + ", " + t.getAuthor().getDisplayName(t.getGuild()),
							builder.toString(), false);
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
