package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Attributes;
import com.fantasyunlimited.items.entity.CombatSkill;
import com.fantasyunlimited.items.entity.StatusEffect;

public record BattleStatusEffect(
        String statusName,
        String statusIcon,
        boolean incapacitates,
        boolean permanent,
        int durationInRounds,
        Attributes.Attribute modifiedAttribute,
        CombatSkill modifiedSkill,
        int modifier,
        StatusEffect.StatusEffectType statusType,
        int healthChangeOverTime
) {}
