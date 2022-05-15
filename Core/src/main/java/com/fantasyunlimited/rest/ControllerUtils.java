package com.fantasyunlimited.rest;

import com.fantasyunlimited.data.entity.PlayerCharacter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
public class ControllerUtils {

    public UUID getBattleIdFromSession(HttpServletRequest request) {
        return (UUID) request.getSession().getAttribute("battleId");
    }

    public void setBattleIdFromSession(HttpServletRequest request, UUID battleId) {
        request.getSession().setAttribute("battleId", battleId);
    }

    public PlayerCharacter getPlayerCharacterFromSession(HttpServletRequest request) {
        return (PlayerCharacter) request.getSession().getAttribute("selectedCharacter");
    }

    public void setPlayerCharacterToSession(HttpServletRequest request, PlayerCharacter selectedCharacter) {
        request.getSession().setAttribute("selectedCharacter", selectedCharacter);
    }

}
