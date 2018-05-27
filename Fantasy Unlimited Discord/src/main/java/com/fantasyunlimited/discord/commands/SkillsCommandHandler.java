package com.fantasyunlimited.discord.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.text.WordUtils;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageFormatUtils;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.SkillType;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class SkillsCommandHandler extends CommandHandler implements OptionDescription {
	public static final String CMD = "skills";

	public SkillsCommandHandler(Properties properties) {
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

		buildEmbedBuilderWithAuthorInformation(event, "Information about the skills of a class");

		boolean requirePagination = false;

		final List<Skill> skills = new ArrayList<>();
		CharacterClass charClass = null;

		Collection<CharacterClass> classesFound = FantasyUnlimited.getInstance().getClassBag().getItemsByValue(value);
		if (classesFound.isEmpty()) {
			embedBuilder.appendField("No class found", "The class '" + value + "' does not exist.", false);
		} else if (classesFound.size() == 1) {
			requirePagination = true;

			charClass = classesFound.iterator().next();
			embedBuilder.withTitle("Information about skills of the class " + charClass.getName());

			skills.addAll(charClass.getSkills());

			Skill skill = skills.get(0);

			embedBuilder.withThumbnail("https://cdn.discordapp.com/emojis/" + skill.getIconId() + ".png?v=1");

			StringBuilder basics = new StringBuilder();
			basics.append("```md\n");
			basics.append(MessageFormatUtils.fillStringSuffix("Name:", 15) + skill.getName() + "\n");
			String description = MessageFormatUtils.fillStringSuffix("Description:", 15) + skill.getDescription();
			description = WordUtils.wrap(description, 45, "\n               ", false);
			basics.append(description + "\n");
			basics.append(MessageFormatUtils.fillStringSuffix("Base cost:", 15) + skill.getCostOfExecution() + " "
					+ charClass.getEnergyType().toString() + "\n");

			basics.append(MessageFormatUtils.fillStringSuffix("Type:", 15));
			switch (skill.getType()) {
			case BUFF:
				basics.append("Buff" + "\n");
				break;
			case DEBUFF:
				basics.append("Debuff" + "\n");
				break;
			case DEFENSIVE:
				basics.append("Defensive/Heal" + "\n");
				break;
			case OFFENSIVE:
				basics.append("Offensive/Damage" + "\n");
				break;
			default:
				basics.append("???" + "\n");
				break;
			}

			if (skill.getTargetType() != null) {
				basics.append(MessageFormatUtils.fillStringSuffix("Target type:", 15));
				switch (skill.getTargetType()) {
				case AREA:
					basics.append("Area effect" + "\n");
					break;
				case ENEMY:
					basics.append("Opponents" + "\n");
					break;
				case FRIEND:
					basics.append("Friends/Self" + "\n");
					break;
				case OWN:
					basics.append("Self" + "\n");
					break;
				default:
					basics.append("???" + "\n");
					break;

				}
			}

			basics.append("```");
			embedBuilder.appendField("Basic data", basics.toString(), false);

			StringBuilder damage = new StringBuilder();
			damage.append("```md\n");

			damage.append(MessageFormatUtils.fillStringSuffix("Base Amount:", 17) + skill.getMinDamage() + "-"
					+ skill.getMaxDamage() + "\n");
			damage.append(MessageFormatUtils.fillStringSuffix("Stat modifier:", 17)
					+ (skill.getAttribute() != null ? skill.getAttribute().toString() : "None") + "\n");
			damage.append(MessageFormatUtils.fillStringSuffix("Weapon modifier:", 17)
					+ (skill.getWeaponModifier() != null ? skill.getWeaponModifier().toString() : "None") + "\n");

			damage.append("```");
			embedBuilder.appendField("Damage values", damage.toString(), false);

			if (skill.getType() == SkillType.BUFF || skill.getType() == SkillType.DEBUFF) {
				StringBuilder buffInfo = new StringBuilder();
				buffInfo.append("```md\n");

				buffInfo.append(MessageFormatUtils.fillStringSuffix("Duration:", 15) + skill.getDurationInTurns()
						+ " Rounds\n");
				buffInfo.append(MessageFormatUtils.fillStringSuffix("Incapacitates:", 15)
						+ (skill.isSkillIncapacitates() ? "Yes" : "No") + "\n");
				if (skill.getBuffModifiesAttribute() != null) {
					buffInfo.append(MessageFormatUtils.fillStringSuffix("Attribute:", 15)
							+ skill.getBuffModifiesAttribute().toString() + "\n");
					buffInfo.append(MessageFormatUtils.fillStringSuffix("By %", 15) + skill.getBuffModifier());
				}
				if (skill.getBuffModifiesCombatSkill() != null) {
					buffInfo.append(MessageFormatUtils.fillStringSuffix("Skill:", 15)
							+ skill.getBuffModifiesCombatSkill().toString() + "\n");
					buffInfo.append(MessageFormatUtils.fillStringSuffix("By %", 15) + skill.getBuffModifier());
				}

				buffInfo.append("```");
				embedBuilder.appendField("Buff/Debuff information", buffInfo.toString(), false);
			}

			StringBuilder ranks = new StringBuilder();
			ranks.append("```md\n");
			ranks.append(MessageFormatUtils.fillStringSuffix("| #", 5));
			ranks.append(MessageFormatUtils.fillStringSuffix("| Lvl", 6));
			ranks.append(MessageFormatUtils.fillStringSuffix("| Stat", 7));
			ranks.append(MessageFormatUtils.fillStringSuffix("| Dmg+", 7));
			ranks.append(MessageFormatUtils.fillStringSuffix("| Cost+", 8));
			ranks.append("\n");

			skill.getRanks().stream().sorted((r1, r2) -> Integer.compare(r1.getRank(), r2.getRank())).forEach(rank -> {
				ranks.append(MessageFormatUtils.fillStringSuffix("| " + rank.getRank(), 5));
				ranks.append(MessageFormatUtils.fillStringSuffix("| " + rank.getRequiredPlayerLevel(), 6));
				ranks.append(MessageFormatUtils.fillStringSuffix("| " + rank.getRequiredAttributeValue(), 7));
				ranks.append(MessageFormatUtils.fillStringSuffix("| +" + rank.getDamageModifier(), 7));
				ranks.append(MessageFormatUtils.fillStringSuffix("| +" + rank.getCostModifier(), 8));
				ranks.append("\n");
			});

			ranks.append("```");
			embedBuilder.appendField("Ranks", ranks.toString(), false);
		} else {
			// wanna print multiples?
			StringBuilder classes = new StringBuilder();
			classes.append("```md\n");
			for (CharacterClass found : classesFound) {
				classes.append("[" + found.getId() + "][" + found.getName() + "]\n");
			}
			classes.append("```");
			embedBuilder.appendField("Found " + classesFound.size() + " classes, please specify further.",
					classes.toString(), false);
		}

		IMessage msg = FantasyUnlimited.getInstance().sendMessage(event.getChannel(), embedBuilder.build());

		if (requirePagination) {

			embedBuilder.withFooterText("Page 1 of " + skills.size());

			FantasyUnlimited.getInstance().addReactions(msg,
					new String[] { Unicodes.arrow_backward, Unicodes.arrow_forward });

			MessageInformation information = new MessageInformation();
			information.setCanBeRemoved(false);
			information.setOriginDate(event.getMessage().getTimestamp());
			information.setOriginator(event.getMessage().getAuthor());
			MessageStatus status = new MessageStatus();
			status.setName(Name.SKILLS_INFO);
			information.setStatus(status);
			information.setMessage(msg);
			information.getVars().put("skills", skills);
			information.getVars().put("class", charClass);
			information.getStatus().setCurrentPage(1);
			FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(msg.getLongID(), information);
		}
	}

	@Override
	public String getDescription() {
		return "Displays information about skills of a class";
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
