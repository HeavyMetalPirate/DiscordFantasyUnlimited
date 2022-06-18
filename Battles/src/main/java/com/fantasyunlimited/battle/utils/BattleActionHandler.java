package com.fantasyunlimited.battle.utils;

import com.fantasyunlimited.battle.entity.BattleAction;
import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import com.fantasyunlimited.battle.entity.BattleStatus;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import com.fantasyunlimited.items.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class BattleActionHandler {

    @Autowired
    private BattleStatsUtils statsUtils;
    @Autowired
    private PlayerCharacterService playerCharacterService;

    /*
     *public static final Integer DODGED = -2 ^ 22;
     *public static final Integer BLOCKED = -2 ^ 23;
     *public static final Integer PARRIED = -2 ^ 24;
     */
    public BattleAction executeAction(BattleAction action) {
        if (action.isExecuted()) {
            return action;
        }
        action.setExecuted(true);

        // if you die in that round, don't do actions because u ded
        if (action.getExecuting().isDefeated()) {
            action.setDefeated(true);
            return action;
        }

        if(action.isFlee()) {
            BattleStatus fleeStatus = new BattleStatus();
            fleeStatus.setIncapacitated(true);
            fleeStatus.setStatusName("battle.status.flee");
            fleeStatus.setRoundsRemaining(-1);
            action.getExecuting().getStatusModifiers().add(fleeStatus);
            return action;
        }

        if (action.isPass()) {
            performAtkUsageAndRegen(0, action);
            return action;
        }

        if (action.getExecuting().getStatusModifiers().parallelStream()
                .anyMatch(BattleStatus::isIncapacitated)) {
            action.setIncapacitated(true);
            return action;
        }

        if(action.getUsedSkill() != null) {
            return handleSkillAction(action);
        }
        return handleConsumableAction(action);
    }

    private BattleAction handleConsumableAction(BattleAction action) {
        Consumable usedConsumable = action.getUsedConsumable();

        if(action.getExecuting() instanceof BattlePlayer == false) {
            return action;
        }

        BattlePlayer executing = (BattlePlayer) action.getExecuting();
        PlayerCharacter character = playerCharacterService.findCharacter(executing.getCharacterId());

        // check inventory
        if(character.getInventory().containsKey(usedConsumable.getId()) == false ||
                character.getInventory().get(usedConsumable.getId()) == 0) {
            return action;
        }

        // Execute health changes
        executing.setCurrentHealth(executing.getCurrentHealth() + usedConsumable.getHealthRestored());
        if(executing.getCurrentHealth() > executing.getMaxHealth()) {
            executing.setCurrentHealth(executing.getMaxHealth());
        }

        // Only perform atkResource Changes if for the right type, else potion just gets wasted /shrug
        if(usedConsumable.getResourceType() == executing.getCharClassId().getEnergyType()) {
            executing.setCurrentAtkResource(executing.getCurrentAtkResource() + usedConsumable.getAtkResourceRestored());
            if (executing.getCurrentAtkResource() > executing.getMaxAtkResource()) {
                executing.setCurrentAtkResource(executing.getMaxAtkResource());
            }
        }

        // Add status effects
        usedConsumable.getAttributeModifiers().stream()
                .forEach(modifier -> {
                    BattleStatus status = buildBattleStatus(modifier);
                    status.setModifiedAttribute(modifier.getAttribute());
                    status.setRoundsRemaining(usedConsumable.getDurationRounds());
                    if(usedConsumable.getDurationRounds() < 0) {
                        status.setPermanent(true);
                    }
                    else {
                        status.setPermanent(false);
                    }

                    executing.getStatusModifiers().add(status);
                });
        usedConsumable.getCombatSkillModifiers().stream()
                .forEach(modifier -> {
                    BattleStatus status = buildBattleStatus(modifier);
                    status.setModifiedSkill(modifier.getSkill());
                    status.setRoundsRemaining(usedConsumable.getDurationRounds());
                    if(usedConsumable.getDurationRounds() < 0) {
                        status.setPermanent(true);
                    }
                    else {
                        status.setPermanent(false);
                    }
                    executing.getStatusModifiers().add(status);
                });

        // Drop the item after usage
        playerCharacterService.dropItems(character, usedConsumable.getId(),1);
        return action;
    }

    private BattleStatus buildBattleStatus(AbstractStatus modifier) {
        BattleStatus status = new BattleStatus();
        status.setStatusName(modifier.getStatusName());
        status.setStatusIcon(modifier.getStatusIcon());

        if(modifier.getBonus() >= 0) {
            status.setModifierType(BattleStatus.ModifierType.RAISE);
        }
        else {
            status.setModifierType(BattleStatus.ModifierType.LOWER);
        }
        status.setAmountModifier(Math.abs(modifier.getBonus()));
        return status;
    }

    private BattleAction handleSkillAction(BattleAction action) {
        Skill usedSkill = action.getUsedSkill();
        // plus one to get to the actual max
        SkillRank rank = getSkillRank(usedSkill, action.getExecuting());

        int minDamage = usedSkill.getMinDamage() + rank.getDamageModifier();
        int maxDamage = usedSkill.getMaxDamage() + rank.getDamageModifier();

        int actionAmount;
        actionAmount = ThreadLocalRandom.current().nextInt(minDamage, maxDamage + 1);

        Weapon weaponId = null;
        if (usedSkill.getWeaponModifier() != null) {
            switch (usedSkill.getWeaponModifier()) {
                case WEAPON_MAINHAND -> weaponId = action.getExecuting().getEquipment().getMainhand();
                case WEAPON_OFFHAND -> weaponId = action.getExecuting().getEquipment().getOffhand();
                case NONE -> {}
            }
        }

        if (weaponId != null) {
            actionAmount += ThreadLocalRandom.current().nextInt(weaponId.getMinDamage(), weaponId.getMaxDamage() + 1);
        }

        float chance = ThreadLocalRandom.current().nextFloat();
        float crit = statsUtils.calculateCritChance(action.getExecuting()) / 100;
        if (chance < crit) {
            action.setCritical(true);
            actionAmount *= 2;
        }

        action.setActionAmount(actionAmount);

        // TODO stats multiplier
        // TODO def reducer
        // TODO level multiplier

        if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.OFFENSIVE)) {
            usedSkill.setTargetType(Skill.TargetType.ENEMY); // fallback
        } else if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.DEFENSIVE)) {
            usedSkill.setTargetType(Skill.TargetType.FRIEND); // fallback
        }
        if(performHitCheck(action)) {
            executeHealthChanging(action);
            executeStatusChanging(action);
        }

        int totalcost = usedSkill.getCostOfExecution();
        totalcost += rank.getCostModifier();
        performAtkUsageAndRegen(totalcost, action);

        return action;
    }

    private boolean performHitCheck(BattleAction action) {
        switch (action.getUsedSkill().getTargetType()) {
            case AREA:
            case ENEMY:
                if (hasEvadedAttack(action)) {
                    action.setActionAmount(0);
                    return false;
                }
                break;
            case FRIEND:
            case OWN:
            default:
                break;

        }
        return true;
    }

    private void performAtkUsageAndRegen(int totalcost, BattleAction action) {
        if (action.getExecuting().getCharClassId().getEnergyType() == EnergyType.RAGE) {
            action.getExecuting().consumeAtkResource(totalcost);
            if (action.isDodged() || action.isParried() || action.isBlocked()) {
                // no rage gain for attacks evaded
                return;
            }
            if (action.getUsedSkill().getTargetType() == Skill.TargetType.OWN ||
                    action.getUsedSkill().getTargetType() == Skill.TargetType.FRIEND) {
                // no rage gain for supportive attacks
                return;
            }
            action.getExecuting().generateRage(action.getActionAmount());
        } else {
            action.getExecuting().regenAtkResource();
            action.getExecuting().consumeAtkResource(totalcost);
        }
    }

    private void executeStatusChanging(BattleAction action) {

        if(action.getUsedSkill().getStatusEffects().isEmpty()) {
            return;
        }

        action.getUsedSkill().getStatusEffects().stream()
            .forEach(effect -> {
                BattleStatus status = new BattleStatus();
                status.setStatusName(effect.getStatusName());
                status.setStatusIcon(effect.getStatusIcon());

                if (effect.getStatusType() == StatusEffect.StatusEffectType.BUFF) {
                    status.setModifierType(BattleStatus.ModifierType.RAISE);
                } else {
                    status.setModifierType(BattleStatus.ModifierType.LOWER);
                }
                status.setIncapacitated(effect.isSkillIncapacitates());

                if (effect.getBuffModifiesAttribute() != null) {
                    // stat modifier
                    status.setModifiedAttribute(effect.getBuffModifiesAttribute());
                }
                if (effect.getBuffModifiesCombatSkill() != null) {
                    // combat skill mod
                    status.setModifiedSkill(effect.getBuffModifiesCombatSkill());
                }

                status.setAmountModifier(effect.getBuffModifier());
                status.setRoundsRemaining(effect.getDurationInTurns());
                status.setHealthchangePerRound(action.getActionAmount());

                if(effect.getDurationInTurns() < 0) {
                    status.setPermanent(true);
                }
                else{
                    status.setPermanent(false);
                }
                action.getTarget().getStatusModifiers().add(status);
            });
    }

    private boolean hasEvadedAttack(BattleAction action) {

        BattleParticipant target = action.getTarget();

        // No dodge/parry/block if you are incapacitated!
        if(target.getStatusModifiers().stream().anyMatch(status -> status.isIncapacitated())) {
            return false;
        }

        float chance = ThreadLocalRandom.current().nextFloat();
        float dodge = statsUtils.calculateDodgeChance(target) / 100;

        if (chance < dodge) {
            action.setDodged(true);
            return true;
        }

        chance = ThreadLocalRandom.current().nextFloat();
        float block = statsUtils.calculateBlockChance(target) / 100;
        if (chance < block) {
            action.setBlocked(true);
            return true;
        }

        chance = ThreadLocalRandom.current().nextFloat();
        float parry = statsUtils.calculateParryChance(target) / 100;
        if (chance < parry) {
            action.setParried(true);
            return true;
        }
        return false;
    }
    private void executeHealthChanging(BattleAction action) {
        switch (action.getUsedSkill().getTargetType()) {
            case AREA:
            case ENEMY:
                applyDefensiveModifiers(action.getTarget());
                action.getTarget().applyDamage(action.getActionAmount());
                break;
            case FRIEND:
                action.getTarget().applyHeal(action.getActionAmount());
                break;
            case OWN:
                action.getExecuting().applyHeal(action.getActionAmount());
                break;
        }
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
    private void applyDefensiveModifiers(BattleParticipant target) {

    }
}
