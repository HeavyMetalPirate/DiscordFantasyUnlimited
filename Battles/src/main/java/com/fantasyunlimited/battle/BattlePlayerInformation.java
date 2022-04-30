package com.fantasyunlimited.battle;

import java.io.Serializable;

import com.fantasyunlimited.battle.entity.BattleParticipant;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import com.fantasyunlimited.items.entity.Skill;

public class BattlePlayerInformation implements Serializable  {
    /**
     *
     */
    private static final long serialVersionUID = 5898708685904083520L;

    private BattlePlayer character;

    private Skill skillUsed;
    private BattleParticipant target;

    private long guildId;
    private long channelId;
    private long messageId;

    public BattlePlayer getCharacter() {
        return character;
    }
    public void setCharacter(BattlePlayer character) {
        this.character = character;
    }
    public Skill getSkillUsed() {
        return skillUsed;
    }
    public void setSkillUsed(Skill skillUsed) {
        this.skillUsed = skillUsed;
    }
    public BattleParticipant getTarget() {
        return target;
    }
    public void setTarget(BattleParticipant target) {
        this.target = target;
    }
}
