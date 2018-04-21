package com.fantasyunlimited.discord.reactions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Race;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

public class CharacterCreationHandler extends ReactionsHandler {

	private static final String[] numNames = { "\u0031\u20E3", "\u0032\u20E3", "\u0033\u20E3", "\u0034\u20E3",
			"\u0035\u20E3", "\u0036\u20E3", "\u0037\u20E3", "\u0038\u20E3", "\u0039\u20E3" };

	private Map<Long, Race> selectionStorage = new HashMap<>();

	public CharacterCreationHandler(Properties properties) {
		super(properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		String emojiName = event.getReaction().getEmoji().getName();

		MessageInformation information = getInformationSecure(event);

		List<?> usedNumbers = (List<?>) information.getVars().get("usedNumbers");
		if (usedNumbers.contains(emojiName) == false) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
					"Suck it and stop being stupid, " + event.getUser().getDisplayName(event.getGuild()));
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
			information.getVars().put(numNames[classCounter], characterClass); // add
																				// first
																				// for
																				// correct
																				// access
			classCounter++; // then increment the counter for display
			builder.append(classCounter + ": " + characterClass.getName() + " (ID: " + characterClass.getId() + ")\n");
		}
		embedBuilder
				.withFooterText("For a description of classes type '"
						+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "class <name/id>'.")
				.appendField(
						"Choose a class for the " + selectedRace.getName() + " " + information.getVars().get("characterName") + ", "
								+ information.getOriginator().getDisplayName(information.getMessage().getGuild()),
						builder.toString(), false);

		IMessage message = FantasyUnlimited.getInstance().editMessage(information.getMessage(), embedBuilder.build());
		information.setMessage(message);

		String[] usedNumbers = Arrays.copyOf(numNames, classCounter);
		information.getVars().put("usedNumbers", Arrays.asList(usedNumbers));
		FantasyUnlimited.getInstance().addReactions(information.getMessage(), usedNumbers);

		// set the status to the new one
		information.getStatus().setName(Name.CREATE_CHAR_CLASS_SELECTION);
	}

	private void handleClassSelected(MessageInformation information, String emojiName) {
		// get the selection first;
		CharacterClass selectedClass = (CharacterClass) information.getVars().get(emojiName);
		Race selectedRace = selectionStorage.get(information.getOriginator().getLongID());

		FantasyUnlimited.getInstance().sendMessage(information.getMessage().getChannel(),
				"So you want to be a " + selectedRace.getName() + " " + selectedClass.getName()
						+ "? Too bad I'm still lazy as fuck and haven't done this shit yet.");
	}
}
