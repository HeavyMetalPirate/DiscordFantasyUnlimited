package com.fantasyunlimited.discord.reactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import com.fantasyunlimited.discord.BattleAction;
import com.fantasyunlimited.discord.BattleInformation;
import com.fantasyunlimited.discord.BattlePlayerInformation;
import com.fantasyunlimited.discord.BattleUtils;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.Unicodes;
import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.SkillType;
import com.fantasyunlimited.discord.xml.Skill.TargetType;
import com.fantasyunlimited.entity.PlayerCharacter;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.handle.obj.IMessage;

public class BattleHandler extends ReactionsHandler {

	private Random randomGenerator = new Random();

	public BattleHandler(Properties properties) {
		super(properties);
	}

	@Override
	public void handle(ReactionAddEvent event) {
		// find out if the emoji used is a valid skill for the player of this
		// player

		MessageInformation msgInfo = getInformationSecure(event);

		switch (msgInfo.getStatus().getName()) {
		case BATTLE_ACTIONBAR:
			handleActionBarStatus(event, msgInfo);
			return;
		case BATTLE_TARGETSELECTION:
			handleTargetSelectionStatus(event, msgInfo);
			return;
		case BATTLE_WAITING:
			// Clear user reactions
			RequestBuffer.request(() -> {
				event.getMessage().removeReaction(event.getUser(), event.getReaction());
			});
			return;
		default:
			throw new IllegalStateException();
		}
	}

