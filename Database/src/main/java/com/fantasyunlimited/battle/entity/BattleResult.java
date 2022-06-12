package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.rest.dto.BattleSide;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class BattleResult {
    @Id
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;

    @OneToOne
    private BattleInformation battleInformation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BattleLoot> lootList;

    @Enumerated(EnumType.STRING)
    private BattleSide winningSide;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BattleInformation getBattleInformation() {
        return battleInformation;
    }

    public void setBattleInformation(BattleInformation battleInformation) {
        this.battleInformation = battleInformation;
    }

    public List<BattleLoot> getLootList() {
        return lootList;
    }

    public void setLootList(List<BattleLoot> lootList) {
        this.lootList = lootList;
    }

    public BattleSide getWinningSide() {
        return winningSide;
    }

    public void setWinningSide(BattleSide winningSide) {
        this.winningSide = winningSide;
    }
}
