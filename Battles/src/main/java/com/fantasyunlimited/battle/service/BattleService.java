package com.fantasyunlimited.battle.service;

import com.fantasyunlimited.battle.entity.BattleAction;
import com.fantasyunlimited.battle.entity.BattleInformation;
import com.fantasyunlimited.battle.entity.BattleNPC;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import com.fantasyunlimited.battle.utils.BattleActionHandler;
import com.fantasyunlimited.battle.utils.BattleUtils;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.entity.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BattleService {
    private static final Logger log = LoggerFactory.getLogger(BattleService.class);

    @Autowired
    private BattleCrudService crudService;
    @Autowired
    private BattleUtils battleUtils;
    @Autowired
    private BattleActionHandler actionHandler;

    @Autowired
    private LocationBag locationBag;

    @Transactional(readOnly = true)
    public boolean isBattleActive(BattleInformation battleInformation) {
        boolean anyPlayersAlive = false;
        boolean anyHostilesAlive = false;

        anyPlayersAlive = battleInformation.getPlayers().stream()
                .anyMatch(player -> player.isDefeated() == false);
        anyHostilesAlive = battleInformation.getHostiles().stream()
                .anyMatch(hostile -> hostile.isDefeated() == false);

        return anyPlayersAlive == false || anyHostilesAlive == false;
    }

    @Transactional
    public boolean isParticipating(PlayerCharacter character, BattleInformation battleInformation) {
        return battleInformation.getPlayers().stream().anyMatch(player -> player.getCharacterId().longValue() == character.getId());
    }

    @Transactional(readOnly = true)
    public BattleInformation findActiveBattleForPlayer(PlayerCharacter character) {
        BattleInformation battle = crudService.getCurrentCharacterBattle(character);

        if(battle != null && battle.getLocation().getId()
                                    .equals(character.getLocationId().getId()) == false) {
            // location check - if they don't match, let the character flee from battle
            // non-matching should in theory only be possible if someone modifies the database directly
            // TODO
        }
        return battle;
    }

    public BattleInformation initializeBattle(List<PlayerCharacter> players, String locationId) {
        // find out who is eligible - only people in the same location that are not in a battle
        List<PlayerCharacter> inLocation = new ArrayList<>();
        players.stream()
                .filter(character -> character.getLocationId().getId().equals(locationId))
                .forEach(inLocation::add);

        List<PlayerCharacter> notInBattle = new ArrayList<>();
        inLocation.stream()
                .filter(character -> crudService.characterInActiveBattle(character) == false)
                .forEach(notInBattle::add);

        log.debug("Found characters for battle: {}", notInBattle);
        if(notInBattle.size() == 0) {
            throw new IllegalStateException("All characters are either not in the requested location or in an active battle.");
        }
        final Location location = locationBag.getItem(locationId);
        if(location == null) {
            throw new IllegalArgumentException("Location with id " + locationId + " not found.");
        }
        List<BattleNPC> battleNPCs = new ArrayList<>();
        battleUtils.findOpponents(location).stream()
                .map(hostile -> battleUtils.initializeHostileNPC(hostile))
                .forEach(battleNPCs::add);

        if(battleNPCs.size() == 0) {
            throw new IllegalStateException("No NPCs could've been found for location " + location);
        }

        BattleInformation battleInformation = new BattleInformation();
        battleInformation.setActive(true);
        battleInformation.setLocation(location);

        List<BattlePlayer> battlePlayers = new ArrayList<>();
        inLocation.stream()
                .map(character -> battleUtils.initializeBattlePlayer(character))
                .peek(character -> character.setBattleInformation(battleInformation))
                .forEach(battlePlayers::add);

        battleInformation.setPlayers(battlePlayers);
        battleInformation.setHostiles(battleNPCs);

        battleNPCs.forEach(npc -> npc.setBattleInformation(battleInformation));

        return crudService.saveBattle(battleInformation);
    }

    public BattleInformation getBattleInformation(UUID battleId) {
        return crudService.findBattle(battleId);
    }
}
