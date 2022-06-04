package com.fantasyunlimited.rest.dto;

public record BattleParticipantAction(
        BattleActionType actionType,
        BattleParticipantDetails executing,
        BattleParticipantDetails target,
        BattleSkill usedSkill,
        InventoryItem usedConsumable
) {}
