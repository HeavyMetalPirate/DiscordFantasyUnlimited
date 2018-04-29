package com.fantasyunlimited.discord.reactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.Weapon;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class CharacterCreationHandler extends PaginationHandler {

	private Map<Long, Race> selectionStorage = new HashMap<>();

	@Autowired
	private DiscordPlayerLogic playerLogic;

	public CharacterCreationHandler(Properties properties) {
		super(properties);
		FantasyUnlimited.autowire(this);
	}

	@Override
	public Triple<Boolean, String[], Boolean> doDelegate(ReactionAddEvent event) {
		String emojiName = getEmojiName(event);
		MessageInformation information = getInformationSecure(event);
		if (information.isCanBeRemoved()) {
			return Triple.of(false, null, false);
		}

		List<?> usedNumbers = (List<?>) information.getVars().get("usedNumbers");

		switch (information.getStatus().getName()) {
		case CREATE_CHAR_CLASS_SELECTION:
		case CREATE_CHAR_RACE_SELECTION:
			if (usedNumbers.contains(emojiName) == false) {
				FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
						"Suck it and stop being stupid, " + event.getUser().getDisplayName(event.getGuild()));
				return Triple.of(false, null, false);
			}
			break;
		case CREATE_CHAR_CONFIRMATION:
			if (emojiName.equals(Unicodes.checkmark) == false && emojiName.equals(Unicodes.crossmark) == false) {
				FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
						"Suck it and stop being stupid, " + event.getUser().getDisplayName(event.getGuild()));
				return Triple.of(false, null, false);
			}
			break;
		default:
			return Triple.of(false, null, false);

		}

		// Clear all reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeAllReactions();
		});

		switch (information.getStatus().getName()) {
		case CREATE_CHAR_CLASS_SELECTION:
			return handleClassSelected(information, emojiName);
		case CREATE_CHAR_RACE_SELECTION:
			return handleRaceSelected(information, emojiName);
		case CREATE_CHAR_CONFIRMATION:
			return handleConfirmationSelection(information, emojiName);
		default:
			return Triple.of(null, null, false);
		}
	}

	@SuppressWarnings("unchecked")
	private Triple<Boolean, String[], Boolean> handleRaceSelected(MessageInformation information, String emojiName) {
		// get the selection first;
		int usedEmojiIndex = 0;
		for (String emoji : (List<String>) information.getVars().get("usedNumbers")) {
			if (emoji.equals(emojiName)) {
				break;
			}
			usedEmojiIndex++;
		}

		// then add the page factor to it
		// i.e. if you're on page 2 and used the 4th emoji => add 10
		int itemsPerPage = information.getStatus().getItemsPerPage();
		int currentPage = information.getStatus().getCurrentPage();

		usedEmojiIndex = usedEmojiIndex + (itemsPerPage * currentPage - itemsPerPage);

		Race selectedRace = (Race) information.getVars().get("race" + usedEmojiIndex);
		selectionStorage.put(information.getOriginator().getLongID(), selectedRace);
		// then edit the message
		List<String> values = new ArrayList<String>();

		int classCounter = 0;
		for (CharacterClass characterClass : FantasyUnlimited.getInstance().getClassBag().getItems()) {
			information.getVars().put("class" + classCounter, characterClass);
			classCounter++; // then increment the counter for display
			int displayValue = classCounter % itemsPerPage == 0? itemsPerPage : classCounter % itemsPerPage;
			values.add(displayValue + ": " + characterClass.getName() + " (ID: " + characterClass.getId() + ")");
		}
		information.getVars().put(PaginationHandler.VARNAME, values);

		embedBuilder = new EmbedBuilder()
				.withFooterText("For a description of classes type '"
						+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "class <name/id>'.")
				.withTitle("Choose a class for the " + selectedRace.getName() + " "
						+ information.getVars().get("characterName") + ", "
						+ information.getOriginator().getDisplayName(information.getMessage().getGuild()));

		String[] usedNumbers = Arrays.copyOf(Unicodes.numNames, classCounter > 10 ? 10 : classCounter);
		information.getVars().put("usedNumbers", Arrays.asList(usedNumbers));
		// set the status to the new one
		information.getStatus().setName(Name.CREATE_CHAR_CLASS_SELECTION);
		information.getStatus().setCurrentPage(1);
		information.getVars().put("embedBuilder", embedBuilder);
		return Triple.of(true, usedNumbers, true);
	}

	@SuppressWarnings("unchecked")
	private Triple<Boolean, String[], Boolean> handleClassSelected(MessageInformation information, String emojiName) {

		// get the selection first;
		int usedEmojiIndex = 0;
		for (String emoji : (List<String>) information.getVars().get("usedNumbers")) {
			if (emoji.equals(emojiName)) {
				break;
			}
			usedEmojiIndex++;
		}

		// then add the page factor to it
		// i.e. if you're on page 2 and used the 4th emoji => add 10
		int itemsPerPage = information.getStatus().getItemsPerPage();
		int currentPage = information.getStatus().getCurrentPage();

		usedEmojiIndex = usedEmojiIndex + (itemsPerPage * currentPage - itemsPerPage);
		CharacterClass selectedClass = (CharacterClass) information.getVars().get("class" + usedEmojiIndex);
		Race selectedRace = selectionStorage.get(information.getOriginator().getLongID());
		selectionStorage.remove(information.getOriginator().getLongID());

		information.getVars().put("selectedClass", selectedClass);
		information.getVars().put("selectedRace", selectedRace);

		embedBuilder = new EmbedBuilder()
				.withFooterText("Character creation for "
						+ information.getOriginator().getDisplayName(information.getMessage().getGuild()))
				.appendField("Confirmation",
						"Do you really want to be a " + selectedRace.getName() + " " + selectedClass.getName()
								+ " by the name of '" + information.getVars().get("characterName") + "'?",
						false);

		IMessage message = FantasyUnlimited.getInstance().editMessage(information.getMessage(), embedBuilder.build());
		information.setMessage(message);
		FantasyUnlimited.getInstance().addReactions(message, Unicodes.checkmark, Unicodes.crossmark);

		information.getVars().put("embedBuilder", embedBuilder);
		information.getStatus().setName(Name.CREATE_CHAR_CONFIRMATION);
		return Triple.of(false, new String[] { Unicodes.checkmark, Unicodes.crossmark }, false);
	}

	private Triple<Boolean, String[], Boolean> handleConfirmationSelection(MessageInformation information, String emojiName) {
		if (emojiName.equals(Unicodes.crossmark)) {
			information.setCanBeRemoved(true);
			FantasyUnlimited.getInstance().editMessage(information.getMessage(), "Creation aborted by "
					+ information.getOriginator().getDisplayName(information.getMessage().getGuild()));
			return Triple.of(false, null, false);
		}
		DiscordPlayer player = playerLogic.findByDiscordId(information.getOriginator().getStringID());

		PlayerCharacter character = new PlayerCharacter();
		character.setName((String) information.getVars().get("characterName"));
		character.setPlayer(player);

		CharacterClass selectedClass = (CharacterClass) information.getVars().get("selectedClass");
		Race selectedRace = (Race) information.getVars().get("selectedRace");

		character.setRaceId(selectedRace.getId());

		character.setClassId(selectedClass.getId());
		character.getAttributes().setEndurance(selectedClass.getAttributes().getEndurance());
		character.getAttributes().setStrength(selectedClass.getAttributes().getStrength());
		character.getAttributes().setDexterity(selectedClass.getAttributes().getDexterity());
		character.getAttributes().setWisdom(selectedClass.getAttributes().getWisdom());
		character.getAttributes().setIntelligence(selectedClass.getAttributes().getIntelligence());
		character.getAttributes().setDefense(selectedClass.getAttributes().getDefense());
		character.getAttributes().setLuck(selectedClass.getAttributes().getLuck());
		character.getAttributes().setUnspent(0);

		character.getEquipment().setCharacter(character);
		character.getEquipment().setMainhand(selectedClass.getStartingMainhand());
		character.getEquipment().setOffhand(selectedClass.getStartingOffhand());
		character.getEquipment().setHelmet(selectedClass.getStartingHelmet());
		character.getEquipment().setChest(selectedClass.getStartingChest());
		character.getEquipment().setGloves(selectedClass.getStartingGloves());
		character.getEquipment().setPants(selectedClass.getStartingPants());
		character.getEquipment().setBoots(selectedClass.getStartingBoots());
		character.getEquipment().setRing1(selectedClass.getStartingRing1());
		character.getEquipment().setRing2(selectedClass.getStartingRing2());
		character.getEquipment().setNeck(selectedClass.getStartingNeck());

		character.setLocationId("testbox"); // TODO

		character.setCurrentLevel(1);
		character.setCurrentXp(0);
		character.setCurrentHealth(selectedClass.getAttributes().getEndurance() * 10 + 15);
		character.setCurrentAtkResource(selectedClass.getAttributes().getWisdom() * 15 + 20);

		player = playerLogic.addCharacter(player, character);
		FantasyUnlimited.getInstance().getRegisteredUserCache().put(information.getOriginator().getLongID(), player);

		Weapon startingWeapon = FantasyUnlimited.getInstance().getWeaponBag()
				.getItem(character.getEquipment().getMainhand());
		Location startingLocation = FantasyUnlimited.getInstance().getLocationsBag().getItem(character.getLocationId());
		embedBuilder = new EmbedBuilder()
				.withFooterText("Your active character is '" + player.getCurrentCharacter().getName() + "'.")
				.appendField("The journey begins...",
						"It's finally time for your adventure to begin. You grab your " + startingWeapon.getName()
								+ " and set off. Like every new adventurer you first have to pass a bunch of tests, before you are legally allowed to roam the lands.",
						false)
				.appendField("You arrive at " + startingLocation.getName(),
						startingLocation.getDescription() + "\nThis looks like a perfect place for your first steps.",
						false)
				.appendField("Will you find your place in this world?", "And what will it be - good, bad, or maybe...?",
						false)
				.withAuthorName(information.getOriginator().getDisplayName(information.getMessage().getGuild()))
				.withAuthorIcon(information.getOriginator().getAvatarURL());

		FantasyUnlimited.getInstance().editMessage(information.getMessage(), embedBuilder.build());
		information.setCanBeRemoved(true);

		return Triple.of(false, null, false);
	}
}
