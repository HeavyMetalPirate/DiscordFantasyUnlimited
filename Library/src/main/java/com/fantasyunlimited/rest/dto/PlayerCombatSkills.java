package com.fantasyunlimited.rest.dto;

public record PlayerCombatSkills(
        float dodge,
        float block,
        float parry,
        float critical,
        int spellpower,
        int healpower
) {}
