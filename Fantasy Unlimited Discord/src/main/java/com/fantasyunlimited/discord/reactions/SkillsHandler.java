package com.fantasyunlimited.discord.reactions;

import java.util.List;
import java.util.Properties;

import org.apache.commons.text.WordUtils;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageFormatUtils;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.SerializableEmbedBuilder;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.SkillType;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.util.RequestBuffer;

public class SkillsHandler extends ReactionsHandler {

	public SkillsHandler(Properties properties) {
		super(properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		MessageInformation information = getInformationSecure(event);
		String emojiName = getEmojiName(event);

		// Clear user reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeReaction(event.getUser(), event.getReaction());
		});

		@SuppressWarnings("unchecked")
		List<Skill> skills = (List<Skill>) information.getVars().get("skills");
		int maxPage = skills.size();
		int currentPage = information.getStatus().getCurrentPage();

		if (emojiName.equals(Unicodes.arrow_backward)) {
			// backwards operation
			if (currentPage == 1) {
				return; // sanity check, shouldn't happen really
			}
			currentPage -= 1;
			information.getStatus().setCurrentPage(currentPage);

		} else if (emojiName.equals(Unicodes.arrow_forward)) {
			// forward operation
			if (currentPage == maxPage) {
				return; // shouldn't really happen
			}
			currentPage += 1;
			information.getStatus().setCurrentPage(currentPage);
		}

		Skill skill = skills.get(currentPage - 1);

		
		CharacterClass charClass = (CharacterClass) information.getVars().get("class");
		buildEmbedBuilderWithAuthorInformation(event, "Information about skills of the class " + charClass.getName());

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

			buffInfo.append(
					MessageFormatUtils.fillStringSuffix("Duration:", 15) + skill.getDurationInTurns() + " Rounds\n");
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

		embedBuilder.withFooterText("Page " + currentPage + " of " + maxPage);

		FantasyUnlimited.getInstance().editMessage(information.getMessage(), embedBuilder.build());

	}

	protected void buildEmbedBuilderWithAuthorInformation(ReactionAddEvent event, String title) {
		embedBuilder = new SerializableEmbedBuilder().withAuthorName(event.getUser().getDisplayName(event.getGuild()))
				.withAuthorIcon(event.getUser().getAvatarURL()).withTitle(title);
	}
}
