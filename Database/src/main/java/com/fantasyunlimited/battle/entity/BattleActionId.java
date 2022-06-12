package com.fantasyunlimited.battle.entity;

import java.io.Serializable;
import java.util.Objects;

public class BattleActionId implements Serializable {
    private String battleId;
    private int sequence;

    public String getBattleId() {
        return battleId;
    }

    public void setBattleId(String battleId) {
        this.battleId = battleId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleActionId that = (BattleActionId) o;
        return sequence == that.sequence && battleId.equals(that.battleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(battleId, sequence);
    }
}
