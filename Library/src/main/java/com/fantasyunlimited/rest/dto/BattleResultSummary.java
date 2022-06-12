package com.fantasyunlimited.rest.dto;

import java.util.List;

public record BattleResultSummary(
        BattleSide winningSide,
        List<PlayerLootSummary> lootSummaryList
) {}
