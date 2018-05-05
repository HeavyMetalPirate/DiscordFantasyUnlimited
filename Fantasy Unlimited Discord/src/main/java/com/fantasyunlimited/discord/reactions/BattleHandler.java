package com.fantasyunlimited.discord.reactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.ArrayUtils;

import com.fantasyunlimited.discord.BattleAction;
import com.fantasyunlimited.discord.BattleInformation;
import com.fantasyunlimited.discord.BattlePlayerInformation;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.SerializableEmbedBuilder;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.TargetType;
import com.fantasyunlimited.entity.PlayerCharacter;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.handle.obj.IMessage;

public class BattleHandler extends ReactionsHandler {

	public BattleHandler(Properties properties) {
		super(properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		// find out if the emoji used is a valid skill for the player of this
		// player

		MessageInformation msgInfo = getInformationSecure(event);

		if (msgInfo.getStatus().getName() == Name.BATTLE_ACTIONBAR) {
			handleActionBarStatus(event, msgInfo);
		} else {
			handleTargetSelectionStatus(event, msgInfo);
		}

	}

	private void handleTargetSelectionStatus(ReactionAddEvent event, MessageInformation msgInfo) {

	}

	private void handleActionBarStatus(ReactionAddEvent event, MessageInformation msgInfo) {
		PlayerCharacter playerCharacter = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(msgInfo.getOriginator().getLongID()).getCurrentCharacter();

		BattlePlayerInformation battlePlayerInfo = FantasyUnlimited.getInstance().getBattles()
				.get(playerCharacter.getId());
		if (battlePlayerInfo == null) {
			throw new IllegalStateException("Battle Information lost for character " + playerCharacter.getName());
		}
		BattleInformation battleInfo = battlePlayerInfo.getBattle();
		if (battleInfo.isFinished()) {
			return;
		}
		BattlePlayer character = battlePlayerInfo.getCharacter();

		ReactionEmoji usedEmoji = event.getReaction().getEmoji();

		for (Skill skill : character.getCharClass().getAvailableSkills(character.getLevel(),
				character.getAttributes())) {
			long iconId = Long.parseLong(skill.getIconId());
			if (iconId == usedEmoji.getLongID()) {
				battlePlayerInfo.setSkillUsed(skill);
				break;
			}
		}

		// Clear user reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeReaction(event.getUser(), event.getReaction());
		});

		if (battlePlayerInfo.getSkillUsed() == null) {
			return;
		}

		if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.ENEMY
				&& battleInfo.getAliveEnemyCount() > 1) {
			handleTargetSelectionRequired(battlePlayerInfo, battleInfo, true);
			return;
		} else if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.FRIEND
				&& battleInfo.getAlivePlayerCount() > 1) {
			handleTargetSelectionRequired(battlePlayerInfo, battleInfo, false);
			return;
		} else {
			// queue the action
			queueAction(battlePlayerInfo);
		}
	}

	private void handleTargetSelectionRequired(BattlePlayerInformation playerInfo, BattleInformation battleInfo,
			boolean enemy) {

		String[] usedNumbers = new String[0];
		StringBuilder builder = new StringBuilder();

		if (enemy) {
			for (int hostileId : battleInfo.getHostiles().keySet()) {
				BattleNPC hostile = battleInfo.getHostiles().get(hostileId);
				if (hostile.isDefeated()) {
					continue;
				}
				usedNumbers = ArrayUtils.add(usedNumbers, Unicodes.numNames[hostileId - 1]);
				builder.append(
						"(" + hostileId + ") [" + hostile.getLevel() + "][" + hostile.getBase().getName() + "]\n");
			}
		} else {
			Map<Integer, BattlePlayer> numberPlayerMap = new HashMap<>();
			int counter = 0;
			for (long characterId : battleInfo.getPlayers().keySet()) {
				BattlePlayer player = battleInfo.getPlayers().get(characterId).getCharacter();
				if (player.isDefeated()) {
					continue;
				}
				usedNumbers = ArrayUtils.add(usedNumbers, Unicodes.numNames[counter]);
				numberPlayerMap.put(counter, player);
				counter++;
				builder.append("(" + counter + ") [" + player.getLevel() + "][" + player.getName() + "]\n");
			}
		}

		IMessage msg = FantasyUnlimited.getInstance().editMessage(playerInfo.getMessage(),
				"<@" + playerInfo.getCharacter().getDiscordId() + "> - Select your target for '"
						+ playerInfo.getSkillUsed().getName() + "'\n" + builder.toString());
		playerInfo.setMessage(msg);
		FantasyUnlimited.getInstance().addReactions(msg, usedNumbers);

	}

	private void queueAction(BattlePlayerInformation playerInfo) {
		Skill usedSkill = playerInfo.getSkillUsed();

		BattleAction action = new BattleAction();
		action.setExecutingPlayer(playerInfo.getCharacter());
		action.setUsedSkill(usedSkill);
		action.setArea(false);

		if (usedSkill.getTargetType() == TargetType.OWN) {
			action.setPlayerTarget(playerInfo.getCharacter());
		} else if (usedSkill.getTargetType() == TargetType.AREA) {
			action.setArea(true);
		} else {
			action.setHostileTarget(playerInfo.getHostileTarget());
			action.setPlayerTarget(playerInfo.getFriendlyTarget());
		}

		BattleInformation battle = playerInfo.getBattle();
		if (battle.getRounds().get(battle.getCurrentRound()) == null) {
			battle.getRounds().put(battle.getCurrentRound(), new ArrayList<>());
		}
		battle.getRounds().get(battle.getCurrentRound()).add(action);

		if (battle.getRounds().get(battle.getCurrentRound()).size() == battle.getAlivePlayerCount()) {
			calculateAndPrintResults(battle);
		}
	}

	private void calculateAndPrintResults(BattleInformation battle) {
		// TODO
		// probably put this calculation stuff to a seperate utility class? Or
		// even right into the battle information?
		FantasyUnlimited.getInstance().sendMessage(battle.getMessage().getChannel(),
				"I should do calculations now but I'm too lazy.");
	}

	private void createEmbedBuilder(BattleInformation information) {
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

		embedBuilder = new SerializableEmbedBuilder().withTitle("Battle")
				.appendField("Players (1)", players.toString(), true)
				.appendField("Enemies (" + information.getHostiles().size() + ")", enemies.toString(), true);
	}

}
