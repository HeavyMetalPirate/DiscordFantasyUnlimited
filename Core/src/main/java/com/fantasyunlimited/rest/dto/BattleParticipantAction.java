package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.battle.entity.BattleParticipant;

public record BattleParticipantAction(
        BattleActionType actionType,
        BattleParticipantDetails executing,
        BattleParticipantDetails target,
        BattleSkill usedSkill,
        InventoryItem usedConsumable
) {}
