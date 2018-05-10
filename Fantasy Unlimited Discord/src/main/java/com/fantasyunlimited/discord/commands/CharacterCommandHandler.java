package com.fantasyunlimited.discord.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.SerializableEmbedBuilder;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.GenericItem;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.Weapon;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CharacterCommandHandler extends CommandRequiresAuthenticationHandler {

	public static final String CMD = "character";

	private Map<String, Consumer<MessageReceivedEvent>> options;

	@Autowired
	private DiscordPlayerLogic playerLogic;

	public CharacterCommandHandler(Properties properties) {
		super(properties, CMD);
		options = new LinkedHashMap<String, Consumer<MessageReceivedEvent>>();
		options.put(HandleCreate.OPTION, new HandleCreate(properties));
		options.put(HandleList.OPTION, new HandleList());
		options.put(HandleSelect.OPTION, new HandleSelect());
		options.put(HandleInfo.OPTION, new HandleInfo());
		FantasyUnlimited.autowire(this);
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
				builder.append(available + (desc.getParameter().isEmpty() ? "" : " [" + desc.getParameter() + "]")
						+ ": " + desc.getDescription());
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

	private class HandleInfo implements OptionDescription, Consumer<MessageReceivedEvent> {
		protected static final String OPTION = "info";

		@Override
		public void accept(MessageReceivedEvent t) {
			String stripped = stripParameterFromMessage(t.getMessage(), OPTION);

			PlayerCharacter character;
			if (stripped.trim().isEmpty()) {
				DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache()
						.get(t.getAuthor().getLongID());
				character = playerLogic.getCharacter(player.getCurrentCharacter().getName());
			} else {
				character = playerLogic.getCharacter(stripped);
			}

			if (character == null) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(), "The character '" + stripped
						+ "' does not exist, " + t.getAuthor().getDisplayName(t.getGuild()));
				return;
			}

			embedBuilder = new SerializableEmbedBuilder().withAuthorName(t.getAuthor().getDisplayName(t.getGuild()))
					.withAuthorIcon(t.getAuthor().getAvatarURL()).withTitle("Character Information");

			// Basic data
			StringBuilder builder = new StringBuilder();

			Location location = FantasyUnlimited.getInstance().getLocationsBag().getItem(character.getLocationId());
			builder.append("```\n");
			builder.append("Name: \t" + character.getName() + "\n");
			builder.append("Level:\t" + character.getCurrentLevel() + "\n");
			builder.append("Experience: " + character.getCurrentXp() + "\n");
			builder.append("Current location: " + location.getName());
			builder.append("```");
			embedBuilder.appendField("Basic data", builder.toString(), true);

			// Race and class
			Race race = FantasyUnlimited.getInstance().getRaceBag().getItem(character.getRaceId());
			CharacterClass charClass = FantasyUnlimited.getInstance().getClassBag().getItem(character.getClassId());

			builder = new StringBuilder();
			builder.append("```\n");
			builder.append("Race: \t" + race.getName() + "\n");
			builder.append("Class:\t" + charClass.getName());
			builder.append("```");
			embedBuilder.appendField("Race & Class", builder.toString(), true);

			// Stats
			builder = new StringBuilder();
			builder.append("```\n");
			builder.append("Health: " + character.getCurrentHealth() + "/" + character.getMaxHealth() + "\n");
			builder.append(
					"ATKRES(!): " + character.getCurrentAtkResource() + "/" + character.getMaxAtkResource() + "\n");
			builder.append("STR:\t" + character.getAttributes().getStrength() + "\t");
			builder.append("DEX:\t" + character.getAttributes().getDexterity() + "\n");
			builder.append("END:\t" + character.getAttributes().getEndurance() + "\t");
			builder.append("DEF:\t" + character.getAttributes().getDefense() + "\n");
			builder.append("WIS:\t" + character.getAttributes().getWisdom() + "\t");
			builder.append("INT:\t" + character.getAttributes().getIntelligence() + "\n");
			builder.append("LCK:\t" + character.getAttributes().getLuck() + "\n");
			builder.append("Unspent points: " + character.getAttributes().getUnspent());
			builder.append("```");
			embedBuilder.appendField("Stats", builder.toString(), false);

			// Equipment
			Weapon mainHand = FantasyUnlimited.getInstance().getWeaponBag()
					.getItem(character.getEquipment().getMainhand());
			Weapon offHand = FantasyUnlimited.getInstance().getWeaponBag()
					.getItem(character.getEquipment().getOffhand());
			Equipment helmet = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getHelmet());
			Equipment gloves = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getGloves());
			Equipment chest = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getChest());
			Equipment pants = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getPants());
			Equipment boots = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getBoots());
			Equipment ring1 = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getRing1());
			Equipment ring2 = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getRing2());
			Equipment neck = FantasyUnlimited.getInstance().getEquipmentBag()
					.getItem(character.getEquipment().getNeck());

			embedBuilder.appendField("Helmet", getName(helmet), true);
			embedBuilder.appendField("Neck", getName(neck), true);
			embedBuilder.appendField("Chest", getName(chest), true);
			embedBuilder.appendField("Gloves", getName(gloves), true);
			embedBuilder.appendField("Pants", getName(pants), true);
			embedBuilder.appendField("Boots", getName(boots), true);
			embedBuilder.appendField("Ring", getName(ring1), true);
			embedBuilder.appendField("Ring", getName(ring2), true);
			embedBuilder.appendField("Mainhand", getName(mainHand), true);
			embedBuilder.appendField("Offhand", getName(offHand), true);

			// TODO: companion
			embedBuilder.appendField("Companion", "none", false);

			FantasyUnlimited.getInstance().sendMessage(t.getChannel(), embedBuilder.build());
		}

		private String getName(GenericItem item) {
			return item == null ? "none" : item.getName();
		}

		@Override
		public String getDescription() {
			return "displays information about the current character or about the character of the name provided";
		}

		@Override
		public String getParameter() {
			return "name of the character (empty: current own character)";
		}

	}

	private class HandleSelect implements OptionDescription, Consumer<MessageReceivedEvent> {
		protected static final String OPTION = "select";

		@Override
		public void accept(MessageReceivedEvent t) {
			String stripped = stripParameterFromMessage(t.getMessage(), OPTION);
			if (stripped.trim().isEmpty()) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
						"Usage: " + OPTION + " <" + getParameter() + ">");
				return;
			}
			DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache()
					.get(t.getAuthor().getLongID());

			PlayerCharacter character = playerLogic.getCharacterForPlayer(player, stripped);
			if (character == null) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(), "You don't have a character by the name of '"
						+ stripped + "', " + t.getAuthor().getDisplayName(t.getGuild()));
				return;
			}

			player = playerLogic.selectActiveCharacter(player, character);
			FantasyUnlimited.getInstance().getRegisteredUserCache().put(t.getAuthor().getLongID(), player);
			embedBuilder = new SerializableEmbedBuilder().withAuthorName(t.getAuthor().getDisplayName(t.getGuild()))
					.withAuthorIcon(t.getAuthor().getAvatarURL())
					.withFooterText("Your active character is '" + character.getName() + "'.").withTitle("Characters")
					.appendField("Character selected", "Active character successfully changed.", false);
			FantasyUnlimited.getInstance().sendMessage(t.getChannel(), embedBuilder.build());
		}

		@Override
		public String getDescription() {
			return "Selects the character as your current character";
		}

		@Override
		public String getParameter() {
			return "name of the character";
		}

	}

	private class HandleList extends CommandSupportsPaginatorHandler
			implements OptionDescription, Consumer<MessageReceivedEvent> {
		protected static final String OPTION = "list";

		public HandleList() {
			super(null, CMD, false);
		}

		@Override
		public void accept(MessageReceivedEvent t) {
			handle(t);
		}

		@Override
		public String getDescription() {
			return "Lists all characters created";
		}

		@Override
		public String getParameter() {
			return "";
		}

		@Override
		public Triple<Integer, MessageInformation, List<String>> doDelegate(MessageReceivedEvent t) {
			DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache()
					.get(t.getAuthor().getLongID());
			List<PlayerCharacter> characters = playerLogic.getCharactersForPlayer(player);

			List<String> values = new ArrayList<String>();
			int counter = 0;
			for (PlayerCharacter character : characters) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(++counter + ":\t");
				stringBuilder.append(character.getName());

				CharacterClass charClass = FantasyUnlimited.getInstance().getClassBag().getItem(character.getClassId());
				Race charRace = FantasyUnlimited.getInstance().getRaceBag().getItem(character.getRaceId());
				Location currentLocation = FantasyUnlimited.getInstance().getLocationsBag()
						.getItem(character.getLocationId());
				stringBuilder.append(" (" + charRace.getName() + " " + charClass.getName() + ") - ");
				stringBuilder.append("Lvl: " + character.getCurrentLevel() + " - ");
				stringBuilder.append("Loc: " + currentLocation.getName());
				values.add(stringBuilder.toString());
			}

			if (values.isEmpty()) {
				values.add("No characters available!");
			}

			PlayerCharacter current = player.getCurrentCharacter();

			embedBuilder = new SerializableEmbedBuilder().withAuthorName(t.getAuthor().getDisplayName(t.getGuild()))
					.withAuthorIcon(t.getAuthor().getAvatarURL())
					.withFooterText("Your active character is '" + (current == null ? "n/a" : current.getName()) + "'.")
					.withTitle("Characters");

			MessageInformation information = new MessageInformation();
			information.setCanBeRemoved(false);
			information.setOriginator(t.getAuthor());
			information.setOriginDate(t.getMessage().getCreationDate());
			information.getVars().put(CommandSupportsPaginatorHandler.EMBED_VAR, true);
			MessageStatus status = new MessageStatus();
			status.setName(Name.CHARACTER_LIST);
			information.setStatus(status);

			return Triple.of(10, information, values);
		}

		@Override
		public Type getType() {
			return null;
		}

	}

	private class HandleCreate extends CommandSupportsPaginatorHandler
			implements OptionDescription, Consumer<MessageReceivedEvent> {
		protected static final String OPTION = "create";

		public HandleCreate(Properties properties) {
			super(properties, CMD, false);
		}

		@Override
		public void accept(MessageReceivedEvent t) {
			handle(t);
		}

		@Override
		public String getDescription() {
			return "Starts the character creation process.";
		}

		@Override
		public String getParameter() {
			return "name of the character";
		}

		@Override
		public Triple<Integer, MessageInformation, List<String>> doDelegate(MessageReceivedEvent t) {
			String stripped = stripParameterFromMessage(t.getMessage(), OPTION);
			if (stripped.trim().isEmpty()) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(),
						"Usage: " + OPTION + " <" + getParameter() + ">");
				return Triple.of(null, null, null);
			}

			if (playerLogic.isNameAvailable(stripped) == false) {
				FantasyUnlimited.getInstance().sendMessage(t.getChannel(), "The name '" + stripped
						+ "' isn't available, " + t.getAuthor().getDisplayName(t.getGuild()) + ".");
				return Triple.of(null, null, null);
			}

			int raceCounter = 0;
			int itemsPerPage = 5;

			MessageInformation information = new MessageInformation();
			information.getVars().put("characterName", stripped);
			information.setCanBeRemoved(false);
			information.setOriginDate(t.getMessage().getTimestamp());
			information.setOriginator(t.getMessage().getAuthor());

			List<String> values = new ArrayList<String>();
			for (Race race : FantasyUnlimited.getInstance().getRaceBag().getItems()) {
				information.getVars().put("race" + raceCounter, race);
				raceCounter++; // then increment the counter for display
				int displayValue = raceCounter % itemsPerPage == 0 ? itemsPerPage : raceCounter % itemsPerPage;
				values.add(displayValue + ": " + race.getName() + " (ID: " + race.getId() + ")");
			}
			embedBuilder = new SerializableEmbedBuilder()
					.withFooterText("For a description of races type '"
							+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "race <name/id>'.")
					.withTitle("Choose a race for " + stripped + ", " + t.getAuthor().getDisplayName(t.getGuild()));

			String[] usedNumbers = Arrays.copyOf(Unicodes.numNames,
					raceCounter > itemsPerPage ? itemsPerPage : raceCounter);
			information.getVars().put("usedNumbers", Arrays.asList(usedNumbers));

			MessageStatus status = new MessageStatus();
			status.setName(Name.CREATE_CHAR_RACE_SELECTION);
			information.setStatus(status);

			information.getVars().put(ADDITIONAL_REACTIONS_VAR, usedNumbers);
			information.getVars().put(EMBED_VAR, true);
			information.getVars().put("embedBuilder", embedBuilder);
			return Triple.of(itemsPerPage, information, values);
		}

		@Override
		public Type getType() {
			return null;
		}
	}
}
