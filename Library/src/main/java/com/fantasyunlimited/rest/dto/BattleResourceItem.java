package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.EnergyType;

public record BattleResourceItem(int currentHealth, int maxHealth, int currentResource, int maxResource, EnergyType energyType) {}
