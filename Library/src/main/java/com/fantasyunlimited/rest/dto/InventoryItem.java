package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Dropable;

public record InventoryItem(Dropable item, String type, int count) {}
