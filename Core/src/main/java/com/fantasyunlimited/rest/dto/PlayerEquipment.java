package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Weapon;

public record PlayerEquipment(
    Weapon mainhand,
    Weapon offhand,
    Equipment helmet,
    Equipment chest,
    Equipment gloves,
    Equipment pants,
    Equipment boots,
    Equipment ring1,
    Equipment ring2,
    Equipment neck
) {}
