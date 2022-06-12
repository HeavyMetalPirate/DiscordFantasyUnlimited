package com.fantasyunlimited.rest.dto;

import java.util.List;

public record PlayerLootSummary(
        int experience,
        boolean levelUp,
        int gold,
        List<InventoryItem> items
) {}
