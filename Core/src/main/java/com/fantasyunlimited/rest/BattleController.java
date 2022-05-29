package com.fantasyunlimited.rest;

import com.fantasyunlimited.battle.entity.BattleInformation;
import com.fantasyunlimited.battle.service.BattleService;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.entity.Skill;
import com.fantasyunlimited.rest.dto.BattleParticipantAction;
import com.fantasyunlimited.rest.dto.BattleParticipantDetails;
import com.fantasyunlimited.rest.dto.BattleSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ReflectPermission;
import java.util.UUID;

@Controller
@RequestMapping("/api/battle")
public class BattleController {
    private static final Logger log = LoggerFactory.getLogger(BattleController.class);

    @Autowired
    private ControllerUtils utils;
    @Autowired
    private BattleService battleService;

    @RequestMapping(value = "/{id}/action", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Void> addBattleAction(@PathVariable("id") String id, @RequestBody BattleParticipantAction action, HttpServletRequest request) {

        log.debug("Received action: {}", action);

        UUID battleId = UUID.fromString(id);
        BattleInformation battleInfo = battleService.getBattleInformation(battleId);

        if(battleInfo == null) {
            log.trace("No battle was found.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        PlayerCharacter selectedPlayer = utils.getPlayerCharacterFromSession(request);
        if(battleService.isParticipating(selectedPlayer, battleInfo) == false) {
            log.trace("Player is not participating in battle.");
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        BattleSkill usedSkill = action.usedSkill();
        switch(action.actionType()) {
            case CONSUMABLE, FLEE, PASS -> { /* no check required */}
            case SKILL -> {
                if(checkTargetType(action, battleInfo, selectedPlayer) == false) {
                    log.trace("Target type of used skill does not align with target.");
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }
        }
        log.trace("Passed checks, creating action...");

        // TODO figure out how things worked with discord and copy as good as it gets :)

        log.trace("Action added to battle.");
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Checks the combination of used skill/target type of skill and selected target of the action
     * @param action
     * @param selectedPlayer
     * @return
     */
    private boolean checkTargetType(BattleParticipantAction action, BattleInformation battleInfo, PlayerCharacter selectedPlayer) {
        Skill.TargetType targetType = action.usedSkill().targetType();
        if(targetType == null) {
            targetType = Skill.TargetType.ENEMY;
        }

        switch(targetType) {
            case AREA -> { /* Has no target check */ }
            case OWN -> {
                // figure out if target is actually selectedCharacter
                if (action.target() == null || action.target().details() == null) {
                    return false;
                }
                if(action.target().details().id().longValue() != selectedPlayer.getId()) {
                    return false;
                }
            }
            case ENEMY -> {
                if (action.target() == null || action.target().details() == null) {
                    return false;
                }
                // figure out if target is actually in the enemy list
                return battleInfo.getHostiles().stream()
                        .anyMatch(hostile -> UUID.fromString(action.target().id())
                                                    .equals(hostile.getId()));
            }
            case FRIEND -> {
                if (action.target() == null || action.target().details() == null) {
                    return false;
                }
                // figure out if target is actually in the players list
                return battleInfo.getPlayers().stream()
                        .anyMatch(player -> UUID.fromString(action.target().id())
                                                    .equals(player.getId()));
            }
        }
        return true;
    }
}
