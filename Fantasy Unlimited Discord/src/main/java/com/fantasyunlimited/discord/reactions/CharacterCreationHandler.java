package com.fantasyunlimited.discord.reactions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

public class CharacterCreationHandler extends ReactionsHandler {

	private Map<Long, Race> selectionStorage = new HashMap<>();

	@Autowired
	private DiscordPlayerLogic playerLogic;

	public CharacterCreationHandler(Properties properties) {
		super(properties);
		FantasyUnlimited.autowire(this);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		String emojiName = event.getReaction().getEmoji().getName();

		MessageInformation information = getInformationSecure(event);
		if (information.isCanBeRemoved()) {
			return;
		}

		List<?> usedNumbers = (List<?>) information.getVars().get("usedNumbers");

		switch (information.getStatus().getName()) {
		case CREATE_CHAR_CLASS_SELECTION:
		case CREATE_CHAR_RACE_SELECTION:
			if (usedNumbers.contains(emojiName) == false) {
				FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
						"Suck it and stop being stupid, " + event.getUser().getDisplayName(event.getGuild()));
				return;
			}
			break;
		case CREATE_CHAR_CONFIRMATION:
			if (emojiName.equals(Unicodes.checkmark) == false && emojiName.equals(Unicodes.crossmark) == false) {
				FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
						"Suck it and stop being stupid, " + event.getUser().getDisplayName(event.getGuild()));
				return;
			}
			break;
		default:
			return;

		}

		// Clear all reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeAllReactions();
		});

		switch (information.getStatus().getName()) {
		case CREATE_CHAR_CLASS_SELECTION:
			handleClassSelected(information, emojiName);
			break;
		case CREATE_CHAR_RACE_SELECTION:
			handleRaceSelected(information, emojiName);
			break;
		case CREATE_CHAR_CONFIRMATION:
			handleConfirmationSelection(information, emojiName);
			break;
		default:
			break;

		}
	}

	private void handleRaceSelected(MessageInformation information, String emojiName) {
		// get the selection first;
		Race selectedRace = (Race) information.getVars().get(emojiName);
		selectionStorage.put(information.getOriginator().getLongID(), selectedRace);
		// then edit the message
		StringBuilder builder = new StringBuilder();
		int classCounter = 0;
		for (CharacterClass characterClass : FantasyUnlimited.getInstance().getClassBag().getItems()) {
			information.getVars().put(Unicodes.numNames[classCounter], characterClass);
			classCounter++; // then increment the counter for display
			builder.append(classCounter + ": " + characterClass.getName() + " (ID: " + characterClass.getId() + ")\n");
		}
		embedBuilder = new EmbedBuilder()
				.withFooterText("For a description of classes type '"
						+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "class <name/id>'.")
				.appendField(
						"Choose a class for the " + selectedRace.getName() + " "
								+ information.getVars().get("characterName") + ", "
								+ information.getOriginator().getDisplayName(information.getMessage().getGuild()),
						builder.toString(), false);

		IMessage message = FantasyUnlimited.getInstance().editMessage(information.getMessage(), embedBuilder.build());
		information.setMessage(message);

		String[] usedNumbers = Arrays.copyOf(Unicodes.numNames, classCounter);
		information.getVars().put("usedNumbers", Arrays.asList(usedNumbers));
		FantasyUnlimited.getInstance().addReactions(information.getMessage(), usedNumbers);

		// set the status to the new one
		information.getStatus().setName(Name.CREATE_CHAR_CLASS_SELECTION);
	}

	private void handleClassSelected(MessageInformation information, String emojiName) {
		// get the selection first;
		CharacterClass selectedClass = (CharacterClass) information.getVars().get(emojiName);
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

		information.getStatus().setName(Name.CREATE_CHAR_CONFIRMATION);

	}

	private void handleConfirmationSelection(MessageInformation information, String emojiName) {
		if (emojiName.equals(Unicodes.crossmark)) {
			information.setCanBeRemoved(true);
			FantasyUnlimited.getInstance().editMessage(information.getMessage(), "Creation aborted by "
					+ information.getOriginator().getDisplayName(information.getMessage().getGuild()));
			return;
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

		character.setLocationId("1"); // TODO

		character.setCurrentLevel(1);
		character.setCurrentXp(0);

		player = playerLogic.addCharacter(player, character);
		FantasyUnlimited.getInstance().getRegisteredUserCache().put(information.getOriginator().getLongID(), player);
		
		Weapon startingWeapon = FantasyUnlimited.getInstance().getWeaponBag()
				.getItem(character.getEquipment().getMainhand());
		Location startingLocation = FantasyUnlimited.getInstance().getLocationsBag().getItem(character.getLocationId());
		embedBuilder = new EmbedBuilder().withFooterText("Your active character is '" + player.getCurrentCharacter().getName() + "'.")
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
	}
}
