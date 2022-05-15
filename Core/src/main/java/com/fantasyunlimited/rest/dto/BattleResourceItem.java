package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.CharacterClass;

public record BattleResourceItem(int currentHealth, int maxHealth, int currentResource, int maxResource, CharacterClass.EnergyType energyType) {}
