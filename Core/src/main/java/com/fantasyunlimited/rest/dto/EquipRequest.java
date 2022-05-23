package com.fantasyunlimited.rest.dto;

import com.fantasyunlimited.data.enums.EquipmentSlot;

public record EquipRequest(String itemId, EquipmentSlot slot) {}
