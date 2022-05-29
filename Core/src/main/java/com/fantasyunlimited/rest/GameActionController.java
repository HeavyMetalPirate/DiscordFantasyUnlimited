package com.fantasyunlimited.rest;

import com.fantasyunlimited.battle.entity.BattleInformation;
import com.fantasyunlimited.battle.service.BattleService;
import com.fantasyunlimited.battle.utils.BattleStatsUtils;
import com.fantasyunlimited.data.entity.Attributes;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.entity.SecondarySkills;
import com.fantasyunlimited.data.service.PlayerCharacterService;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.items.util.DropableUtils;
import com.fantasyunlimited.rest.dto.*;
import com.fantasyunlimited.util.InventoryAction;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/game")
public class GameActionController {
    private Logger log = LoggerFactory.getLogger(GameActionController.class);

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
    @Autowired
    private BattleStatsUtils battleStatsUtils;

    @Autowired
    private BattleService battleService;

    @RequestMapping(value = "/battle/{id}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<BattleDetailInfo> getBattleInformation(@PathVariable("id") String battleId, HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);

        /*
         * TODOS:
         * - Fetch Battle from Database
         * - Check if current Player is participant or not
         * -- Depending on Participation status, build the toolbar or not
         * - Build the BattleDetailInfo according to the battle
         */
        UUID battleUUID = UUID.fromString(battleId);
        BattleInformation battleInformation = battleService.getBattleInformation(battleUUID);

        if(battleInformation == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        LocationItem location = new LocationItem(
                battleInformation.getLocation().getId(),
                battleInformation.getLocation().getName(),
                battleInformation.getLocation().getIconName(),
                battleInformation.getLocation().getBannerImage()
        );

        List<BattleSkill> toolbarSkills = new ArrayList<>();
        selectedCharacter.getClassId().getSkillInstances().stream()
                .map(skill -> utils.buildBattleSkill(skill, selectedCharacter))
                .forEach(toolbarSkills::add);

        int missingSkills = toolbarSkills.size() % 10;
        if(missingSkills > 0) {
            for(int i = 0; i < missingSkills; i++) {
                toolbarSkills.add(new BattleSkill(
                    "empty",
                    "empty",
                    "empty",
                    "/images/emptySlotIcon.png",
                    null,
                    null,
                    null,
                    null,
                    0,
                    0,
                    false,
                    0,
                    0,
                    0,
                    0
                ));
            }
        }

        List<InventoryItem> consumables = new ArrayList<>();
        selectedCharacter.getInventory().entrySet().stream()
                .map(entry -> {
                    String itemId = entry.getKey();
                    int itemCount = entry.getValue();
                    Dropable item = dropableUtils.getDropableItem(itemId);
                    if(item instanceof Consumable consumable && consumable.isDuringBattle()) {
                        return new InventoryItem(item, "consumable", itemCount);
                    }
                    else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(consumables::add);

        BattlePlayerDetails playerDetails = new BattlePlayerDetails(
                selectedCharacter.getId(),
                battleService.isParticipating(selectedCharacter, battleInformation),
                toolbarSkills,
                consumables
        );

        List<BattleParticipantDetails> players = new ArrayList<>();
        battleInformation.getPlayers().stream()
                .map(player -> utils.buildBattleParticipantDetails(player))
                .forEach(players::add);

        List<BattleParticipantDetails> hostiles = new ArrayList<>();
        battleInformation.getHostiles().stream()
                .map(hostile -> utils.buildBattleParticipantDetails(hostile))
                .forEach(hostiles::add);

        List<BattleLogItem> battleLog = new ArrayList<>();
        battleInformation.getActions().stream()
                .map(action -> utils.buildBattleLogItem(action))
                .forEach(battleLog::add);

        BattleDetailInfo detailInfo = new BattleDetailInfo(
                battleInformation.getBattleId().toString(),
                battleService.isBattleActive(battleInformation),
                location,
                playerDetails,
                players,
                hostiles,
                battleLog
        );

        return new ResponseEntity<>(detailInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/character/equip", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Void> equipItem(@RequestBody EquipRequest equipRequest, HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);
        try {
            utils.setPlayerCharacterToSession(request, characterService.equipItem(selectedCharacter, equipRequest.itemId(), equipRequest.slot()));
        }
        catch(IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        catch(Exception e) {
            log.error("Error equipping item.", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    @RequestMapping(value = "/character/unequip", consumes = "application/json", method = RequestMethod.POST)
    public ResponseEntity<Void> unequipItemFromSlot(@RequestBody UnequipRequest unequipRequest, HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);
        try {
           utils.setPlayerCharacterToSession(request, characterService.unequipItemFromSlot(selectedCharacter, unequipRequest.slot()));
        }
        catch(IllegalArgumentException e) {
           return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        catch(Exception e) {
            log.error("Error unequipping item.", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/player/equipment", produces = "application/json",method = RequestMethod.GET)
    public ResponseEntity<PlayerEquipmentDetails> getPlayerEquipmentDetails(HttpServletRequest request) {
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);

        Attributes equipmentAttributes = new Attributes();
        SecondarySkills equipmentSkills = new SecondarySkills();

        selectedCharacter.getEquipment().getGear().stream()
            .filter(Objects::nonNull)
            .forEach(gear -> {
                gear.getAttributeBonuses().stream()
                    .filter(Objects::nonNull)
                    .forEach(bonus -> {
                        switch(bonus.getAttribute()) {
                            case ENDURANCE -> equipmentAttributes.raiseEndurance(bonus.getBonus());
                            case STRENGTH -> equipmentAttributes.raiseStrength(bonus.getBonus());
                            case DEXTERITY -> equipmentAttributes.raiseDexterity(bonus.getBonus());
                            case WISDOM -> equipmentAttributes.raiseWisdom(bonus.getBonus());
                            case INTELLIGENCE -> equipmentAttributes.raiseIntelligence(bonus.getBonus());
                            case DEFENSE -> equipmentAttributes.raiseDefense(bonus.getBonus());
                            case LUCK -> equipmentAttributes.raiseLuck(bonus.getBonus());
                            case ALL -> equipmentAttributes.raiseAll(bonus.getBonus());
                        }
                    });

                gear.getSecondarySkillBonuses().stream()
                    .filter(Objects::nonNull)
                    .forEach(bonus -> {
                        switch (bonus.getSkill()) {
                            case MINING -> equipmentSkills.increaseMining(bonus.getBonus());
                            case ALCHEMY -> equipmentSkills.increaseAlchemy(bonus.getBonus());
                            case FISHING -> equipmentSkills.increaseFishing(bonus.getBonus());
                            case ENCHANTING -> equipmentSkills.increaseEnchanting(bonus.getBonus());
                            case WOODCUTTING -> equipmentSkills.inreaseWoodcutting(bonus.getBonus());
                        }
                    });
            });

        PlayerStats stats = new PlayerStats(
            selectedCharacter.getAttributes(),
            equipmentAttributes
        );

        PlayerSecondaryStats secondaryStats = new PlayerSecondaryStats(
            selectedCharacter.getSecondarySkills(),
            equipmentSkills
        );

        PlayerCombatSkills combatSkills = new PlayerCombatSkills(
            battleStatsUtils.calculateDodgeChance(selectedCharacter),
            battleStatsUtils.calculateBlockChance(selectedCharacter),
            battleStatsUtils.calculateParryChance(selectedCharacter),
            battleStatsUtils.calculateCritChance(selectedCharacter),
            battleStatsUtils.calculateSpellpower(selectedCharacter),
            battleStatsUtils.calculateHealpower(selectedCharacter)
        );

        PlayerEquipment equipment = new PlayerEquipment(
            selectedCharacter.getEquipment().getMainhand(),
            selectedCharacter.getEquipment().getOffhand(),
            selectedCharacter.getEquipment().getHelmet(),
            selectedCharacter.getEquipment().getChest(),
            selectedCharacter.getEquipment().getGloves(),
            selectedCharacter.getEquipment().getPants(),
            selectedCharacter.getEquipment().getBoots(),
            selectedCharacter.getEquipment().getRing1(),
            selectedCharacter.getEquipment().getRing2(),
            selectedCharacter.getEquipment().getNeck()
        );

        PlayerEquipmentDetails details = new PlayerEquipmentDetails(
            stats,
            secondaryStats,
            combatSkills,
            equipment
        );

        return new ResponseEntity<>(details, HttpStatus.OK);
    }

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
            BattleInformation battleInformation = battleService.getBattleInformation(battleId);

            BattleBasicInfo battleInfo = new BattleBasicInfo(
                    battleInformation.getBattleId().toString()
            );

            return new ResponseEntity<>(battleInfo, HttpStatus.FOUND);
        }

        // read from the database if there is a battle from last session
        // TODO group handling? here?
        BattleInformation existingBattle = battleService.findActiveBattleForPlayer(selectedCharacter);
        if(existingBattle != null) {
            BattleBasicInfo battleInfo = new BattleBasicInfo(
                    existingBattle.getBattleId().toString()
            );
            return new ResponseEntity<>(battleInfo, HttpStatus.FOUND);
        }

        // find a new battle, create and store it, then return basic information for display
        BattleInformation battleInformation = battleService.initializeBattle(Arrays.asList(selectedCharacter), locationId);

        utils.setBattleIdFromSession(request, battleInformation.getBattleId());

        BattleBasicInfo battleInfo = new BattleBasicInfo(
                battleInformation.getBattleId().toString()
        );

        return new ResponseEntity<>(battleInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "/battle/current", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<BattleBasicInfo> getCurrentActiveBattle(HttpServletRequest request) {
        UUID battleId = utils.getBattleIdFromSession(request);
        if(battleId != null) {
            BattleBasicInfo battleInfo = new BattleBasicInfo(
                    battleId.toString()
            );
            return new ResponseEntity<>(battleInfo, HttpStatus.OK);
        }
        PlayerCharacter selectedCharacter = utils.getPlayerCharacterFromSession(request);
        BattleInformation existingBattle = battleService.findActiveBattleForPlayer(selectedCharacter);
        if(existingBattle != null) {
            BattleBasicInfo battleInfo = new BattleBasicInfo(
                    existingBattle.getBattleId().toString()
            );
            return new ResponseEntity<>(battleInfo, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
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
