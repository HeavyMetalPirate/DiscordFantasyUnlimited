package com.fantasyunlimited.rest;

import com.fantasyunlimited.battle.entity.BattleAction;
import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import com.fantasyunlimited.battle.entity.BattleStatus;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.rest.dto.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUpgradeHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ControllerUtils {

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

        if(action.isFlee()) {
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
                action.getRound(),
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
