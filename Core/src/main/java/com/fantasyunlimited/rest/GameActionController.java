package com.fantasyunlimited.rest;

import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.Dropable;
import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Location;
import com.fantasyunlimited.items.entity.Weapon;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.*;
import com.fantasyunlimited.util.InventoryAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/game")
public class GameActionController {

    @Autowired
    private ControllerUtils utils;
    @Autowired
    private LocationBag locationBag;
    @Autowired
    private WeaponBag weaponBag;
    @Autowired
    private EquipmentBag equipmentBag;
    @Autowired
    private RaceBag raceBag;
    @Autowired
    private DropableUtils dropableUtils;
    @Autowired
    private PlayerCharacterService characterService;

    @RequestMapping(value = "/inventory/use", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Void> useItem(@RequestBody UseItemDetails useItemDetails, HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);

        return performInventoryAction(
                useItemDetails.itemId(),
                1,
                request,
                () -> characterService.useItemsFromInventory(selectedCharacter, useItemDetails.itemId())
        );
    }
    @RequestMapping(value = "/inventory/drop", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Void> dropItem(@RequestBody DropItemDetails useItemDetails, HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);

        return performInventoryAction(
                useItemDetails.itemId(),
                useItemDetails.count(),
                request,
                () -> characterService.dropItems(selectedCharacter, useItemDetails.itemId(), useItemDetails.count())
        );
    }
    private ResponseEntity<Void> performInventoryAction(String itemId, int count, HttpServletRequest request, InventoryAction action) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);
        Map<String, Integer> inventory = selectedCharacter.getInventory();

        // check if requested item is an actual item
        Dropable dropable = dropableUtils.getDropableItem(itemId);
        if(dropable == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // check if inventory contains requested item
        if(inventory.containsKey(itemId) == false) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // check if requested count is greater than actual item count
        if(inventory.get(itemId) < count) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        // Perform action and set result to session
        PlayerCharacter character = action.performAction();
        utils.setPlayerCharacterToSession(request, character);
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

    @RequestMapping(value = "/inventory", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<InventoryManagementDetails> getInventory(HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);

        List<InventoryItem> items = new ArrayList<>();

        for(String itemId : selectedCharacter.getInventory().keySet()) {
            Dropable item = dropableUtils.getDropableItem(itemId);
            if(item instanceof Weapon) {
                items.add(new InventoryItem(item, "weapon", selectedCharacter.getInventory().get(itemId)));
            }
            else if (item instanceof Equipment) {
                items.add(new InventoryItem(item, "equipment", selectedCharacter.getInventory().get(itemId)));
            }
            else {
                items.add(new InventoryItem(item, "consumable", selectedCharacter.getInventory().get(itemId)));
            }
        }

        items = items.stream().filter(item -> item.item() != null).collect(Collectors.toList());

        InventoryManagementDetails inventory = new InventoryManagementDetails(selectedCharacter.getGold(), items);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }

    @RequestMapping(value = "/location/{id}/battle", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<BattleBasicInfo> findBattle(@PathVariable(name = "id") String locationId, HttpServletRequest request) {

        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);
        UUID battleId = utils.getBattleIdFromSession(request);

        if(battleId != null) {
            // TODO find and build response entity for battle
        }

        // find a new battle, create and store it, then return basic information for display

        return new ResponseEntity<>(new BattleBasicInfo(null), HttpStatus.OK);
    }

    @RequestMapping(value = "/location/{id}/actions", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<List<LocationAction>> getLocationActions(@PathVariable(name = "id") String locationId, HttpServletRequest request) {
        List<LocationAction> actions = new ArrayList<>();

        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);

        if(selectedCharacter == null) {
            return new ResponseEntity<>(actions, HttpStatus.NOT_FOUND);
        }

        Location location = locationBag.getItem(locationId);

        location.getConnections().forEach(connection -> {
            Location target = locationBag.getItem(connection.getTargetLocationId());

            boolean requirementMet = true;
            String reason = null;

            if(connection.getToll() > selectedCharacter.getGold()) {
                requirementMet = false;
                reason = "location.travel.toll.unaffordable";
            }

            actions.add(new LocationAction(
                    LocationActionType.TRAVEL,
                    target.getName(),
                    target.getIconName(),
                    "/travel/" + target.getId(),
                    new TravelDetails(connection.getDuration(), connection.getToll()),
                    requirementMet,
                    reason
                    ));
        });

        location.getAllowedSecondarySkills().forEach((skill, requirement) -> {

            boolean requirementMet = true;
            String reason = null;

            if(selectedCharacter.getSecondarySkill(skill, weaponBag, equipmentBag, raceBag) < requirement) {
                requirementMet = false;
                reason = "location.skill.low";
            }

            actions.add(new LocationAction(
                    LocationActionType.SECONDARY_SKILL,
                    skill.toString(),
                    null,
                    "/location/" + locationId + "/skills/" + skill.toString().toLowerCase(),
                    new SecondarySkillDetails(requirement),
                    requirementMet,
                    reason
            ));
        });

        actions.add(new LocationAction(
                LocationActionType.GLOBAL_TRADING,
                "location.trading.global",
                "global_trading.png",
                "/trade/global",
                null,
                location.isGlobalMarketAccess(),
                null
        ));

        actions.add(new LocationAction(
                LocationActionType.TRADING,
                "location.trading.local",
                "local_trading.png",
                "/location/" + location.getId() + "/trade",
                null,
                location.isMarketAccess(),
                null
        ));

        if(location.getHostileNPCIds().isEmpty() == false) {

            boolean requirementMet = true;
            String reason = null;

            int tenPercentHealth = ((int) selectedCharacter.getMaxHealth() * 10 / 100);

            if(selectedCharacter.getCurrentHealth() <= tenPercentHealth) {
                requirementMet = false;
                reason = "location.battle.health.low";
            }

            actions.add(new LocationAction(
                    LocationActionType.COMBAT,
                    "location.combat.search",
                    "combat.png",
                    "/api/game/location/" + location.getId() + "/battle",
                    new BattleActionDetails(location.getMinimumLevel(), location.getMaximumLevel()),
                    requirementMet,
                    reason
            ));
        }

        if(location.getNpcIds().isEmpty() == false) {
            actions.add(new LocationAction(
                    LocationActionType.OTHER,
                    "location.npcs",
                    "town.png",
                    "/location/" + location.getId() + "/npcs",
                    null,
                    true,
                    null
            ));
        }

        return new ResponseEntity<>(actions, HttpStatus.OK);
    }
}
