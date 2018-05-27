package com.fantasyunlimited.discord.commands;

import java.util.Collection;
import java.util.Properties;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.ItemUtils;
import com.fantasyunlimited.discord.MessageFormatUtils;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.ClassBonus;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.Weapon;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class ClassCommandHandler extends CommandHandler implements OptionDescription {
	public static final String CMD = "class";

	public ClassCommandHandler(Properties properties) {
		super(properties, CMD);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		String value = stripCommandFromMessage(event.getMessage());

		if (value == null || value.isEmpty()) {
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(),
					"Usage: `" + properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "class <id/name>");
			return;
		}

		buildEmbedBuilderWithAuthorInformation(event, "Information about the class");

		Collection<CharacterClass> classesFound = FantasyUnlimited.getInstance().getClassBag().getItemsByValue(value);
		if (classesFound.isEmpty()) {
			embedBuilder.appendField("No class found", "The class '" + value + "' does not exist.", false);
		} else if (classesFound.size() == 1) {
			CharacterClass charClass = classesFound.iterator().next();
			embedBuilder.withTitle("Information about the class " + charClass.getName());
			embedBuilder.appendField("Lore", charClass.getLore(), false);

			StringBuilder baseStats = new StringBuilder();
			baseStats.append("```md\n");
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Strength", 15)
					+ MessageFormatUtils.fillStringSuffix(charClass.getAttributes().getStrength() + " > <+"
							+ charClass.getAttributes().getStrengthGrowth() + ">", 10));
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Dexterity", 15)
					+ MessageFormatUtils.fillStringSuffix(charClass.getAttributes().getDexterity() + " > <+"
							+ charClass.getAttributes().getDexterityGrowth() + ">", 10)
					+ "\n");
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Endurance", 15)
					+ MessageFormatUtils.fillStringSuffix(charClass.getAttributes().getEndurance() + " > <+"
							+ charClass.getAttributes().getEnduranceGrowth() + ">", 10));
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Defense", 15)
					+ MessageFormatUtils.fillStringSuffix(charClass.getAttributes().getDefense() + " > <+"
							+ charClass.getAttributes().getDefenseGrowth() + ">", 10)
					+ "\n");
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Wisdom", 15) + MessageFormatUtils.fillStringSuffix(
					charClass.getAttributes().getWisdom() + " > <+" + charClass.getAttributes().getWisdomGrowth() + ">",
					10));
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Intelligence", 15)
					+ MessageFormatUtils.fillStringSuffix(charClass.getAttributes().getIntelligence() + " > <+"
							+ charClass.getAttributes().getIntelligenceGrowth() + ">", 10)
					+ "\n");
			baseStats.append(MessageFormatUtils.fillStringSuffix("< Luck", 15) + MessageFormatUtils.fillStringSuffix(
					charClass.getAttributes().getLuck() + " > <+" + charClass.getAttributes().getLuckGrowth() + ">", 10)
					+ "\n");
			baseStats.append("```");
			embedBuilder.appendField("Base stats (+ growth / level)", baseStats.toString(), false);

			StringBuilder startingGear = new StringBuilder();
			startingGear.append("```md\n");
			Weapon weapon = ItemUtils.getWeapon(charClass.getStartingMainhand());
			if (weapon != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Right hand ", 15) + " > > " + weapon.getName() + "\n");
			}
			weapon = ItemUtils.getWeapon(charClass.getStartingOffhand());
			if (weapon != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Left hand ", 15) + " > > " + weapon.getName() + "\n");
			}
			Equipment equipment = ItemUtils.getEquipment(charClass.getStartingHelmet());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Helmet ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingChest());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Chest ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingGloves());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Gloves ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingPants());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Pants ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingBoots());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Boots ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingRing1());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Ring (1) ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingRing2());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Ring (2) ", 15) + " > > " + equipment.getName() + "\n");
			}
			equipment = ItemUtils.getEquipment(charClass.getStartingNeck());
			if (equipment != null) {
				startingGear.append(
						MessageFormatUtils.fillStringSuffix("< Neck ", 15) + " > > " + equipment.getName() + "\n");
			}
			startingGear.append("```");
			embedBuilder.appendField("Starting equipment", startingGear.toString(), false);

			StringBuilder treats = new StringBuilder();
			for (ClassBonus bonus : charClass.getBonuses()) {
				String bonusStat = bonus.getCombatSkill() != null ? bonus.getCombatSkill().toString()
						: bonus.getAttribute() != null ? bonus.getAttribute().toString()
								: bonus.getWeaponType().toStringWithSuffix();

				treats.append("```fix\n" + bonus.getName() + " - Raises " + bonusStat + " by " + bonus.getModifier()
						+ "% " + "\n");
				treats.append("= " + bonus.getDescription() + "```\n");
			}

			embedBuilder.appendField("Class treats", treats.toString(), false);
			embedBuilder.appendDesc("This class is " + (charClass.isHumanPlayable() ? "" : "not ") + "playable.");
			embedBuilder.withFooterText("For a skill description, type `"
					+ properties.getProperty(FantasyUnlimited.PREFIX_KEY) + "skills " + value + "`.");
		} else {
			// wanna print multiples?
			StringBuilder classes = new StringBuilder();
			classes.append("```md\n");
			for (CharacterClass charClass : classesFound) {
				classes.append("[" + charClass.getId() + "][" + charClass.getName() + "]\n");
			}
			classes.append("```");
			embedBuilder.appendField("Found " + classesFound.size() + " classes, please specify further.",
					classes.toString(), false);
		}

		FantasyUnlimited.getInstance().sendMessage(event.getChannel(), embedBuilder.build());
	}

	@Override
	public String getDescription() {
		return "Displays information about classes";
	}

	@Override
	public Type getType() {
		return Type.CHARACTER;
	}

	@Override
	public String getParameter() {
		return "name/id of the class";
	}

}
