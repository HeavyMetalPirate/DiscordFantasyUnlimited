package com.fantasyunlimited.rest.dto;

import java.util.List;

public record InventoryManagementDetails(int gold, List<InventoryItem> items) {}