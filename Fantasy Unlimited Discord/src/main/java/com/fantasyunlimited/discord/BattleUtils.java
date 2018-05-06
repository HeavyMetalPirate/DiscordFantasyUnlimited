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
				int skillCost = skill.getCostOfExecution();
				skillCost += rank.getCostModifier();
				if (character.getCurrentAtkResource() < skillCost) {
					continue;
				}

				skillBuilder.append("<:" + skill.getIconName() + ":" + skill.getIconId() + "> " + skill.getName()
						+ " (Rank " + rank.getRank() + ") - " + skillCost + " "
						+ character.getCharClass().getEnergyType().toString() + "\n");
				skillIcons.put(skill.getIconName(), Long.parseLong(skill.getIconId()));
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
		for (Long id : information.getPlayers().keySet()) {
			BattlePlayer character = information.getPlayers().get(id).getCharacter();

			players.append("```md\n");
			players.append("[" + character.getLevel() + "][" + character.getName() + "]\n");
			players.append("<    Health   : " + character.getCurrentHealth() + "/" + character.getMaxHealth() + ">\n");
			players.append("#    " + character.getCharClass().getEnergyType().toString() + ": "
					+ character.getCurrentAtkResource() + "/" + character.getMaxAtkResource() + "#\n");
			players.append("```");
		}

		StringBuilder enemies = new StringBuilder();
		enemies.append("```md\n");
		for (int index : information.getHostiles().keySet()) {
			BattleNPC npc = information.getHostiles().get(index);
			enemies.append("(" + index + ") [" + npc.getLevel() + "][" + npc.getBase().getName() + "]\n");
			enemies.append("<    Health   : " + npc.getCurrentHealth() + "/" + npc.getMaxHealth() + ">\n");
			enemies.append("#    " + npc.getCharClass().getEnergyType().toString() + ": " + npc.getCurrentAtkResource()
					+ "/" + npc.getMaxAtkResource() + "#\n");
		}
		enemies.append("```");

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
					builder.append("Passed");
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
