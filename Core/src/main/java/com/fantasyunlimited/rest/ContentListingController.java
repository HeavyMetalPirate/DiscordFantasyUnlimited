package com.fantasyunlimited.rest;

import com.fantasyunlimited.items.bags.*;
import com.fantasyunlimited.items.entity.*;
import com.fantasyunlimited.rest.dto.ActiveUserItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/content")
@Tag(name = "Content Listings")
public class ContentListingController {

    @Autowired
    private ClassBag classBag;
    @Autowired
    private ConsumablesBag consumablesBag;
    @Autowired
    private EquipmentBag equipmentBag;
    @Autowired
    private HostileNPCBag hostileNPCBag;
    @Autowired
    private LocationBag locationBag;
    @Autowired
    private NPCBag npcBag;
    @Autowired
    private RaceBag raceBag;
    @Autowired
    private WeaponBag weaponBag;
    @Autowired
    private SkillBag skillBag;

    @Autowired
    private SimpMessagingTemplate template;
    @RequestMapping("/test")
    public ResponseEntity<Void> doTest() {

        final List<ActiveUserItem> activeUsers = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            activeUsers.add(new ActiveUserItem(
                    "Penis" + i,
                    "FOOBAR ROLLE " + i
            ));
        }

        template.convertAndSend("/topic/users", activeUsers);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/classes", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Character classes", description = "Lists all character classes")
    public ResponseEntity<Collection<CharacterClass>> getCharacterClasses() {
        return new ResponseEntity<>(classBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/classes/playable", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Playable Character classes", description = "Lists all playable character classes")
    public ResponseEntity<Collection<CharacterClass>> getPlayableCharacterClasses() {
        return new ResponseEntity<>(classBag.getItems().stream()
                                            .filter(clazz -> clazz.isHumanPlayable())
                                            .collect(Collectors.toList()), HttpStatus.OK);
    }

    @RequestMapping(value = "/classes/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Character class", description = "Returns a single class")
    public ResponseEntity<CharacterClass> getCharacterClass(@PathVariable String id) {
        return new ResponseEntity<>(classBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/races", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Races", description = "Lists all races")
    public ResponseEntity<Collection<Race>> getRaces() {
        return new ResponseEntity<>(raceBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/races/playable", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Playable Races", description = "Lists all playable races")
    public ResponseEntity<Collection<Race>> getPlayableRaces() {
        return new ResponseEntity<>(raceBag.getItems().stream()
                                            .filter(race -> race.isHumanPlayable())
                                            .collect(Collectors.toList()), HttpStatus.OK);
    }
    @RequestMapping(value = "/races/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Races", description = "Returns a single race")
    public ResponseEntity<Race> getRace(@PathVariable String id) {
        return new ResponseEntity<>(raceBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/consumables", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Consumables", description = "Lists all consumables")
    public ResponseEntity<Collection<Consumable>> getConsumables() {
        return new ResponseEntity<>(consumablesBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/consumables/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Consumable", description = "Returns a single consumable")
    public ResponseEntity<Consumable> getConsumable(@PathVariable String id) {
        return new ResponseEntity<>(consumablesBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/equipment", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Equipment", description = "Lists all equipment")
    public ResponseEntity<Collection<Equipment>> getEquipment() {
        return new ResponseEntity<>(equipmentBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/equipment/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Equipment", description = "Returns a single equipment")
    public ResponseEntity<Equipment> getEquipment(@PathVariable String id) {
        return new ResponseEntity<>(equipmentBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/weapons", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Weapons", description = "Lists all weapons")
    public ResponseEntity<Collection<Weapon>> getWeapons() {
        return new ResponseEntity<>(weaponBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/weapons/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Weapon", description = "Returns a single weapon")
    public ResponseEntity<Weapon> getWeapon(@PathVariable String id) {
        return new ResponseEntity<>(weaponBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/npcs", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "NPCs", description = "Lists all NPCs")
    public ResponseEntity<Collection<NPC>> getNPCs() {
        return new ResponseEntity<>(npcBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/npcs/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "NPC", description = "Returns a single NPC")
    public ResponseEntity<NPC> getNPC(@PathVariable String id) {
        return new ResponseEntity<>(npcBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/hostiles", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Hostile NPCs", description = "Lists all hostile NPCs")
    public ResponseEntity<Collection<HostileNPC>> getHostiles() {
        return new ResponseEntity<>(hostileNPCBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/hostiles/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Hostile NPC", description = "Returns a single hostile NPC")
    public ResponseEntity<HostileNPC> getHostileNPC(@PathVariable String id) {
        return new ResponseEntity<>(hostileNPCBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/locations", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Locations", description = "Lists all locations")
    public ResponseEntity<Collection<Location>> getLocations() {
        return new ResponseEntity<>(locationBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/locations/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Location", description = "Returns a single location")
    public ResponseEntity<Location> getLocation(@PathVariable String id) {
        return new ResponseEntity<>(locationBag.getItem(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/skills", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Skills", description = "Lists all skills")
    public ResponseEntity<Collection<Skill>> getSkills() {
        return new ResponseEntity<>(skillBag.getItems(), HttpStatus.OK);
    }
    @RequestMapping(value = "/skills/{id}", produces = "application/json", method = RequestMethod.GET)
    @Operation(summary = "Skill", description = "Returns a single skill")
    public ResponseEntity<Skill> getSkill(@PathVariable String id) {
        return new ResponseEntity<>(skillBag.getItem(id), HttpStatus.OK);
    }
}
