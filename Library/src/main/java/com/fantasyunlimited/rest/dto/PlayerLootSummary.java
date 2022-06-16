package com.fantasyunlimited.rest.dto;

import java.util.List;

public record PlayerLootSummary(
        PlayerCharacterItem character,
        int experience,
        boolean levelUp,
        int gold,
        List<InventoryItem> items
) {}
