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
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Dropable;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.SkillType;
import com.fantasyunlimited.discord.xml.Skill.TargetType;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.handle.obj.IMessage;

public class BattleHandler extends ReactionsHandler {

	private Random randomGenerator = new Random();
	private final BattleResultsHandler resultsHandler;

	public BattleHandler(Properties properties) {
		super(properties);
		this.resultsHandler = new BattleResultsHandler();
	}

	@Override
	public void handle(ReactionAddEvent event) {
		// find out if the emoji used is a valid skill for the player of this
		// player

		MessageInformation msgInfo = getInformationSecure(event);

		if (event.getReaction().getEmoji().getName().equals(Unicodes.end)) {
			fleeFromBattle(event, msgInfo);
			// Clear user reactions
			RequestBuffer.request(() -> {
				event.getMessage().removeReaction(event.getUser(), event.getReaction());
			});
			return;
		}

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

	private void fleeFromBattle(ReactionAddEvent event, MessageInformation msgInfo) {
		PlayerCharacter playerCharacter = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(msgInfo.getOriginator().getLongID()).getCurrentCharacter();

		BattlePlayerInformation battlePlayerInfo = FantasyUnlimited.getInstance().getBattles()
				.get(playerCharacter.getId());
		BattleInformation battleInfo = fetchBattleInformation(battlePlayerInfo.getCharacter());

		if (battleInfo.isFinished()) {
			return;
		}

		if (battleInfo.getAlivePlayerCount() > 1) {
			// you cannot flee if you're in a party with more than one player alive!
			// TODO maybe let the single player run from this battle and abandon the party
			FantasyUnlimited.getInstance().sendMessage(event.getChannel(), "You can't run away right now!");
			return;
		}

		for (Long id : battleInfo.getPlayers().keySet()) {
			BattlePlayerInformation playerInfo = battleInfo.getPlayers().get(id);

			IMessage message = playerInfo.getMessage();
			RequestBuffer.request(() -> {
				message.delete();
			});
		}

		for (BattlePlayerInformation playerInfo : battleInfo.getPlayers().values()) {
			FantasyUnlimited.getInstance().getBattles().remove(playerInfo.getCharacter().getCharacterId());
			FantasyUnlimited.getInstance().getBattleMap().remove(playerInfo.getCharacter().getCharacterId());
		}

		embedBuilder = BattleUtils.createBattleOutputEmbeds(battleInfo, false);
		embedBuilder.appendField("You managed to get away", "You got away - this time.", false);
		FantasyUnlimited.getInstance().editMessage(battleInfo.getMessage(), embedBuilder.build());
		battleInfo.flee();
	}

	private void handleTargetSelectionStatus(ReactionAddEvent event, MessageInformation msgInfo) {
		PlayerCharacter playerCharacter = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(msgInfo.getOriginator().getLongID()).getCurrentCharacter();

		BattlePlayerInformation battlePlayerInfo = FantasyUnlimited.getInstance().getBattles()
				.get(playerCharacter.getId());
		BattleInformation battleInfo = fetchBattleInformation(battlePlayerInfo.getCharacter());

		if (battleInfo.isFinished()) {
			return;
		}

		ReactionEmoji usedEmoji = event.getReaction().getEmoji();

		// Clear user reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeReaction(event.getUser(), event.getReaction());
		});

		BattlePlayer character = battlePlayerInfo.getCharacter();

		if (character.isDefeated()) {
			return;
		}

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
			battlePlayerInfo.setTarget(hostile);
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
			battlePlayerInfo.setTarget(player);
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
		BattleInformation battleInfo = fetchBattleInformation(battlePlayerInfo.getCharacter());
		;
		if (battleInfo.isFinished()) {
			return;
		}
		BattlePlayer character = battlePlayerInfo.getCharacter();

		if (character.isDefeated()) {
			return;
		}

		ReactionEmoji usedEmoji = event.getReaction().getEmoji();
		// Clear user reactions
		RequestBuffer.request(() -> {
			event.getMessage().removeReaction(event.getUser(), event.getReaction());
		});

		if (usedEmoji.getName().equals(Unicodes.crossmark)) {
			queueAction(battlePlayerInfo);
			return;
		}

		for (Skill skill : character.getCharClass().getAvailableSkills(character.getLevel(),
				character.getAttributes())) {
			long iconId = Long.parseLong(skill.getIconId());
			if (iconId == usedEmoji.getLongID()) {
				battlePlayerInfo.setSkillUsed(skill);
				break;
			}
		}

		if (battlePlayerInfo.getSkillUsed() == null) {
			return;
		}

		int totalcost = battlePlayerInfo.getSkillUsed().getCostOfExecution();
		totalcost += battlePlayerInfo.getSkillUsed()
				.getHighestAvailable(character.getLevel(), character.getAttributes()).getCostModifier();

