package com.fantasyunlimited.rest;

import com.fantasyunlimited.battle.entity.*;
import com.fantasyunlimited.battle.service.BattleService;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUpgradeHandler;
import java.util.*;

@Component
public class ControllerUtils {

    @Autowired
    private BattleService battleService;
    @Autowired
    private DropableUtils dropableUtils;

    public UUID getBattleIdFromSession(HttpServletRequest request) {
        return (UUID) request.getSession().getAttribute("battleId");
    }

    public void setBattleIdFromSession(HttpServletRequest request, UUID battleId) {
        request.getSession().setAttribute("battleId", battleId);
    }

    public PlayerCharacter getPlayerCharacterFromSession(HttpServletRequest request) {
        return (PlayerCharacter) request.getSession().getAttribute("selectedCharacter");
    }

    public void setPlayerCharacterToSession(HttpServletRequest request, PlayerCharacter selectedCharacter) {
        request.getSession().setAttribute("selectedCharacter", selectedCharacter);
    }

    public PlayerCharacterItem buildPlayerCharacterItem(PlayerCharacter character) {
        ClassItem classItem = buildClassItem(character.getClassId());
        RaceItem raceItem = buildRaceItem(character.getRaceId());
        LocationItem locationItem = buildLocationItem(character.getLocationId());
        BattleResourceItem battleResourceItem = buildBattleResourceItem(character);

        return new PlayerCharacterItem(
                character.getId(),
                character.getName(),
                classItem,
                raceItem,
                locationItem,
                character.getCurrentLevel(),
                character.getCurrentXp(),
                battleResourceItem
        );
    }

    public ClassItem buildClassItem(CharacterClass characterClass) {
        return new ClassItem(characterClass.getId(), characterClass.getName(), characterClass.getIconName());
    }
    public RaceItem buildRaceItem(Race race) {
        return new RaceItem(race.getId(), race.getName(), race.getIconName());
    }
    public LocationItem buildLocationItem(Location location) {
        return new LocationItem(location.getId(), location.getName(), location.getIconName(), location.getBannerImage());
    }
    public BattleResourceItem buildBattleResourceItem(PlayerCharacter character) {
        return new BattleResourceItem(
                character.getCurrentHealth(),
                character.getMaxHealth(),
                character.getCurrentAtkResource(),
                character.getMaxAtkResource(),
                character.getClassId().getEnergyType()
        );
    }

    public BattleResourceItem buildBattleResourceItem(BattleParticipant character) {
        return new BattleResourceItem(
                character.getCurrentHealth(),
                character.getMaxHealth(),
                character.getCurrentAtkResource(),
                character.getMaxAtkResource(),
                character.getCharClassId().getEnergyType()
        );
    }

    public PlayerCharacterItem buildPlayerCharacterItem(BattleParticipant character) {
        ClassItem classItem = buildClassItem(character.getCharClassId());
        RaceItem raceItem = buildRaceItem(character.getRaceId());
        LocationItem locationItem = buildLocationItem(character.getBattleInformation().getLocation());
        BattleResourceItem battleResourceItem = buildBattleResourceItem(character);

        PlayerCharacterItem playerCharacterItem;
        if(character instanceof BattlePlayer player) {
            playerCharacterItem = new PlayerCharacterItem(
                    player.getCharacterId(),
                    character.getName(),
                    classItem,
                    raceItem,
                    locationItem,
                    character.getLevel(),
                    0, // BattleParticipants don't have exp
                    battleResourceItem
            );
        }
        else {
            playerCharacterItem = new PlayerCharacterItem(
                    0L,
                    character.getName(),
                    classItem,
                    raceItem,
                    locationItem,
                    character.getLevel(),
                    0, // BattleParticipants don't have exp
                    battleResourceItem
            );
        }
        return playerCharacterItem;
    }

    public BattleParticipantStatus buildBattleParticipantStatus(BattleStatus status) {
        // TODO
        return new BattleParticipantStatus();
    }

    public BattleLogItem buildBattleLogItem(BattleAction action) {
        BattleActionStatus status;
        BattleActionOutcome outcome;

        if(action.isExecuted() == false) {
            status = BattleActionStatus.WAITING;
            outcome = BattleActionOutcome.NONE;
        }
        else if(action.isFlee()) {
            status = BattleActionStatus.FLEE;
            outcome = BattleActionOutcome.NONE;
        }
        else if(action.isPass()) {
            status = BattleActionStatus.PASS;
            outcome = BattleActionOutcome.NONE;
        }
        else if(action.isIncapacitated()) {
            status = BattleActionStatus.INCAPACITATED;
            outcome = BattleActionOutcome.NONE;
        }
        else if(action.isBlocked()) {
            status = BattleActionStatus.EXECUTED;
            outcome = BattleActionOutcome.BLOCKED;
        }
        else if(action.isDodged()) {
            status = BattleActionStatus.EXECUTED;
            outcome = BattleActionOutcome.DODGED;
        }
        else if(action.isCritical()) {
            status = BattleActionStatus.EXECUTED;
            outcome = BattleActionOutcome.CRITICAL;
        }
        else if(action.isParried()) {
            status = BattleActionStatus.EXECUTED;
            outcome = BattleActionOutcome.PARRIED;
        }
        else {
            status = BattleActionStatus.EXECUTED;
            outcome = BattleActionOutcome.HIT;
        }

        return new BattleLogItem(
                action.getSequence(),
                action.getOrdinal(),
                action.getRound(),
                action.getActionDate().toInstant().toEpochMilli(),
                action.isExecuted(),
                status,
                outcome,
                buildPlayerCharacterItem(action.getExecuting()),
                buildPlayerCharacterItem(action.getTarget()),
                buildBattleSkill(action.getUsedSkill(), action.getExecuting()),
                action.getActionAmount()
        );
    }

