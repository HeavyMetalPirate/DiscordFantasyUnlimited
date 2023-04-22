package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.data.converter.PlayerCharacterConverter;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;

import java.sql.Types;
import java.util.Map;
import java.util.UUID;

@Entity
public class BattleLoot {
    @Id
    @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;

    @ManyToOne
    private BattleResult result;

    @Convert(converter = PlayerCharacterConverter.class)
    private PlayerCharacter player;

    private boolean levelUp;
    private int experienceAwarded;
    private int goldAwarded;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "BattleItemLoot")
    private Map<String, Integer> items;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BattleResult getResult() {
        return result;
    }

    public void setResult(BattleResult result) {
        this.result = result;
    }

    public PlayerCharacter getPlayer() {
        return player;
    }

    public void setPlayer(PlayerCharacter player) {
        this.player = player;
    }

    public int getExperienceAwarded() {
        return experienceAwarded;
    }

    public boolean isLevelUp() {
        return levelUp;
    }

    public void setLevelUp(boolean levelUp) {
        this.levelUp = levelUp;
    }

    public void setExperienceAwarded(int experienceAwarded) {
        this.experienceAwarded = experienceAwarded;
    }

    public int getGoldAwarded() {
        return goldAwarded;
    }

    public void setGoldAwarded(int goldAwarded) {
        this.goldAwarded = goldAwarded;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }
}
