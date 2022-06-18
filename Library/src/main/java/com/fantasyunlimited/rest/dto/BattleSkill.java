package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Attributes;
import com.fantasyunlimited.items.entity.Skill;

import java.util.List;

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
        int minDamage,
        int maxDamage,
        int cost,
        int rank,
        List<BattleStatusEffect> statusEffects
) {}
