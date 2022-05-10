package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.battle.entity.BattleAction;
import com.fantasyunlimited.data.converter.LocationConverter;
import com.fantasyunlimited.items.entity.Location;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class BattleInformation implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5616626735379540290L;

    @Id
    @GeneratedValue
    private UUID battleId;
    private LocalDateTime begin = LocalDateTime.now();
    @Convert(converter = LocationConverter.class)
    private Location location;

    private int currentRound = 1;
    @OneToMany(mappedBy = "battle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BattleAction> actions = new ArrayList<>();

    public UUID getBattleId() {
        return battleId;
    }

    public void setBattleId(UUID battleId) {
        this.battleId = battleId;
    }

    public LocalDateTime getBegin() {
        return begin;
    }

    public void setBegin(LocalDateTime begin) {
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
}
