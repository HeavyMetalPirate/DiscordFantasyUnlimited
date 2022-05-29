package com.fantasyunlimited.rest.dto;

public record BattleLogItem(
        int sequence,
        int round,
        boolean executed,
        BattleActionStatus status,
        BattleActionOutcome outcome,
        PlayerCharacterItem executing,
        PlayerCharacterItem target,
        BattleSkill usedSkill,
        int amount
) {}
