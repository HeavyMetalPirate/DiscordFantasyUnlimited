package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Attributes;
import com.fantasyunlimited.items.entity.Skill;

public record BattleSkill(
        String id,
        String name,
        String description,
        String iconName,
        Attributes.Attribute attribute,
        Skill.SkillType skillType,
        Skill.TargetType targetType,
        Skill.SkillWeaponModifier weaponModifier,
        int preparationRounds,
        int durationInTurns,
        boolean incapacitates,
        int minDamage,
        int maxDamage,
        int cost,
        int rank
) {}
