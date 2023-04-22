package com.fantasyunlimited.data.converter;

import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import org.springframework.context.ApplicationContext;

import jakarta.persistence.AttributeConverter;

public class PlayerCharacterConverter implements AttributeConverter<PlayerCharacter, String> {

    private final ApplicationContext applicationContext;

    public PlayerCharacterConverter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String convertToDatabaseColumn(PlayerCharacter attribute) {
        return "" + attribute.getId();
    }

    @Override
    public PlayerCharacter convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;
        Long playerId = Long.parseLong(dbData);

        PlayerCharacterService playerService = applicationContext.getBean(PlayerCharacterService.class);
        return playerService.findCharacter(playerId);
    }
}
