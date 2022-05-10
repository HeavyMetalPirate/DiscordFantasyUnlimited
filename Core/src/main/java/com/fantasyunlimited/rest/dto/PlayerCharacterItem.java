package com.fantasyunlimited.rest.dto;

public record PlayerCharacterItem(String name, ClassItem characterClass, RaceItem race, LocationItem location, int level, int exp, BattleResourceItem resources) {}
