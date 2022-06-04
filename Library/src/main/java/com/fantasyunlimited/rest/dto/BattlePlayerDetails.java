package com.fantasyunlimited.rest.dto;

import java.util.List;

public record BattlePlayerDetails(
        long id,
        boolean participation,
        List<BattleSkill> toolbarSkills,
        List<InventoryItem> consumables
) {}
