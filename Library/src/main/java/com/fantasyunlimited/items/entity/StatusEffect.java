package com.fantasyunlimited.items.entity;

import java.io.Serializable;

public class StatusEffect extends AbstractStatus implements Serializable {

    private StatusEffectType statusType;
    private Attributes.Attribute buffModifiesAttribute;
    private CombatSkill buffModifiesCombatSkill;
    private int buffModifier;
    private boolean skillIncapacitates;
    private int durationInTurns;
    private int healthChangedOverTime;

    public StatusEffectType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusEffectType statusType) {
        this.statusType = statusType;
    }

    public Attributes.Attribute getBuffModifiesAttribute() {
        return buffModifiesAttribute;
    }

    public void setBuffModifiesAttribute(Attributes.Attribute buffModifiesAttribute) {
        this.buffModifiesAttribute = buffModifiesAttribute;
    }

    public CombatSkill getBuffModifiesCombatSkill() {
        return buffModifiesCombatSkill;
    }

    public void setBuffModifiesCombatSkill(CombatSkill buffModifiesCombatSkill) {
        this.buffModifiesCombatSkill = buffModifiesCombatSkill;
    }

    public int getBuffModifier() {
        return buffModifier;
    }

    public void setBuffModifier(int buffModifier) {
        this.buffModifier = buffModifier;
    }

    public boolean isSkillIncapacitates() {
        return skillIncapacitates;
    }

    public void setSkillIncapacitates(boolean skillIncapacitates) {
        this.skillIncapacitates = skillIncapacitates;
    }

    public int getDurationInTurns() {
        return durationInTurns;
    }

    public void setDurationInTurns(int durationInTurns) {
        this.durationInTurns = durationInTurns;
    }

    public int getHealthChangedOverTime() {
        return healthChangedOverTime;
    }

    public void setHealthChangedOverTime(int healthChangedOverTime) {
        this.healthChangedOverTime = healthChangedOverTime;
    }

    public enum StatusEffectType {
        BUFF,
        DEBUFF
    }
}
