package com.fantasyunlimited.battle.service;

import com.fantasyunlimited.battle.entity.*;
import com.fantasyunlimited.battle.utils.BattleActionHandler;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.BattleSide;
import com.fantasyunlimited.rest.dto.BattleUpdate;
import com.fantasyunlimited.utils.service.BattleUtils;
import com.fantasyunlimited.utils.service.DTOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BattleService {
    private static final Logger log = LoggerFactory.getLogger(BattleService.class);

    @Autowired
    private BattleCrudService crudService;
    @Autowired
    private BattleUtils battleUtils;
    @Autowired
    @Qualifier("dtoUtils")
    private DTOUtils utils;
    @Autowired
    private BattleActionHandler actionHandler;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private LocationBag locationBag;
    @Autowired
    private DropableUtils dropableUtils;
    @Autowired
    private PlayerCharacterService characterService;


    private Random randomGenerator = new Random();

    public boolean isParticipating(PlayerCharacter character, BattleInformation battleInfo) {
        return battleUtils.isParticipating(character, battleInfo);
    }

    public boolean isBattleActive(BattleInformation battleInformation) {
        return battleUtils.isBattleActive(battleInformation);
    }

    public BattleInformation findActiveBattleForPlayer(PlayerCharacter character) {
        BattleInformation battle = crudService.getCurrentCharacterBattle(character);

        if(battle != null && battle.getLocation().getId()
                                    .equals(character.getLocationId().getId()) == false) {
            // location check - if they don't match, let the character flee from battle
            // non-matching should in theory only be possible if someone modifies the database directly
            // TODO
        }
        return battle;
    }

    public BattleInformation addBattleAction(BattleInformation battleInfo, BattleParticipant executing, Consumable usedConsumable) {
        BattleAction action = buildBaseAction(battleInfo);

        action.setExecuting(executing);
        action.setUsedConsumable(usedConsumable);

        return crudService.saveBattle(battleInfo);
    }

    public BattleInformation addBattleAction(BattleInformation battleInfo, BattleParticipant executing, BattleParticipant target, Skill usedSkill) {
        if(target.isDefeated()) return battleInfo;

        BattleAction action = buildBaseAction(battleInfo);

        action.setExecuting(executing);
        action.setTarget(target);
        action.setUsedSkill(usedSkill);

        return crudService.saveBattle(battleInfo);
    }

    private BattleAction buildBaseAction(BattleInformation battleInfo) {
        BattleAction action = new BattleAction();
        action.setBattle(battleInfo);
        action.setBattleId(battleInfo.getBattleId().toString());
        action.setSequence(battleInfo.getNextActionSequence());
        action.setExecuted(false);
        action.setRound(battleInfo.getCurrentRound());
        battleInfo.getActions().add(action);
        return action;
    }

    public BattleInformation initializeBattle(List<PlayerCharacter> players, String locationId) {
        // find out who is eligible - only people in the same location that are not in a battle
        List<PlayerCharacter> inLocation = new ArrayList<>();
        players.stream()
                .filter(character -> character.getLocationId().getId().equals(locationId))
                .forEach(inLocation::add);

        List<PlayerCharacter> notInBattle = new ArrayList<>();
        inLocation.stream()
                .filter(character -> crudService.characterInActiveBattle(character) == false)
                .forEach(notInBattle::add);

        log.debug("Found characters for battle: {}", notInBattle);
        if(notInBattle.size() == 0) {
            throw new IllegalStateException("All characters are either not in the requested location or in an active battle.");
        }
        final Location location = locationBag.getItem(locationId);
        if(location == null) {
            throw new IllegalArgumentException("Location with id " + locationId + " not found.");
        }
        List<BattleNPC> battleNPCs = new ArrayList<>();
        battleUtils.findOpponents(location).stream()
                .map(hostile -> battleUtils.initializeHostileNPC(hostile))
                .forEach(battleNPCs::add);

        if(battleNPCs.size() == 0) {
            throw new IllegalStateException("No NPCs could've been found for location " + location);
        }

        BattleInformation battleInformation = new BattleInformation();
        battleInformation.setActive(true);
        battleInformation.setLocation(location);

        List<BattlePlayer> battlePlayers = new ArrayList<>();
        inLocation.stream()
                .map(character -> battleUtils.initializeBattlePlayer(character))
                .peek(character -> character.setBattleInformation(battleInformation))
                .forEach(battlePlayers::add);

        battleInformation.setPlayers(battlePlayers);
        battleInformation.setHostiles(battleNPCs);

        battleNPCs.forEach(npc -> npc.setBattleInformation(battleInformation));

        return crudService.saveBattle(battleInformation);
    }

    public BattleUpdate checkBattleStatus(BattleInformation battleInfo) {
        // perform round calculations, and broadcast the result
        Pair<Boolean, BattleInformation> updatedInformation = handleRoundCalculations(battleInfo);

        // Send a broadcast update
        BattleUpdate broadcastUpdate = new BattleUpdate(
                updatedInformation.getFirst(),
                utils.buildBattleDetailInfo(updatedInformation.getSecond(), null)
        );

        if(broadcastUpdate.hasUpdate()) {
            template.convertAndSend("/topic/battle/" + battleInfo.getBattleId().toString(), broadcastUpdate);
        }

        return broadcastUpdate;
    }

    public Pair<Boolean, BattleInformation> handleRoundCalculations(BattleInformation battleInfo) {
        if(battleUtils.isBattleActive(battleInfo) == false) return Pair.of(false, battleInfo);

        // figure out the number of total players still active
        AtomicInteger numberParticipants = new AtomicInteger(0);
        battleInfo.getPlayers().stream()
                .filter(player -> player.isDefeated() == false)
                .map(player -> 1)
                .forEach(numberParticipants::addAndGet);

        // figure out number of non-executed actions
        List<BattleAction> actions = new ArrayList<>();
        battleInfo.getActions().stream()
                .filter(action -> action.isExecuted() == false)
                .forEach(actions::add);

        if(actions.size() < numberParticipants.get()) {
            // not every player has put in their action yet
            return Pair.of(false, battleInfo);
        }

        battleInfo.getHostiles().stream()
                .filter(hostile -> hostile.isDefeated() == false)
                .forEach(hostile -> {
                    Skill usedSkill = calculateSkillUsed(hostile);

                    if(usedSkill.getTargetType() == Skill.TargetType.AREA) {
                        // add actions for each player target
                        battleInfo.getPlayers().stream()
                                .filter(player -> player.isDefeated() == false)
                                .forEach(player -> {
                            BattleAction action = calculateEnemyAction(battleInfo, usedSkill, hostile, player);

                            battleInfo.getActions().add(action);
                        });
                    }
                    else {
                        BattleParticipant target = calculateHostileActionTarget(usedSkill,hostile,battleInfo);
                        BattleAction action = calculateEnemyAction(battleInfo, usedSkill,hostile, target);

                        battleInfo.getActions().add(action);
                    }
                });

        AtomicInteger ordinal = new AtomicInteger(0);
        battleInfo.getActions().stream()
                .filter(action -> action.isExecuted() == false)
                .sorted(new BattleActionComparator())
                .map(action -> {
                    action.setOrdinal(ordinal.incrementAndGet());
                    return action;
                })
                .forEach(action -> actionHandler.executeAction(action));


        battleInfo.setCurrentRound(battleInfo.getCurrentRound() + 1);
        battleInfo.setActive(battleUtils.isBattleActive(battleInfo));

        // TODO handle status effects and timeouts
        battleInfo.getPlayers().stream()
                .forEach(player -> {

                    // Apply DoTs and HoTs

                    // Expiry
                    player.getStatusModifiers().stream()
                            .filter(status -> status.isPermanent() == false)
                            .forEach(status -> status.setRoundsRemaining(status.getRoundsRemaining() - 1));
                    player.getStatusModifiers().removeIf(status -> status.isPermanent() == false && status.getRoundsRemaining() == 0);
                });

        battleInfo.getHostiles().stream()
                .forEach(hostile -> {

                    // Apply DoTs and HoTs

                    // Expiry
                    hostile.getStatusModifiers().stream()
                            .filter(status -> status.isPermanent() == false)
                            .forEach(status -> status.setRoundsRemaining(status.getRoundsRemaining() - 1));
                    hostile.getStatusModifiers().removeIf(status -> status.isPermanent() == false && status.getRoundsRemaining() == 0);
                });


        BattleInformation toStore = handleBattleFinalized(battleInfo);
        return Pair.of(true,crudService.saveBattle(toStore));
    }

    private BattleInformation handleBattleFinalized(BattleInformation battleInfo) {
        if(battleInfo.isActive()) return battleInfo; // Not yet finalized
        if(battleInfo.getResult() != null) return battleInfo; // Already calculated

        BattleResult result = new BattleResult();
        result.setBattleInformation(battleInfo);
        result.setLootList(new ArrayList<>());

        if(battleInfo.getPlayers().stream().allMatch(player -> player.isDefeated())) {
            result.setWinningSide(BattleSide.RIGHT);
        }
        else {
            result.setWinningSide(BattleSide.LEFT);
        }

        Map<Dropable, Integer> loot = new HashMap<>();

        AtomicInteger xppool = new AtomicInteger(0);
        AtomicInteger goldpool = new AtomicInteger(0);
        AtomicInteger averagelevel = new AtomicInteger(0);

        battleInfo.getHostiles().stream()
                .forEach(npc -> {
                    double level = npc.getLevel();
                    xppool.getAndAdd((int) Math.ceil(Math.log10(level) * level + (10 + level)
                            + ThreadLocalRandom.current().nextDouble(level * 2 / 3)));
                    averagelevel.getAndAdd(npc.getLevel());

                    if(npc.getBase().getMaximumGold() != 0) {
                        int gold = ThreadLocalRandom.current().nextInt(npc.getBase().getMinimumGold(), npc.getBase().getMaximumGold());
                        goldpool.getAndAdd(gold);
                    }

                    Map<String, Double> lootTable = npc.getBase().getLoottable();
                    lootTable.keySet().stream()
                            .filter(id -> {
                                float chance = ThreadLocalRandom.current().nextFloat() * 100;
                                return lootTable.get(id) < chance;
                            })
                            .map(id -> dropableUtils.getDropableItem(id))
                            .filter(Objects::nonNull)
                            .forEach(item -> {
                                if (loot.containsKey(item)) {
                                    loot.put(item, loot.get(item) + 1);
                                } else {
                                    loot.put(item, 1);
                                }
                            });
                });
        averagelevel.set(Math.floorDiv(averagelevel.get(), battleInfo.getHostiles().size()));
        log.trace("Average level: " + averagelevel);
        log.trace("XP pool: " + xppool);

        Map<Long, List<Dropable>> lootDistribution = new HashMap<>();
        if (battleInfo.getPlayers().size() == 1) {
            long playerId = battleInfo.getPlayers().stream()
                    .findFirst()
                    .get()
                    .getCharacterId();
            lootDistribution.put(playerId, new ArrayList<>());
            // player gets everything
            loot.keySet().stream()
                    .forEach(dropable -> lootDistribution.get(playerId).add(dropable));

        } else {
            battleInfo.getPlayers().forEach(player -> lootDistribution.put(player.getCharacterId(), new ArrayList<>()));
            // roll for every item, highest bidder wins
            loot.keySet().stream()
                    .forEach(dropable -> {
                        int highestRoll = 0;
                        BattlePlayer highestRoller = null;
                        log.trace("Rolling for Item {}.", dropable);
                        for(BattlePlayer player: battleInfo.getPlayers()) {
                            int roll = ThreadLocalRandom.current().nextInt(1,100);
                            log.trace("{} rolled: {}", player, roll);
                            if (roll > highestRoll) {
                                highestRoll = roll;
                                highestRoller = player;
                            }
                        }
                        log.trace("Awarding {} with roll {} the item {}.", highestRoller, highestRoll, dropable);
                        lootDistribution.get(highestRoller.getCharacterId()).add(dropable);
                    });
        }

        battleInfo.getPlayers().stream()
                .forEach(player -> {
                    BattleLoot battleLoot = new BattleLoot();
                    battleLoot.setPlayer(player.getPlayerCharacter());
                    battleLoot.setItems(new HashMap<>());

                    int yield = Math.floorDiv(xppool.get(), battleInfo.getPlayers().size());
                    log.trace("XP for player " + player.getName() + " before level bonus: " + yield);
                    int level = player.getLevel();
                    double multiplier = Math.sqrt(Math.abs(level - averagelevel.get()));
                    if (multiplier == 0) {
                        multiplier = 1;
                    }
                    if (level > averagelevel.get()) {
                        multiplier = 1 / multiplier;
                        if (level > averagelevel.get() + 10) {
                            multiplier = 0;
                        }
                    }
                    yield = (int) Math.ceil(yield * multiplier);
                    log.trace("Character level: " + player.getLevel());
                    log.trace("XP for player " + player.getName() + " after level bonus: " + yield);
                    boolean levelUp = characterService.addExperience(player.getCharacterId(), yield);
                    battleLoot.setLevelUp(levelUp);
                    battleLoot.setExperienceAwarded(yield);

                    yield = Math.floorDiv(goldpool.get(), battleInfo.getPlayers().size());
                    characterService.addGold(player.getCharacterId(), yield);
                    battleLoot.setGoldAwarded(yield);

                    List<Dropable> items = lootDistribution.get(player.getCharacterId());
                    if(items != null) {
                        items.forEach(item -> {
                            characterService.addItemToInventory(player.getCharacterId(), item, 1);
                            if(battleLoot.getItems().containsKey(item.getId())) {
                                battleLoot.getItems().put(
                                        item.getId(),
                                        battleLoot.getItems().get(item.getId()) + 1
                                );
                            }
                            else {
                                battleLoot.getItems().put(item.getId(), 1);
                            }
                        });
                    }

                    battleLoot.setResult(result);
                    result.getLootList().add(battleLoot);
                });

        battleInfo.setResult(result);
        return battleInfo;
    }

    private Skill calculateSkillUsed(BattleNPC hostile) {
        // select skill
        List<Skill> executableSkills = new ArrayList<>();
        hostile.getCharClassId().getSkillInstances().stream()
                .filter(skill -> {
                    SkillRank highest = getHighestSkillRank(skill, hostile);
                    if(highest == null) return false;

                    int skillCost = skill.getCostOfExecution();
                    skillCost += highest.getCostModifier();
                    if(hostile.getCurrentAtkResource() < skillCost) return false;
                    return true;
                })
                .forEach(executableSkills::add);
        if (executableSkills.size() == 0) {
            return null;
        }
        return executableSkills.get(randomGenerator.nextInt(executableSkills.size()));
    }

    private BattleAction calculateEnemyAction(BattleInformation battleInformation, Skill usedSkill, BattleParticipant executing, BattleParticipant target) {
        BattleAction action = new BattleAction();
        action.setBattleId(battleInformation.getBattleId().toString());
        action.setBattle(battleInformation);
        action.setSequence(battleInformation.getNextActionSequence());
        action.setRound(battleInformation.getCurrentRound());

        action.setExecuted(false);
        action.setExecuting(executing);

        if(usedSkill == null) {
            action.setPass(true);
            return action;
        }

        action.setUsedSkill(usedSkill);
        action.setTarget(target);
        return action;
    }

    private BattleParticipant calculateHostileActionTarget(Skill usedSkill, BattleParticipant executing, BattleInformation battleInformation) {
        if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.OFFENSIVE)) {
            usedSkill.setTargetType(Skill.TargetType.ENEMY); // fallback
        } else if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.DEFENSIVE)) {
            usedSkill.setTargetType(Skill.TargetType.FRIEND); // fallback
        }

        switch (usedSkill.getTargetType()) {
            case AREA:
                return null;
            case ENEMY:
                List<BattlePlayer> availableTargets = new ArrayList<>();
                // TODO stream players and filter defeated
                battleInformation.getPlayers().stream()
                        .filter(player -> player.isDefeated() == false)
                        .forEach(availableTargets::add);
                // TODO aggro table!
                return availableTargets.get(randomGenerator.nextInt(availableTargets.size()));
            case FRIEND:
                List<BattleNPC> availableFriends = new ArrayList<>();
                // TODO stream hostiles and filter defeated
                battleInformation.getHostiles().stream()
                        .filter(npc -> npc.isDefeated() == false)
                        .forEach(availableFriends::add);
                return availableFriends.get(randomGenerator.nextInt(availableFriends.size()));
            default:
                return executing;
        }
    }

    public SkillRank getHighestSkillRank(Skill usedSkill, BattleParticipant executing) {
        SkillRank highest = null;
        int attributeInQuestion = switch (usedSkill.getAttribute()) {
            case ALL -> throw new IllegalStateException("Skill can't be dependent on ALL attributes.");
            case DEFENSE -> executing.getAttributes().getDefense();
            case DEXTERITY -> executing.getAttributes().getDexterity();
            case ENDURANCE -> executing.getAttributes().getEndurance();
            case INTELLIGENCE -> executing.getAttributes().getIntelligence();
            case LUCK -> executing.getAttributes().getLuck();
            case STRENGTH -> executing.getAttributes().getStrength();
            case WISDOM -> executing.getAttributes().getWisdom();
        };

        for (SkillRank rank : usedSkill.getRanks()) {
            if (executing.getLevel() >= rank.getRequiredPlayerLevel()
                    && attributeInQuestion >= rank.getRequiredAttributeValue()
                    && (highest == null || highest.getRank() < rank.getRank())) {
                highest = rank;
            }
        }
        return highest;
    }

    public BattleInformation getBattleInformation(UUID battleId) {
        return crudService.findBattle(battleId);
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

}
