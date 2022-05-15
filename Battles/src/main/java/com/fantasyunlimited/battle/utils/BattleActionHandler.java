package com.fantasyunlimited.battle.utils;

import com.fantasyunlimited.battle.entity.BattleAction;
import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.entity.BattleStatus;
import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.Skill;
import com.fantasyunlimited.items.entity.SkillRank;
import com.fantasyunlimited.items.entity.Weapon;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class BattleActionHandler {

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
        if (action.isPass()) {
            performAtkUsageAndRegen(0, action);
            return action;
        }
        // if you die in that round, don't do actions because u ded
        if (action.getExecuting().isDefeated()) {
            return action;
        }
        if (action.getExecuting().getStatusModifiers().parallelStream().anyMatch(BattleStatus::isIncapacitated)) {
            action.setIncapacitated(true);
            return action;
        }
        Skill usedSkill = action.getUsedSkill();
        // plus one to get to the actual max
        SkillRank rank = getSkillRank(usedSkill, action.getExecuting());

        int actionAmount;
        actionAmount = ThreadLocalRandom.current().nextInt(usedSkill.getMinDamage(), usedSkill.getMaxDamage() + 1);
        actionAmount += rank.getDamageModifier();

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
        float crit = action.getExecuting().calculateCritChance() / 100;
        if (chance < crit) {
            action.setCritical(true);
            actionAmount *= 2;
        }

        action.setActionAmount(actionAmount);

        // below needs to be done once we decided which type of attack (single/area) it
        // is
        // TODO stats multiplier
        // TODO def reducer
        // TODO level multiplier

        if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.OFFENSIVE || usedSkill.getType() == Skill.SkillType.DEBUFF)) {
            usedSkill.setTargetType(Skill.TargetType.ENEMY); // fallback
        } else if (usedSkill.getTargetType() == null
                && (usedSkill.getType() == Skill.SkillType.DEFENSIVE || usedSkill.getType() == Skill.SkillType.BUFF)) {
            usedSkill.setTargetType(Skill.TargetType.FRIEND); // fallback
        }

        switch (usedSkill.getType()) {
            case BUFF, DEBUFF -> executeStatusChanging(action);
            case DEFENSIVE, OFFENSIVE -> executeHealthChanging(action);
            default -> { }
        }
        int totalcost = usedSkill.getCostOfExecution();
        totalcost += rank.getCostModifier();
        performAtkUsageAndRegen(totalcost, action);

        return action;
    }

    private void performAtkUsageAndRegen(int totalcost, BattleAction action) {
        if (action.getExecuting().getCharClassId().getEnergyType() == CharacterClass.EnergyType.RAGE) {
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
        switch (action.getUsedSkill().getTargetType()) {
            case AREA:
                // TODO foo
                break;
            case ENEMY:
                if (hasEvadedAttack(action)) {
                    action.setActionAmount(0);
                    return;
                }
                break;
            case FRIEND:
            case OWN:
            default:
                break;

        }

        BattleStatus status = new BattleStatus();
        status.setStatusName(action.getUsedSkill().getName());
        if (action.getUsedSkill().getType() == Skill.SkillType.BUFF) {
            status.setModifierType(BattleStatus.ModifierType.RAISE);
        } else {
            status.setModifierType(BattleStatus.ModifierType.LOWER);
        }

        status.setIncapacitated(action.getUsedSkill().isSkillIncapacitates());

        if (action.getUsedSkill().getPreparationRounds() > 0) {
            // wind up attack
            status.setAmountModifier(0);
            status.setHealthchangePerRound(0);
            status.setHealthchangeOnEnd(action.getActionAmount());
            status.setRoundsRemaining(action.getUsedSkill().getPreparationRounds());
        } else {
            if (action.getUsedSkill().getBuffModifiesAttribute() != null) {
                // stat modifier
                status.setModifiedAttribute(action.getUsedSkill().getBuffModifiesAttribute());
            } else if (action.getUsedSkill().getBuffModifiesCombatSkill() != null) {
                // combat skill mod
                status.setModifiedSkill(action.getUsedSkill().getBuffModifiesCombatSkill());
            } else {
                // damage / heal over time
                // nothing because all other things are added anyways
            }
            status.setAmountModifier(action.getUsedSkill().getBuffModifier());
            status.setRoundsRemaining(action.getUsedSkill().getDurationInTurns());
            status.setHealthchangePerRound(action.getActionAmount());
        }

        switch (action.getUsedSkill().getTargetType()) {
            case AREA:
                // TODO understand this thing
//                for (Long targetId : areaTargets.keySet()) {
//                    BattleParticipant target = areaTargets.get(targetId);
//                    if (hasEvadedAttack(targetId, target)) {
//                        continue;
//                    }
//                    target.getStatusModifiers().add(status);
//                }
                break;
            case ENEMY:
            case FRIEND:
            case OWN:
            default:
                action.getTarget().getStatusModifiers().add(status);
        }
    }

    private boolean hasEvadedAttack(BattleAction action) {

        BattleParticipant target = action.getTarget();

        float chance = ThreadLocalRandom.current().nextFloat();
        float dodge = target.calculateDodgeChance() / 100;

        if (chance < dodge) {
            action.setDodged(true);
            return true;
        }

        chance = ThreadLocalRandom.current().nextFloat();
        float block = target.calculateBlockChance() / 100;
        if (chance < block) {
            action.setBlocked(true);
            return true;
        }

        chance = ThreadLocalRandom.current().nextFloat();
        float parry = target.calculateParryChance() / 100;
        if (chance < parry) {
            action.setParried(true);
            return true;
        }
        return false;
    }
    private void executeHealthChanging(BattleAction action) {
        switch (action.getUsedSkill().getTargetType()) {
            case ENEMY:
                if (hasEvadedAttack(action)) {
                    action.setActionAmount(0);
                    return;
                }
                break;
            case AREA:
            case FRIEND:
            case OWN:
            default:
                break;

        }

        switch (action.getUsedSkill().getTargetType()) {
            case AREA:
                // TODO try and understand what this thing did in the first place
//                for (Long targetId : areaTargets.keySet()) {
//                    BattleParticipant target = areaTargets.get(targetId);
//                    if (hasEvadedAttack(targetId, target)) {
//                        continue;
//                    }
//                    // TODO target defensive calulations
//                    applyDefensiveModifiers(target);
//                    areaDamage.put(targetId, actionAmount);
//                    target.applyDamage(actionAmount);
//                }
                break;
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
