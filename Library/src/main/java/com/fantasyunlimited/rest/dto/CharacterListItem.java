package com.fantasyunlimited.rest.dto;

public record CharacterListItem(Long id, String name, ClassItem characterClass, RaceItem race, LocationItem location, int level) {}
