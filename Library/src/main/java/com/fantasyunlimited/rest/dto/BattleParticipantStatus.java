package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Attributes;
import com.fantasyunlimited.items.entity.CombatSkill;
import com.fantasyunlimited.items.entity.Skill;

public record BattleParticipantStatus (
        Skill.SkillType statusType,
        String name,
        String iconName,
        Attributes.Attribute attribute,
        CombatSkill combatSkill,
        int modifier,
        int healthPerRound,
        int healthOnEnd,
        boolean incapacitated,
        int roundsRemaining
) {}
