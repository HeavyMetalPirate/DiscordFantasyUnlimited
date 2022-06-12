package com.fantasyunlimited.rest;

import com.fantasyunlimited.battle.entity.BattleInformation;
import com.fantasyunlimited.battle.entity.BattleNPC;
import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.service.BattleService;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import com.fantasyunlimited.items.entity.Consumable;
import com.fantasyunlimited.items.entity.Dropable;
import com.fantasyunlimited.items.entity.Skill;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/battle")
public class BattleController {
    private static final Logger log = LoggerFactory.getLogger(BattleController.class);

    @Autowired
    private ControllerUtils utils;
    @Autowired
    private BattleService battleService;
    @Autowired
    private PlayerCharacterService characterService;
    @Autowired
    private DropableUtils dropableUtils;

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping(value = "/{id}/action", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<BattleDetailInfo> addBattleAction(@PathVariable("id") String id, @RequestBody BattleParticipantAction action, HttpServletRequest request) {

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

        // Gather my horse and weapons, tell my family how I died!
        BattleParticipant executing = getExecuting(action, battleInfo);
        BattleParticipant target = getTarget(action, battleInfo);
        Skill usedSkill = null;
        if(action.usedSkill() != null)
            usedSkill = executing.getCharClassId().getSkillInstances().stream()
                .filter(skill -> skill.getId().equals(action.usedSkill().id()))
                .findFirst().orElse(null);
        Dropable usedDropable = dropableUtils.getDropableItem(action.usedConsumable());
        // Final sanity checks.
        if(executing == null) {
            log.error("Could not find battle action entities. Executing: {}", action.executing());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // TODO add checks for being alive for executing and target
        switch(action.actionType()) {
            case CONSUMABLE -> {
                if(usedDropable == null) {
                    log.error("Could not find battle action entities. usedConsumable: {}", action.usedConsumable());
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                // check if the item is actually a consumable, and if it is in the inventory of the player
                if(usedDropable instanceof Consumable == false) {
                    log.error("Wrong type of consumable item. usedConsumable: {}", usedDropable.getClass().getName());
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if(executing.isDefeated()) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                if(selectedPlayer.getInventory().containsKey(usedDropable.getId()) == false ||
                        selectedPlayer.getInventory().get(usedDropable.getId()) == 0) {
                    log.error("Item '" + usedDropable.getId() + "' not in inventory.");
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }
            case FLEE, PASS -> { /* no check required */}
            case SKILL -> {
                if(usedSkill == null) {
                    log.error("Could not find usedSkill. usedSkill: {}", action.usedSkill());
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if(checkTargetType(action, battleInfo, selectedPlayer) == false) {
                    log.trace("Target type of used skill does not align with target.");
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                if (action.usedSkill().targetType() == Skill.TargetType.AREA && target != null) {
                    log.error("Found a target when no target was expected. Target: {}", target);
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (action.usedSkill().targetType() != Skill.TargetType.AREA && target == null) {
                    log.error("Did not find a target when target was expected.");
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if(executing.isDefeated()) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                if((usedSkill.getTargetType() == null ||
                        usedSkill.getTargetType() == Skill.TargetType.ENEMY ||
                        usedSkill.getTargetType() == Skill.TargetType.FRIEND) &&
                    target.isDefeated()) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
            }
        }
        // check if battle actions has any not executed actions by executing
        if (battleInfo.getActions().stream()
                .anyMatch(existing -> existing.isExecuted() == false &&
                    existing.getExecuting().getId().equals(executing.getId()))) {
            log.error("Executing already has a non executed action.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        log.trace("Passed checks, creating action...");

        switch(action.actionType()) {
            case PASS, FLEE -> {}
            case CONSUMABLE -> {
                battleInfo = battleService.addBattleAction(battleInfo, executing, (Consumable) usedDropable);
            }
            case SKILL -> {
                if(action.usedSkill().targetType() == Skill.TargetType.AREA) {
                    // add one for every target plz!
                    List<BattleNPC> aoeActions = new ArrayList<>(battleInfo.getHostiles());
                    for(BattleParticipant aoeTarget: aoeActions) {
                        battleInfo = battleService.addBattleAction(battleInfo, executing, aoeTarget, usedSkill);
                    }
                }
                else {
                    battleInfo = battleService.addBattleAction(battleInfo, executing, target, usedSkill);
                }
            }
        }
        log.trace("Action added to battle.");

        // check battle update status, if updated, calculate health changes and publish them
        // to each player separate channel
        BattleUpdate update = battleService.checkBattleStatus(battleInfo);
        if(update.hasUpdate()) {
            // update all PlayerCharacter items as well
            battleInfo.getPlayers().stream()
                    .map(player -> characterService.updateHealthAndAtk(player.getCharacterId(), player.getCurrentHealth(), player.getCurrentAtkResource()))
                    .map(player -> {
                        template.convertAndSend("/topic/updates/character/" + player.getId(), utils.buildPlayerCharacterItem(player));
                        return player;
                    }) // TODO maybe with the above we can stop doing the thing down below completely?
                    // find the player character and set it to the session for the currentCharacterPanel
                    .filter(player -> player.getId() == selectedPlayer.getId())
                    .findFirst()
                    .ifPresent(character -> utils.setPlayerCharacterToSession(request, character));

        }

        return new ResponseEntity<>(utils.buildBattleDetailInfo(battleInfo, selectedPlayer), HttpStatus.OK);
    }

    private BattleParticipant getTarget(BattleParticipantAction action, BattleInformation battleInfo) {
        if(action.usedSkill() == null) return null;

        Skill.TargetType targetType = action.usedSkill().targetType();
        if(targetType == null) {
            targetType = Skill.TargetType.ENEMY;
        }

        switch(targetType) {
            case OWN -> {
                return getExecuting(action, battleInfo);
            }
            case FRIEND -> {
                return battleInfo.getPlayers().stream()
                        .filter(player -> UUID.fromString(action.target().id())
                                .equals(player.getId()))
                        .findFirst().orElse(null);
            }
            case ENEMY -> {
                return battleInfo.getHostiles().stream()
                        .filter(hostile -> UUID.fromString(action.target().id())
                                .equals(hostile.getId()))
                        .findFirst().orElse(null);
            }
            default -> {
                return null;
            }
        }
    }
    private BattleParticipant getExecuting(BattleParticipantAction action, BattleInformation battleInfo) {
        return battleInfo.getPlayers().stream()
                .filter(player -> UUID.fromString(action.executing().id())
                        .equals(player.getId()))
                .findFirst().orElse(null);
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