	private void handleTargetSelectionStatus(ReactionAddEvent event, MessageInformation msgInfo) {
		PlayerCharacter playerCharacter = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(msgInfo.getOriginator().getLongID()).getCurrentCharacter();

		BattlePlayerInformation battlePlayerInfo = FantasyUnlimited.getInstance().getBattles()
				.get(playerCharacter.getId());
		BattleInformation battleInfo = battlePlayerInfo.getBattle();
		if (battleInfo.isFinished()) {
			return;
		}
		ReactionEmoji usedEmoji = event.getReaction().getEmoji();

		// Clear user reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeReaction(event.getUser(), event.getReaction());
		});

		boolean enemy = (boolean) msgInfo.getVars().get("enemy");

		int index = Arrays.asList(Unicodes.numNames).indexOf(usedEmoji.getName());
		if (index < 0) {
			return; // not found
		}

		if (enemy) {
			if (index >= battleInfo.getHostiles().size()) {
				return;
			}

			BattleNPC hostile = battleInfo.getHostiles().get(++index);
			if (hostile.isDefeated()) {
				return;
			}
			battlePlayerInfo.setHostileTarget(hostile);
		} else {
			if (index >= battleInfo.getPlayers().size()) {
				return;
			}
			@SuppressWarnings("unchecked")
			Map<Integer, BattlePlayer> numberPlayerMap = (Map<Integer, BattlePlayer>) msgInfo.getVars()
					.get("availableTargets");
			BattlePlayer player = numberPlayerMap.get(index);
			if (player.isDefeated()) {
				return;
			}
			battlePlayerInfo.setFriendlyTarget(player);
		}
		queueAction(battlePlayerInfo);
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

		if (character.isDefeated()) {
			return;
		}

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

		if (battlePlayerInfo.getSkillUsed().getTargetType() == null
				&& (battlePlayerInfo.getSkillUsed().getType() == SkillType.OFFENSIVE
						|| battlePlayerInfo.getSkillUsed().getType() == SkillType.DEBUFF)) {
			battlePlayerInfo.getSkillUsed().setTargetType(TargetType.ENEMY); // fallback
		} else if (battlePlayerInfo.getSkillUsed().getTargetType() == null
				&& (battlePlayerInfo.getSkillUsed().getType() == SkillType.DEFENSIVE
						|| battlePlayerInfo.getSkillUsed().getType() == SkillType.BUFF)) {
			battlePlayerInfo.getSkillUsed().setTargetType(TargetType.FRIEND); // fallback
		}

		if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.ENEMY
				&& battleInfo.getAliveEnemyCount() > 1) {
			handleTargetSelectionRequired(msgInfo, battlePlayerInfo, battleInfo, true);
			return;
		} else if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.FRIEND
				&& battleInfo.getAlivePlayerCount() > 1) {
			handleTargetSelectionRequired(msgInfo, battlePlayerInfo, battleInfo, false);
			return;
		} else {
			// queue the action
			if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.FRIEND
					|| battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.OWN) {
				battlePlayerInfo.setFriendlyTarget(character);
			} else if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.ENEMY) {
				for (int hostileId : battleInfo.getHostiles().keySet()) {
					BattleNPC hostile = battleInfo.getHostiles().get(hostileId);
					if (hostile.isDefeated()) {
						continue;
					}
					battlePlayerInfo.setHostileTarget(hostile);
					break;
				}
			}
			queueAction(battlePlayerInfo);
		}
	}

	private void handleTargetSelectionRequired(MessageInformation msgInfo, BattlePlayerInformation playerInfo,
			BattleInformation battleInfo, boolean enemy) {

		msgInfo.getVars().put("enemy", enemy);
		String[] usedNumbers = new String[0];
		StringBuilder builder = new StringBuilder();
		builder.append("```md\n");
		if (enemy) {
			for (int hostileId : battleInfo.getHostiles().keySet()) {
				BattleNPC hostile = battleInfo.getHostiles().get(hostileId);
				usedNumbers = ArrayUtils.add(usedNumbers, Unicodes.numNames[hostileId - 1]);
				if (hostile.isDefeated()) {
					continue;
				}
				builder.append(
						"(" + hostileId + ") [" + hostile.getLevel() + "][" + hostile.getBase().getName() + "]\n");
			}
		} else {
			Map<Integer, BattlePlayer> numberPlayerMap = new HashMap<>();
			int counter = 0;
			for (long characterId : battleInfo.getPlayers().keySet()) {
				BattlePlayer player = battleInfo.getPlayers().get(characterId).getCharacter();
				usedNumbers = ArrayUtils.add(usedNumbers, Unicodes.numNames[counter]);
				if (player.isDefeated()) {
					continue;
				}
				numberPlayerMap.put(counter, player);
				counter++;
				builder.append("(" + counter + ") [" + player.getLevel() + "][" + player.getName() + "]\n");
			}
			msgInfo.getVars().put("availableTargets", numberPlayerMap);
		}
		builder.append("```");

		IMessage msg = FantasyUnlimited.getInstance().editMessage(playerInfo.getMessage(),
				"<@" + playerInfo.getCharacter().getDiscordId() + "> - Select your target for '"
						+ playerInfo.getSkillUsed().getName() + "'\n" + builder.toString());
		playerInfo.setMessage(msg);
		FantasyUnlimited.getInstance().addReactions(msg, usedNumbers);
		msgInfo.getStatus().setName(Name.BATTLE_TARGETSELECTION);

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

	private class BattleActionComparator implements Comparator<BattleAction> {

		@Override
		public int compare(BattleAction o1, BattleAction o2) {
			Integer dex1, dex2;
			dex1 = o1.getExecutingPlayer() == null ? o1.getExecutingHostile().getAttributes().getDexterity()
					: o1.getExecutingPlayer().getAttributes().getDexterity();
			dex2 = o2.getExecutingPlayer() == null ? o2.getExecutingHostile().getAttributes().getDexterity()
					: o2.getExecutingPlayer().getAttributes().getDexterity();

			return dex1.compareTo(dex2);
		}
	}

	private void calculateAndPrintResults(BattleInformation battle) {
		queueHostileActions(battle);

		List<BattleAction> actions = battle.getRounds().get(battle.getCurrentRound());
		Collections.sort(actions, new BattleActionComparator());
		// actions sorted by dexterity

		embedBuilder = BattleUtils.createBattleOutputEmbeds(battle);
		FantasyUnlimited.getInstance().editMessage(battle.getMessage(), embedBuilder.build());
		
		battle.setCurrentRound(battle.getCurrentRound() + 1);
		BattleUtils.prepareNextroundActionbars(battle);

		if (battle.isFinished()) {
			for (long characterId : battle.getPlayers().keySet()) {
				FantasyUnlimited.getInstance().getBattles().remove(characterId);
			}
		}
	}

	private void queueHostileActions(BattleInformation battle) {
		for (int hostileId : battle.getHostiles().keySet()) {
			BattleNPC hostile = battle.getHostiles().get(hostileId);
			if (hostile.isDefeated()) {
				continue;
			}
			BattleAction action = new BattleAction();
			action.setExecutingHostile(hostile);

			// select skill
			List<Skill> executableSkills = new ArrayList<>();
			for (Skill skill : hostile.getCharClass().getAvailableSkills(hostile.getLevel(), hostile.getAttributes())) {
				int skillCost = skill.getCostOfExecution();
				skillCost += skill.getHighestAvailable(hostile.getLevel(), hostile.getAttributes()).getCostModifier();

				if (hostile.getCurrentAtkResource() < skillCost) {
					continue;
				}
				executableSkills.add(skill);
			}
			if (executableSkills.size() == 0) {
				action.setPass(true);
				battle.getRounds().get(battle.getCurrentRound()).add(action);
				continue;
			}

			Skill usedSkill = executableSkills.get(randomGenerator.nextInt(executableSkills.size()));
			action.setUsedSkill(usedSkill);
			// select target

			if (usedSkill.getTargetType() == null
					&& (usedSkill.getType() == SkillType.OFFENSIVE || usedSkill.getType() == SkillType.DEBUFF)) {
				usedSkill.setTargetType(TargetType.ENEMY); // fallback
			} else if (usedSkill.getTargetType() == null
					&& (usedSkill.getType() == SkillType.DEFENSIVE || usedSkill.getType() == SkillType.BUFF)) {
				usedSkill.setTargetType(TargetType.FRIEND); // fallback
			}

			switch (usedSkill.getTargetType()) {
			case AREA:
				action.setArea(true);
				break;
			case ENEMY:
				List<BattlePlayer> availableTargets = new ArrayList<>();
				for (long characterId : battle.getPlayers().keySet()) {
					BattlePlayer player = battle.getPlayers().get(characterId).getCharacter();
					if (player.isDefeated()) {
						continue;
					}
					availableTargets.add(player);
				}
				action.setPlayerTarget(availableTargets.get(randomGenerator.nextInt(availableTargets.size())));
				break;
			case FRIEND:
				List<BattleNPC> availableFriends = new ArrayList<>();
				for (int i : battle.getHostiles().keySet()) {
					BattleNPC h = battle.getHostiles().get(i);
					if (h.isDefeated()) {
						continue;
					}
					availableFriends.add(h);
				}
				action.setHostileTarget(availableFriends.get(randomGenerator.nextInt(availableFriends.size())));
				break;
			case OWN:
				action.setHostileTarget(hostile);
			}
			battle.getRounds().get(battle.getCurrentRound()).add(action);
		}
	}

	

}
