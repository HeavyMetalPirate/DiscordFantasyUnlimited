package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Consumable;

public record ToolbarConsumableItem(
    Consumable consumable,
    int count
) {}
