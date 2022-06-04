package com.fantasyunlimited.rest.dto;

public record PlayerEquipmentDetails(
        PlayerStats stats,
        PlayerSecondaryStats secondaryStats,
        PlayerCombatSkills combatSkills,
        PlayerEquipment equipment
) {}
