package com.fantasyunlimited.utils.service;

import com.fantasyunlimited.battle.entity.*;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Qualifier("dtoUtils")
public class DTOUtils {
    @Autowired
    private DropableUtils dropableUtils;
    @Autowired
    private BattleUtils battleUtils;
    @Autowired
    private PlayerCharacterService characterService;

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
                characterService.getExperienceForNextLevel(character.getCurrentLevel()),
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
        if(character == null) return null;
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
                    0, //BattleParticipants don't have exp to next level either
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
                    0, //BattleParticipants don't have exp to next level either
                    battleResourceItem
            );
        }
        return playerCharacterItem;
    }

    public BattleParticipantStatus buildBattleParticipantStatus(BattleStatus status) {
        Skill.SkillType type = status.getModifierType() == BattleStatus.ModifierType.RAISE
                                ? Skill.SkillType.BUFF
                                : Skill.SkillType.DEBUFF;

        return new BattleParticipantStatus(
                type,
                status.getStatusName(),
                status.getStatusIcon(),
                status.getModifiedAttribute(),
                status.getModifiedSkill(),
                status.getAmountModifier(),
                status.getHealthchangePerRound(),
                status.getHealthchangeOnEnd(),
                status.isIncapacitated(),
                status.getRoundsRemaining()
        );
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
                action.getUsedConsumable(),
                action.getActionAmount()
        );
    }

    public BattleSkill buildBattleSkill(Skill usedSkill, PlayerCharacter playerCharacter) {
        if(usedSkill == null) return null;

        SkillRank skillRank = getSkillRank(usedSkill, playerCharacter);
        return buildBattleSkillInternal(usedSkill, skillRank);
    }

    public BattleSkill buildBattleSkill(Skill usedSkill, BattleParticipant participant) {
        if(usedSkill == null) return null;

        SkillRank skillRank = getSkillRank(usedSkill, participant);
        return buildBattleSkillInternal(usedSkill, skillRank);
    }

    private BattleSkill buildBattleSkillInternal(Skill usedSkill, SkillRank skillRank) {
        if(usedSkill == null) return null;

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
        if(usedSkill == null) return null;

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
        if(usedSkill == null) return null;

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
        LocationItem location = buildLocationItem(battleInformation.getLocation());

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

            List<ToolbarConsumableItem> consumables = new ArrayList<>();
            selectedCharacter.getInventory().entrySet().stream()
                    .map(entry -> {
                        String itemId = entry.getKey();
                        int itemCount = entry.getValue();
                        Dropable item = dropableUtils.getDropableItem(itemId);
                        if (item instanceof Consumable consumable && consumable.isDuringBattle()) {
                            return new ToolbarConsumableItem(consumable,  itemCount);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .forEach(consumables::add);

            playerDetails =new BattlePlayerDetails(
                    selectedCharacter.getId(),
                    battleUtils.isParticipating(selectedCharacter, battleInformation),
                    toolbarSkills,
                    consumables
            );
        }
        else {
            playerDetails = null;
        }

        List<BattleParticipantDetails> players = new ArrayList<>();
        battleInformation.getPlayers().stream()
                .map(this::buildBattleParticipantDetails)
                .forEach(players::add);

        List<BattleParticipantDetails> hostiles = new ArrayList<>();
        battleInformation.getHostiles().stream()
                .map(this::buildBattleParticipantDetails)
                .forEach(hostiles::add);

        Map<Integer, List<BattleLogItem>> battleLog = new HashMap<>();
        battleInformation.getActions().stream()
                .sorted((log1, log2) -> Integer.compare(log2.getRound(), log1.getRound()))
                .map(this::buildBattleLogItem)
                .forEach(log -> {
                    if(battleLog.containsKey(log.round()) == false) {
                        battleLog.put(log.round(), new ArrayList<>());
                    }
                    battleLog.get(log.round()).add(log);
                });


        return new BattleDetailInfo(
                battleInformation.getBattleId().toString(),
                battleUtils.isBattleActive(battleInformation),
                location,
                playerDetails,
                players,
                hostiles,
                new BattleLog(battleLog),
                buildSummary(battleInformation)
        );
    }

    public BattleResultSummary buildSummary(BattleInformation battleInfo) {
        if(battleInfo.isActive() || battleInfo.getResult() == null) {
            return null;
        }

        List<PlayerLootSummary> lootSummary = new ArrayList<>();

        battleInfo.getResult().getLootList().stream()
                .map(lootList -> {
                    List<InventoryItem> items = new ArrayList<>();
                    lootList.getItems().keySet().stream()
                            .map(id -> {
                                int count = lootList.getItems().get(id);
                                return buildInventoryItem(id, count);
                            }).forEach(items::add);

                    return new PlayerLootSummary(
                            buildPlayerCharacterItem(lootList.getPlayer()),
                            lootList.getExperienceAwarded(),
                            lootList.isLevelUp(),
                            lootList.getGoldAwarded(),
                            items
                    );
                })
                .forEach(lootSummary::add);

        return new BattleResultSummary(
                battleInfo.getResult().getWinningSide(),
                lootSummary
        );
    }

    public InventoryItem buildInventoryItem(String itemId, int count) {
        Dropable item = dropableUtils.getDropableItem(itemId);
        if(item instanceof Weapon) {
            return new InventoryItem(item, "weapon", count);
        }
        else if (item instanceof Equipment) {
            return new InventoryItem(item, "equipment", count);
        }
        else {
            return new InventoryItem(item, "consumable", count);
        }
    }

    public BattleParticipantDetails buildBattleParticipantDetails(BattleParticipant participant) {
        PlayerCharacterItem characterItem = buildPlayerCharacterItem(participant);
        List<BattleParticipantStatus> statusEffects = new ArrayList<>();

        participant.getStatusModifiers().stream()
                .map(this::buildBattleParticipantStatus)
                .forEach(statusEffects::add);

        return new BattleParticipantDetails(
                participant.getId().toString(),
                characterItem,
                statusEffects
        );
    }
}