    public BattleSkill buildBattleSkill(Skill usedSkill, PlayerCharacter playerCharacter) {
        SkillRank skillRank = getSkillRank(usedSkill, playerCharacter);
        return buildBattleSkillInternal(usedSkill, skillRank);
    }

    public BattleSkill buildBattleSkill(Skill usedSkill, BattleParticipant participant) {
        SkillRank skillRank = getSkillRank(usedSkill, participant);
        return buildBattleSkillInternal(usedSkill, skillRank);
    }

    private BattleSkill buildBattleSkillInternal(Skill usedSkill, SkillRank skillRank) {
        int minDamage = usedSkill.getMinDamage() + skillRank.getDamageModifier();
        int maxDamage = usedSkill.getMaxDamage() + skillRank.getDamageModifier();
        int cost = usedSkill.getCostOfExecution() + skillRank.getCostModifier();

        return new BattleSkill(
                usedSkill.getId(),
                usedSkill.getName(),
                usedSkill.getDescription(),
                usedSkill.getIconId(),
                usedSkill.getAttribute(),
                usedSkill.getType(),
                usedSkill.getTargetType(),
                usedSkill.getWeaponModifier(),
                usedSkill.getPreparationRounds(),
                usedSkill.getDurationInTurns(),
                usedSkill.isSkillIncapacitates(),
                minDamage,
                maxDamage,
                cost,
                skillRank.getRank()
        );
    }

    private SkillRank getSkillRank(Skill usedSkill, PlayerCharacter executing) {
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
            if (executing.getCurrentLevel() >= rank.getRequiredPlayerLevel()
                    && attributeInQuestion >= rank.getRequiredAttributeValue()
                    && (highest == null || highest.getRank() < rank.getRank())) {
                highest = rank;
            }
        }
        return highest;
    }

    private SkillRank getSkillRank(Skill usedSkill, BattleParticipant executing) {
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

    public BattleDetailInfo buildBattleDetailInfo(BattleInformation battleInformation, PlayerCharacter selectedCharacter) {
        LocationItem location = new LocationItem(
                battleInformation.getLocation().getId(),
                battleInformation.getLocation().getName(),
                battleInformation.getLocation().getIconName(),
                battleInformation.getLocation().getBannerImage()
        );

        BattlePlayerDetails playerDetails;
        if(selectedCharacter != null) {

            List<BattleSkill> toolbarSkills = new ArrayList<>();
            selectedCharacter.getClassId().getSkillInstances().stream()
                    .map(skill -> buildBattleSkill(skill, selectedCharacter))
                    .forEach(toolbarSkills::add);

            int missingSkills = toolbarSkills.size() % 10;
            if (missingSkills > 0) {
                for (int i = 0; i < missingSkills; i++) {
                    toolbarSkills.add(new BattleSkill(
                            "empty",
                            "empty",
                            "empty",
                            "/images/emptySlotIcon.png",
                            null,
                            null,
                            null,
                            null,
                            0,
                            0,
                            false,
                            0,
                            0,
                            0,
                            0
                    ));
                }
            }

            List<InventoryItem> consumables = new ArrayList<>();
            selectedCharacter.getInventory().entrySet().stream()
                    .map(entry -> {
                        String itemId = entry.getKey();
                        int itemCount = entry.getValue();
                        Dropable item = dropableUtils.getDropableItem(itemId);
                        if (item instanceof Consumable consumable && consumable.isDuringBattle()) {
                            return new InventoryItem(item, "consumable", itemCount);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .forEach(consumables::add);

            playerDetails =new BattlePlayerDetails(
                    selectedCharacter.getId(),
                    battleService.isParticipating(selectedCharacter, battleInformation),
                    toolbarSkills,
                    consumables
            );
        }
        else {
            playerDetails = null;
        }

        List<BattleParticipantDetails> players = new ArrayList<>();
        battleInformation.getPlayers().stream()
                .map(player -> buildBattleParticipantDetails(player))
                .forEach(players::add);

        List<BattleParticipantDetails> hostiles = new ArrayList<>();
        battleInformation.getHostiles().stream()
                .map(hostile -> buildBattleParticipantDetails(hostile))
                .forEach(hostiles::add);

        Map<Integer, List<BattleLogItem>> battleLog = new HashMap<>();
        battleInformation.getActions().stream()
                .sorted((log1, log2) -> Integer.compare(log2.getRound(), log1.getRound()))
                .map(action -> buildBattleLogItem(action))
                .forEach(log -> {
                    if(battleLog.containsKey(log.round()) == false) {
                        battleLog.put(log.round(), new ArrayList<>());
                    }
                    battleLog.get(log.round()).add(log);
                });


        return new BattleDetailInfo(
                battleInformation.getBattleId().toString(),
                battleService.isBattleActive(battleInformation),
                location,
                playerDetails,
                players,
                hostiles,
                new BattleLog(battleLog)
        );
    }

    public BattleParticipantDetails buildBattleParticipantDetails(BattleParticipant participant) {
        PlayerCharacterItem characterItem = buildPlayerCharacterItem(participant);
        List<BattleParticipantStatus> statusEffects = new ArrayList<>();

        participant.getStatusModifiers().stream()
                .map(status -> buildBattleParticipantStatus(status))
                .forEach(statusEffects::add);

        return new BattleParticipantDetails(
                participant.getId().toString(),
                characterItem,
                statusEffects
        );
    }
}
