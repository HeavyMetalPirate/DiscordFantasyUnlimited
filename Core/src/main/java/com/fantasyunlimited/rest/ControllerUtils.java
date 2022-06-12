package com.fantasyunlimited.rest;

import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.utils.service.DTOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
@Qualifier("controllerUtils")
public class ControllerUtils extends DTOUtils {

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
