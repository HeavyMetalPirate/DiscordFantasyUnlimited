package com.fantasyunlimited.discord;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.SkillRank;
import com.fantasyunlimited.entity.Attributes;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class BattleUtils {
	
	public static final void prepareNextroundActionbars(BattleInformation information) {

		for (Long id : information.getPlayers().keySet()) {
			BattlePlayerInformation playerInfo = information.getPlayers().get(id);
			if (information.isFinished()) {
				IMessage message = playerInfo.getMessage();
				RequestBuffer.request(() -> {
					message.delete();
				});
				continue;
			}

			BattlePlayer character = playerInfo.getCharacter();

			if (character.isDefeated()) {
				IMessage message = playerInfo.getMessage();
				FantasyUnlimited.getInstance().editMessage(message,
						"<@" + character.getDiscordId() + "> - You are defeated!");
				continue;
			}

			int level = character.getLevel();
			Attributes attributes = character.getAttributes();

			// For now, this way - later on the player has to choose a
			// preset for his action bar
			List<Skill> skills = character.getCharClass().getAvailableSkills(level, attributes);

			StringBuilder skillBuilder = new StringBuilder();
			Map<String, Long> skillIcons = new LinkedHashMap<>();
			for (Skill skill : skills) {

				SkillRank rank = skill.getHighestAvailable(level, attributes);
				//put the icon to the bar regardless
				skillIcons.put(skill.getIconName(), Long.parseLong(skill.getIconId()));
				
				int skillCost = skill.getCostOfExecution();
				skillCost += rank.getCostModifier();
				if (character.getCurrentAtkResource() < skillCost) {
					continue;
				}

				skillBuilder.append("<:" + skill.getIconName() + ":" + skill.getIconId() + "> `" + skill.getName()
						+ " (Rank " + rank.getRank() + ") - " + skillCost + " "
						+ character.getCharClass().getEnergyType().toString() + "`\n");
				
				if(skillIcons.size() == 5) {
					//max 5 items on the actionbar
					break;
				}
			}

			IMessage actionbar = FantasyUnlimited.getInstance().editMessage(playerInfo.getMessage(),
					"<@" + character.getDiscordId() + "> - Action Bar\n" + skillBuilder.toString());
			if (skillIcons.size() == 0) {
				FantasyUnlimited.getInstance().addReactions(actionbar, Unicodes.crossmark);
			} else {
				FantasyUnlimited.getInstance().addCustomReactions(actionbar, skillIcons);
			}
			MessageInformation msgInfo = new MessageInformation();
			msgInfo.setCanBeRemoved(false);
			msgInfo.setMessage(actionbar);
			msgInfo.setOriginator(FantasyUnlimited.getInstance().fetchUser(character.getDiscordId()));
			MessageStatus status = new MessageStatus();
			status.setPaginator(false);
			status.setName(Name.BATTLE_ACTIONBAR);
			msgInfo.setStatus(status);

			FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(actionbar.getLongID(), msgInfo);

			BattlePlayerInformation battlePlayerInfo = information.getPlayers().get(character.getCharacterId());
			battlePlayerInfo.setMessage(actionbar);
			FantasyUnlimited.getInstance().getBattles().put(character.getCharacterId(), battlePlayerInfo);
		}
	}

	public static final EmbedBuilder createBattleOutputEmbeds(BattleInformation information) {
		StringBuilder battlelog = createBattleLog(information);

		StringBuilder players = new StringBuilder();
		players.append("```md\n");
		for (Long id : information.getPlayers().keySet()) {
			BattlePlayer character = information.getPlayers().get(id).getCharacter();

			String level = MessageFormatUtils.fillStringPrefix("" + character.getLevel(), 3);
			String characterName = character.getName();
			if (characterName.length() > 20) {
				characterName = characterName.substring(0, 17) + "...";
			}
			String leveldisplay = "[" + level + "][" + MessageFormatUtils.fillStringSuffix(characterName, 20) + "]";

			String healthdisplay = "<" + MessageFormatUtils.fillStringPrefix("Health", 11);
			healthdisplay += " : ";
			healthdisplay += MessageFormatUtils.fillStringPrefix("" + character.getCurrentHealth(), 5);
			healthdisplay += "/" + character.getMaxHealth();
			healthdisplay = MessageFormatUtils.fillStringSuffix(healthdisplay, 26);
			healthdisplay += ">";

			String energytype = character.getCharClass().getEnergyType().toString();
			String resourcedisplay = "#" + MessageFormatUtils.fillStringPrefix(energytype, 11);
			resourcedisplay += " : ";
			resourcedisplay += MessageFormatUtils.fillStringPrefix("" + character.getCurrentAtkResource(), 5);
			resourcedisplay += "/" + character.getMaxAtkResource();
			resourcedisplay = MessageFormatUtils.fillStringSuffix(resourcedisplay, 26);
			resourcedisplay += "#";

			players.append(leveldisplay + "\n");
			players.append(healthdisplay + "\n");
			players.append(resourcedisplay + "\n");

		}
		players.append("```\n");

		StringBuilder enemies = new StringBuilder();
		enemies.append("```md\n");
		for (int index : information.getHostiles().keySet()) {
			BattleNPC npc = information.getHostiles().get(index);

			String level = MessageFormatUtils.fillStringPrefix("" + npc.getLevel(), 3);
			String characterName = npc.getBase().getName();
			if (characterName.length() > 20) {
				characterName = characterName.substring(0, 17) + "...";
			}
			String leveldisplay = "[" + level + "][" + MessageFormatUtils.fillStringSuffix(characterName, 20) + "]";

			String healthdisplay = "<" + MessageFormatUtils.fillStringPrefix("Health", 11);
			healthdisplay += " : ";
			healthdisplay += MessageFormatUtils.fillStringPrefix("" + npc.getCurrentHealth(), 5);
			healthdisplay += "/" + npc.getMaxHealth();
			healthdisplay = MessageFormatUtils.fillStringSuffix(healthdisplay, 26);
			healthdisplay += ">";

			String energytype = npc.getCharClass().getEnergyType().toString();
			String resourcedisplay = "#" + MessageFormatUtils.fillStringPrefix(energytype, 11);
			resourcedisplay += " : ";
			resourcedisplay += MessageFormatUtils.fillStringPrefix("" + npc.getCurrentAtkResource(), 5);
			resourcedisplay += "/" + npc.getMaxAtkResource();
			resourcedisplay = MessageFormatUtils.fillStringSuffix(resourcedisplay, 26);
			resourcedisplay += "#";

			enemies.append(leveldisplay + "\n");
			enemies.append(healthdisplay + "\n");
			enemies.append(resourcedisplay + "\n");

		}
		enemies.append("```\n");
		
		return new SerializableEmbedBuilder().withTitle("Battle").appendField("Players (1)", players.toString(), true)
				.appendField("Enemies (" + information.getHostiles().size() + ")", enemies.toString(), true)
				.appendField("Battle Log - Round " + information.getCurrentRound(), battlelog.toString(), false);
	}

	private static final StringBuilder createBattleLog(BattleInformation battle) {
		StringBuilder builder = new StringBuilder();
		builder.append("```\n");
		if (battle.getRounds().size() == 0) {
			builder.append("No actions have been taken yet.");
		} else {
			List<BattleAction> round = battle.getRounds().get(battle.getCurrentRound());
			if (round == null) {
				// redisplay previous one
				round = battle.getRounds().get(battle.getCurrentRound() - 1);
			}
			for (BattleAction action : round) {
				if (action.getExecuting() instanceof BattlePlayer) {
					builder.append(((BattlePlayer) action.getExecuting()).getName() + " -> ");
				} else {
					builder.append(((BattleNPC) action.getExecuting()).getBase().getName() + " -> ");
				}

				if (action.isPass()) {
					builder.append("Passed\n");
					continue;
				}
				if (action.getExecuting().isDefeated()) {
					builder.append("Died before they could make a move!\n");
					continue;
				}

				action.executeAction();

				builder.append(action.getUsedSkill().getName() + " for " + action.getActionAmount() + " -> ");

				if (action.isArea()) {
					builder.append("Area attack");
				} else if (action.getTarget() instanceof BattlePlayer) {
					builder.append(((BattlePlayer) action.getTarget()).getName());
				} else if (action.getTarget() instanceof BattleNPC) {
					builder.append(((BattleNPC) action.getTarget()).getBase().getName());
				} else {
					builder.append("65wat.jpg");
				}

				builder.append("\n");
			}
		}
		checkBattleFinished(battle, builder);

		return builder;
	}

	private static final void checkBattleFinished(BattleInformation battle, StringBuilder builder) {
		if (battle.getAliveEnemyCount() == 0) {
			builder.append("All enemies have perished!\n");
		}
		if (battle.getAlivePlayerCount() == 0) {
			builder.append("All players have perished!\n");
		}
		builder.append("```");
	}
}
