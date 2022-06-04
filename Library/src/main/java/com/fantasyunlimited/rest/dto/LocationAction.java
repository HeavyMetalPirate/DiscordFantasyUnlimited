package com.fantasyunlimited.rest.dto;

public record LocationAction(LocationActionType type, String text, String iconName, String endpoint, Object details, boolean requirementMet, String reason) {}
