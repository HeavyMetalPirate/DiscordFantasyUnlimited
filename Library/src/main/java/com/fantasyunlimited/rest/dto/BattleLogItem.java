package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Consumable;

public record BattleLogItem(
        int sequence,
        int ordinal,
        int round,
        long timestamp,
        boolean executed,
        BattleActionStatus status,
        BattleActionOutcome outcome,
        PlayerCharacterItem executing,
        PlayerCharacterItem target,
        BattleSkill usedSkill,
        Consumable usedConsumable,
        int amount
) {}
