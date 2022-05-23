package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.data.entity.Attributes;
public record PlayerStats(
        Attributes characterAttributes,
        Attributes equipmentAttributes
) {}