		if (totalcost > character.getCurrentAtkResource()) {
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
				battlePlayerInfo.setTarget(character);
			} else if (battlePlayerInfo.getSkillUsed().getTargetType() == TargetType.ENEMY) {
				for (int hostileId : battleInfo.getHostiles().keySet()) {
					BattleNPC hostile = battleInfo.getHostiles().get(hostileId);
					if (hostile.isDefeated()) {
						continue;
					}
					battlePlayerInfo.setTarget(hostile);
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

	private BattleInformation fetchBattleInformation(BattlePlayer character) {
		return FantasyUnlimited.getInstance().getBattleMap().get(character.getCharacterId());
	}

	private void queueAction(BattlePlayerInformation playerInfo) {
		Skill usedSkill = playerInfo.getSkillUsed();

		BattleAction action = new BattleAction();
		action.setExecuting(playerInfo.getCharacter());
		action.setUsedSkill(usedSkill);
		action.setArea(false);

		BattleInformation battle = fetchBattleInformation(playerInfo.getCharacter());

		if (usedSkill == null) {
			action.setPass(true);
		} else {
			if (usedSkill.getTargetType() == TargetType.OWN) {
				action.setTarget(playerInfo.getCharacter());
			} else if (usedSkill.getTargetType() == TargetType.AREA) {
				action.setArea(true);
				for (Integer id : battle.getHostiles().keySet()) {
					action.getAreaTargets().put(Integer.toUnsignedLong(id), battle.getHostiles().get(id));
				}
			} else {
				action.setTarget(playerInfo.getTarget());
			}
		}

		// add the player info anew to avoid stale message data!
		battle.getPlayers().put(playerInfo.getCharacter().getCharacterId(), playerInfo);
		if (battle.getRounds().get(battle.getCurrentRound()) == null) {
			battle.getRounds().put(battle.getCurrentRound(), new ArrayList<>());
		}
		battle.getRounds().get(battle.getCurrentRound()).add(action);

		MessageInformation information = FantasyUnlimited.getInstance().getMessagesAwaitingReactions()
				.get(playerInfo.getMessage().getLongID());
		information.getStatus().setName(Name.BATTLE_WAITING);
		FantasyUnlimited.getInstance().getMessagesAwaitingReactions().put(playerInfo.getMessage().getLongID(),
				information);
		FantasyUnlimited.getInstance().editMessage(playerInfo.getMessage(),
				"`Please wait while the others pick their actions.`");

		if (battle.getRounds().get(battle.getCurrentRound()).size() == battle.getAlivePlayerCount()) {
			calculateAndPrintResults(battle);
		}
	}

	private class BattleActionComparator implements Comparator<BattleAction> {

		@Override
		public int compare(BattleAction o1, BattleAction o2) {
			Integer dex1, dex2;
			dex1 = o1.getExecuting().getAttributes().getDexterity();
			dex2 = o2.getExecuting().getAttributes().getDexterity();

			return dex1.compareTo(dex2);
		}
	}

	private void calculateAndPrintResults(BattleInformation battle) {
		queueHostileActions(battle);

		List<BattleAction> actions = battle.getRounds().get(battle.getCurrentRound());
		Collections.sort(actions, new BattleActionComparator());
		// actions sorted by dexterity

		embedBuilder = BattleUtils.createBattleOutputEmbeds(battle, true);
		FantasyUnlimited.getInstance().editMessage(battle.getMessage(), embedBuilder.build());

		battle.setCurrentRound(battle.getCurrentRound() + 1);
		BattleUtils.prepareNextroundActionbars(battle);

		if (battle.isFinished()) {
			for (long characterId : battle.getPlayers().keySet()) {
				FantasyUnlimited.getInstance().getBattles().remove(characterId);
				FantasyUnlimited.getInstance().getBattleMap().remove(characterId);
			}
			resultsHandler.handle(battle);

		} else {
			// put current data after execution so it sticks through a restart
			for (long characterId : battle.getPlayers().keySet()) {
				BattlePlayerInformation info = battle.getPlayers().get(characterId);
				FantasyUnlimited.getInstance().getBattles().put(characterId, info);
				FantasyUnlimited.getInstance().getBattleMap().put(characterId, battle);
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
			action.setExecuting(hostile);

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
				for (Long id : battle.getPlayers().keySet()) {
					action.getAreaTargets().put(id, battle.getPlayers().get(id).getCharacter());
				}
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
				// TODO aggro table!
				action.setTarget(availableTargets.get(randomGenerator.nextInt(availableTargets.size())));
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
				action.setTarget(availableFriends.get(randomGenerator.nextInt(availableFriends.size())));
				break;
			case OWN:
				action.setTarget(hostile);
			}
			battle.getRounds().get(battle.getCurrentRound()).add(action);
		}
	}

	private class BattleResultsHandler {
		private final Logger logger = Logger.getLogger(BattleResultsHandler.class);
		@Autowired
		private DiscordPlayerLogic playerLogic;

		public BattleResultsHandler() {
			FantasyUnlimited.autowire(this);
		}

		@SuppressWarnings("unchecked")
		public void handle(BattleInformation battle) {
			if (battle.isFinished() == false) {
				return;
			}

			if (battle.getAliveEnemyCount() > 0) {
				return;
			}

			Map<Dropable, Integer> loot = new HashMap<>();

			int xppool = 0;
			int averagelevel = 0;
			for (BattleNPC npc : battle.getHostiles().values()) {
				double level = npc.getLevel();
				xppool += (int) Math.ceil(Math.log10(level) * level + (10 + level)
						+ ThreadLocalRandom.current().nextDouble(level * 2 / 3));
				averagelevel += npc.getLevel();

				for (String itemId : npc.getBase().getLoottable().keySet()) {
					float chance = ThreadLocalRandom.current().nextFloat() * 100;
					if (chance < npc.getBase().getLoottable().get(itemId)) {
						Dropable item = FantasyUnlimited.getInstance().getDropableItem(itemId);
						if (loot.containsKey(item)) {
							loot.put(item, loot.get(item) + 1);
						} else {
							loot.put(item, 1);
						}
					}
				}
			}
			averagelevel = Math.floorDiv(averagelevel, battle.getHostiles().size());
			logger.trace("Average level: " + averagelevel);
			logger.trace("XP pool: " + xppool);

			StringBuilder builder = new StringBuilder();
			builder.append("```md\n");
			for (BattlePlayerInformation playerInfo : battle.getPlayers().values()) {
				
				//store health info for next battle
				playerLogic.saveNewHealth(playerInfo.getCharacter().getCharacterId(),
						playerInfo.getCharacter().getCurrentHealth());

				int yield = Math.floorDiv(xppool, battle.getPlayers().size());
				logger.trace("XP for player " + playerInfo.getCharacter().getName() + " before level bonus: " + yield);
				int level = playerInfo.getCharacter().getLevel();
				double multiplier = Math.sqrt(Math.abs(level - averagelevel));
				if (multiplier == 0) {
					multiplier = 1;
				}
				if (level > averagelevel) {
					multiplier = 1 / multiplier;
					if (level > averagelevel + 10) {
						multiplier = 0;
					}
				}
				yield = (int) Math.ceil(yield * multiplier);
				logger.trace("Character level: " + playerInfo.getCharacter().getLevel());
				logger.trace("XP for player " + playerInfo.getCharacter().getName() + " after level bonus: " + yield);

				builder.append("[" + playerInfo.getCharacter().getName() + "][Received " + yield + " experience]\n");
				if (playerLogic.addExperience(playerInfo.getCharacter().getCharacterId(), yield)) {
					builder.append("[" + playerInfo.getCharacter().getName()
							+ "][Leveled up! They receive 5 status points for distribution.]\n");
					CharacterClass charClass = playerInfo.getCharacter().getCharClass();
					builder.append("They also gain: \n");
					if (charClass.getAttributes().getEnduranceGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getEnduranceGrowth() + " Endurance\n");
					}
					if (charClass.getAttributes().getStrengthGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getStrengthGrowth() + " Strength\n");
					}
					if (charClass.getAttributes().getDexterityGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getDexterityGrowth() + " Dexterity\n");
					}
					if (charClass.getAttributes().getWisdomGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getWisdomGrowth() + " Wisdom\n");
					}
					if (charClass.getAttributes().getIntelligenceGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getIntelligenceGrowth() + " Intelligence\n");
					}
					if (charClass.getAttributes().getDefenseGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getDefenseGrowth() + " Defense\n");
					}
					if (charClass.getAttributes().getLuckGrowth() > 0) {
						builder.append("+ " + charClass.getAttributes().getLuckGrowth() + " Luck\n");
					}
				}
			}

			if (battle.getPlayers().size() == 1) {
				BattlePlayerInformation playerInfo = battle.getPlayers().values().iterator().next();
				builder.append(playerInfo.getCharacter().getName() + " found the following items:\n");
				List<Pair<String, Integer>> items = new ArrayList<>();
				for (Dropable item : loot.keySet()) {
					items.add(Pair.of(item.getId(), loot.get(item)));
					builder.append("[" + loot.get(item) + "x][" + item.getName() + "]\n");
				}
				Pair<String, Integer>[] itemArray = new Pair[items.size()];
				itemArray = items.toArray(itemArray);
				playerLogic.addItemsToInventory(playerInfo.getCharacter().getCharacterId(), itemArray);
			} else {
				// round robbin!
				// TODO
				// plan:
				// every common item that matches number of players: everyone gets one
				// every common item that doesn't match (= itemCount % playerCount != 0) =>
				// random chance
				// every item rarer than common => roll for it
			}

			builder.append("```");

			embedBuilder = BattleUtils.createBattleOutputEmbeds(battle, false);
			embedBuilder.appendField("Results", builder.toString(), false);
			FantasyUnlimited.getInstance().editMessage(battle.getMessage(), embedBuilder.build());
		}
	}
}
