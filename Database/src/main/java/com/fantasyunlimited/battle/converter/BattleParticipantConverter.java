package com.fantasyunlimited.battle.converter;

import com.fantasyunlimited.battle.entity.BattleNPC;
import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import com.fantasyunlimited.battle.service.BattleParticipantCrudService;
import org.springframework.context.ApplicationContext;

import javax.persistence.AttributeConverter;

public class BattleParticipantConverter implements AttributeConverter<BattleParticipant, String> {

    private final ApplicationContext applicationContext;

    public BattleParticipantConverter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String convertToDatabaseColumn(BattleParticipant attribute) {
        if(attribute instanceof BattlePlayer player) {
            return "player/" + player.getId().toString();
        }
        else if(attribute instanceof BattleNPC npc) {
            return "npc/" + npc.getId().toString();
        }
        else if(attribute == null) {
            return null;
        }
        throw new IllegalArgumentException("Unexpected BattleParticipant implementation " + attribute.getClass().getCanonicalName());
    }

    @Override
    public BattleParticipant convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }

        BattleParticipantCrudService crudService = applicationContext.getBean(BattleParticipantCrudService.class);
        if(dbData.startsWith("player/")) {
            return crudService.findBattlePlayer(dbData.substring("player/".length()));
        }
        else {
            return crudService.findBattleNPC(dbData.substring("npc/".length()));
        }
    }
}
