package com.fantasyunlimited.battle.utils;

import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.entity.BattleStatus;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BattleStatsUtils {

    public float calculateLevelBonus(int level) {
        // Level bonus (y=(5*2^-x/15) * 2)
        return (float) (5 * Math.pow(2, (level / 15)) * 2);
    }

    public float calculateDodgeChance(PlayerCharacter playerCharacter) {
        float chance = calculateLevelBonus(playerCharacter.getCurrentLevel()); // base
        chance += getCombatSkillBonus(
                CombatSkill.DODGE,
                playerCharacter.getRaceId(),
                playerCharacter.getClassId(),
                playerCharacter.getEquipment().getGear()
        );
        chance += getAttributeChanceBonus(Attributes.Attribute.DEXTERITY);
        return chance >= 0 ? chance : 0;
    }

    public float calculateDodgeChance(BattleParticipant participant) {
        float chance = calculateLevelBonus(participant.getLevel()); // base
        chance += getBattleCombatSkillBonus(
                CombatSkill.DODGE,
                participant.getRaceId(),
                participant.getCharClassId(),
                participant.getCurrentGear(),
                participant.getStatusModifiers()
        );
        chance += getBattleAttributeChanceBonus(Attributes.Attribute.DEXTERITY);
        return chance >= 0 ? chance : 0;
    }

    public float calculateCritChance(PlayerCharacter playerCharacter) {
        float chance = calculateLevelBonus(playerCharacter.getCurrentLevel()); // base
        chance += getCombatSkillBonus(
                CombatSkill.CRITICAL,
                playerCharacter.getRaceId(),
                playerCharacter.getClassId(),
                playerCharacter.getEquipment().getGear()
        );
        chance += getAttributeChanceBonus(Attributes.Attribute.INTELLIGENCE); // TODO
        return chance >= 0 ? chance : 0;
    }
    public float calculateCritChance(BattleParticipant participant) {
        float chance = calculateLevelBonus(participant.getLevel()); // base
        chance += getBattleCombatSkillBonus(
                CombatSkill.CRITICAL,
                participant.getRaceId(),
                participant.getCharClassId(),
                participant.getCurrentGear(),
                participant.getStatusModifiers()
        );
        chance += getBattleAttributeChanceBonus(Attributes.Attribute.INTELLIGENCE); // TODO
        return chance >= 0 ? chance : 0;
    }

    public float calculateBlockChance(PlayerCharacter playerCharacter) {
        Weapon weapon = playerCharacter.getEquipment().getOffhand();
        if (weapon == null || (weapon != null && weapon.getType() != null && weapon.getType() != Weapon.WeaponType.SHIELD)) {
            // no block if no shield in offhand!
            return 0f;
        }

        float chance = calculateLevelBonus(playerCharacter.getCurrentLevel()); // base
        chance += getCombatSkillBonus(
                CombatSkill.BLOCK,
                playerCharacter.getRaceId(),
                playerCharacter.getClassId(),
                playerCharacter.getEquipment().getGear()
        );
        chance += getAttributeChanceBonus(Attributes.Attribute.DEFENSE);
        return chance >= 0 ? chance : 0;
    }

    public float calculateBlockChance(BattleParticipant participant) {
        Weapon weapon = participant.getEquipment().getOffhand();
        if (weapon == null || (weapon != null && weapon.getType() != null && weapon.getType() != Weapon.WeaponType.SHIELD)) {
            // no block if no shield in offhand!
            return 0f;
        }

        float chance = calculateLevelBonus(participant.getLevel()); // base
        chance += getBattleCombatSkillBonus(
                CombatSkill.BLOCK,
                participant.getRaceId(),
                participant.getCharClassId(),
                participant.getCurrentGear(),
                participant.getStatusModifiers()
        );
        chance += getBattleAttributeChanceBonus(Attributes.Attribute.DEFENSE);
        return chance >= 0 ? chance : 0;
    }

    public float calculateParryChance(PlayerCharacter playerCharacter) {
        float chance = calculateLevelBonus(playerCharacter.getCurrentLevel()); // base
        chance += getCombatSkillBonus(
                CombatSkill.PARRY,
                playerCharacter.getRaceId(),
                playerCharacter.getClassId(),
                playerCharacter.getEquipment().getGear()
        );
        chance += getAttributeChanceBonus(Attributes.Attribute.STRENGTH);
        return chance >= 0 ? chance : 0;
    }

    public float calculateParryChance(BattleParticipant participant) {
        float chance = calculateLevelBonus(participant.getLevel()); // base
        chance += getBattleCombatSkillBonus(
                CombatSkill.PARRY,
                participant.getRaceId(),
                participant.getCharClassId(),
                participant.getCurrentGear(),
                participant.getStatusModifiers()
        );
        chance += getBattleAttributeChanceBonus(Attributes.Attribute.STRENGTH);
        return chance >= 0 ? chance : 0;
    }

    public int calculateSpellpower(PlayerCharacter playerCharacter) {
        return getCombatSkillBonus(
                CombatSkill.SPELLPOWER,
                playerCharacter.getRaceId(),
                playerCharacter.getClassId(),
                playerCharacter.getEquipment().getGear()
        );
    }

    public int calculateSpellpower(BattleParticipant participant) {
        return getBattleCombatSkillBonus(
                CombatSkill.SPELLPOWER,
                participant.getRaceId(),
                participant.getCharClassId(),
                participant.getCurrentGear(),
                participant.getStatusModifiers()
        );
    }

    public int calculateHealpower(PlayerCharacter playerCharacter) {
        return getCombatSkillBonus(
                CombatSkill.HEALPOWER,
                playerCharacter.getRaceId(),
                playerCharacter.getClassId(),
                playerCharacter.getEquipment().getGear()
        );
    }

    public int calculateHealpower(BattleParticipant participant) {
        return getBattleCombatSkillBonus(
                CombatSkill.HEALPOWER,
                participant.getRaceId(),
                participant.getCharClassId(),
                participant.getCurrentGear(),
                participant.getStatusModifiers()
        );
    }

    public int getAttributeBonus(Attributes.Attribute attribute, List<Gear> currentGear) {
        AtomicInteger bonus = new AtomicInteger(0);
        for (Gear equ : currentGear) {
            if (equ.getAttributeBonuses() == null) {
                continue;
            }
            equ.getAttributeBonuses().stream().filter(atrBon -> atrBon.getAttribute() == attribute)
                    .forEach(atrBon -> bonus.addAndGet(atrBon.getBonus()));
        }

        return bonus.get();
    }

    public int getCombatSkillBonus(CombatSkill combatSkill, Race raceId, CharacterClass charClassId, List<Gear> gear) {
        AtomicInteger bonus = new AtomicInteger(0);

        for (Gear equ : gear) {
            if (equ.getSkillBonuses() == null) {
                continue;
            }
            equ.getSkillBonuses().stream().filter(skillBon -> skillBon.getSkill() == combatSkill)
                    .forEach(skillBon -> bonus.addAndGet(skillBon.getBonus()));
        }

        raceId.getBonuses().stream().filter(bon -> bon.getCombatSkill() == combatSkill)
                .forEach(bon -> bonus.addAndGet(bon.getModifier()));
        charClassId.getBonuses().stream().filter(bon -> bon.getCombatSkill() == combatSkill)
                .forEach(bon -> bonus.addAndGet(bon.getModifier()));

        return bonus.get();
    }

    public int getBattleCombatSkillBonus(CombatSkill combatSkill, Race raceId, CharacterClass charClassId, List<Gear> gear, List<BattleStatus> statusModifiers) {
        AtomicInteger bonus = new AtomicInteger(getCombatSkillBonus(combatSkill,raceId,charClassId,gear));

        for (BattleStatus status : statusModifiers) {
            if (status.getModifiedSkill() != null && status.getModifiedSkill() == combatSkill) {
                if (status.getModifierType() == BattleStatus.ModifierType.RAISE) {
                    bonus.addAndGet(status.getAmountModifier());
                } else {
                    bonus.addAndGet(-status.getAmountModifier());
                }
            }
        }
        return bonus.get();
    }

    public float getAttributeChanceBonus(Attributes.Attribute attribute) {
        return 0f;
    }

    public float getBattleAttributeChanceBonus( Attributes.Attribute attribute) {
        float bonus = getAttributeChanceBonus(attribute);
        return bonus;
    }
}
