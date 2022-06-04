package com.fantasyunlimited.battle.service;

import com.fantasyunlimited.battle.entity.*;
import com.fantasyunlimited.battle.utils.BattleActionHandler;
import com.fantasyunlimited.battle.utils.BattleDTOUtils;
import com.fantasyunlimited.battle.utils.BattleUtils;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.entity.Location;
import com.fantasyunlimited.items.entity.Skill;
import com.fantasyunlimited.items.entity.SkillRank;
import com.fantasyunlimited.rest.dto.BattleDetailInfo;
import com.fantasyunlimited.rest.dto.BattleUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BattleService {
    private static final Logger log = LoggerFactory.getLogger(BattleService.class);

    @Autowired
    private BattleCrudService crudService;
    @Autowired
    private BattleUtils battleUtils;
    @Autowired
    private BattleDTOUtils utils;
    @Autowired
    private BattleActionHandler actionHandler;
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private LocationBag locationBag;


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

    public BattleInformation addBattleAction(BattleInformation battleInfo, BattleParticipant executing, BattleParticipant target, Skill usedSkill){
        BattleAction action = new BattleAction();
        action.setBattle(battleInfo);
        action.setBattleId(battleInfo.getBattleId().toString());
        action.setSequence(battleInfo.getNextActionSequence());
        action.setExecuted(false);
        action.setRound(battleInfo.getCurrentRound());

        action.setExecuting(executing);
        action.setTarget(target);
        action.setUsedSkill(usedSkill);

        battleInfo.getActions().add(action);
        return crudService.saveBattle(battleInfo);
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
                        battleInfo.getPlayers().forEach(player -> {
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

        return Pair.of(true,crudService.saveBattle(battleInfo));
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
                && (usedSkill.getType() == Skill.SkillType.OFFENSIVE || usedSkill.getType() == Skill.SkillType.DEBUFF)) {
            usedSkill.setTargetType(Skill.TargetType.ENEMY); // fallback
        } else if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.DEFENSIVE || usedSkill.getType() == Skill.SkillType.BUFF)) {
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
