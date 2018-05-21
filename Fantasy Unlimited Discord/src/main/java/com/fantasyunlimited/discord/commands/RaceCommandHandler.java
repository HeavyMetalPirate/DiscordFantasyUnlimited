package com.fantasyunlimited.discord.commands;

import java.util.Collection;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.RacialBonus;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class RaceCommandHandler extends CommandHandler implements OptionDescription {
	public static final String CMD = "race";

	public RaceCommandHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		String value = stripCommandFromMessage(event.getMessage());

		if (value == null || value.isEmpty()) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
					"Usage: `" + properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "race <id/name>");
			return;
		}

		buildEmbedBuilderWithAuthorInformation(event, "Information about the race");

		Collection<Race> racesFound = FantasyUnlimited.getInstance().getRaceBag().getItemsByValue(value);
		if (racesFound.isEmpty()) {
			embedBuilder.appendField("No race found", "The race '" + value + "' does not exist.", false);
		} else if (racesFound.size() == 1) {
			Race race = racesFound.iterator().next();
			embedBuilder.withTitle("Information about the race " + race.getName());
			embedBuilder.appendField("Lore", race.getLore(), false);
			StringBuilder treats = new StringBuilder();
			for(RacialBonus bonus: race.getBonuses()) {
				treats.append("`" + bonus.getName() + "`\n");
				treats.append("Description: " + bonus.getDescription() + "\n");
				String bonusStat = bonus.getCombatSkill() != null? bonus.getCombatSkill().toString() :
									bonus.getSecondarySkill() != null? bonus.getSecondarySkill().toString() :
										bonus.getAttribute() != null? bonus.getAttribute().toString() :
											bonus.getWeaponType().toStringWithSuffix();
				treats.append("`Raises " + bonusStat + " by " + bonus.getModifier() + "%`\n\n");
			}
			embedBuilder.appendField("Racial treats", treats.toString(), false);
			embedBuilder.appendDesc("This race is " + (race.isHumanPlayable()? "" : "not ") + "playable.");
		} else {
			// wanna print multiples?
			StringBuilder races = new StringBuilder();
			races.append("```md\n");
			for(Race race: racesFound) {
				races.append("[" + race.getId() + "][" + race.getName() + "]\n");
			}
			races.append("```");
			embedBuilder.appendField("Found " + racesFound.size() + " races, please specify further.", races.toString(), false);
		}

		FantasyUnlimited.getInstance().sendMessage(event.getChannel(), embedBuilder.build());
	}

	@Override
	public String getDescription() {
		return "Displays information about races";
	}

	@Override
	public Type getType() {
		return Type.CHARACTER;
	}

	@Override
	public String getParameter() {
		return "name/id of the race";
	}

}
