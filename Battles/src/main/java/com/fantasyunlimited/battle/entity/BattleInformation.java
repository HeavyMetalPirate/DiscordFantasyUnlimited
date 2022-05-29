package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.battle.entity.BattleAction;
import com.fantasyunlimited.data.converter.LocationConverter;
import com.fantasyunlimited.items.entity.Location;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
public class BattleInformation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5616626735379540290L;

    @Id
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID battleId;
    private ZonedDateTime begin = ZonedDateTime.now(ZoneId.of("UTC"));

    private boolean isActive;

    @Convert(converter = LocationConverter.class)
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BattlePlayer> players = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BattleNPC> hostiles = new ArrayList<>();

    private int currentRound = 1;
    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BattleAction> actions = new ArrayList<>();

    public UUID getBattleId() {
        return battleId;
    }

    public void setBattleId(UUID battleId) {
        this.battleId = battleId;
    }

    public ZonedDateTime getBegin() {
        return begin;
    }

    public void setBegin(ZonedDateTime begin) {
        this.begin = begin;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public List<BattleAction> getActions() {
        return actions;
    }

    public void setActions(List<BattleAction> actions) {
        this.actions = actions;
    }

    public List<BattlePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<BattlePlayer> players) {
        this.players = players;
    }

    public List<BattleNPC> getHostiles() {
        return hostiles;
    }

    public void setHostiles(List<BattleNPC> hostiles) {
        this.hostiles = hostiles;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getNextActionSequence() {
        return actions.size() + 1;
    }
}
