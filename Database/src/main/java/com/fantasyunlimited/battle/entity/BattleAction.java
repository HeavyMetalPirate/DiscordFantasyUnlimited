package com.fantasyunlimited.battle.entity;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fantasyunlimited.battle.converter.BattleParticipantConverter;
import com.fantasyunlimited.data.converter.ConsumableConverter;
import com.fantasyunlimited.data.converter.SkillConverter;
import com.fantasyunlimited.items.entity.Consumable;
import com.fantasyunlimited.items.entity.Skill;

import jakarta.persistence.*;

@Entity
@IdClass(BattleActionId.class)
public class BattleAction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1864422563335663870L;

	@Id
	private String battleId;
	@Id
	private int sequence;

	private ZonedDateTime actionDate = ZonedDateTime.now(ZoneId.of("UTC"));

	private int round;
    private int ordinal;

	@ManyToOne
	private BattleInformation battle;

	private boolean isFlee;
	private boolean isArea;
	private boolean isPass;
	private boolean isIncapacitated;
	private boolean isDefeated;
	private boolean executed;

	private boolean dodged;
	private boolean blocked;
	private boolean critical;
	private boolean parried;

	@Convert(converter = BattleParticipantConverter.class)
	private BattleParticipant executing;
	@Convert(converter = BattleParticipantConverter.class)
	private BattleParticipant target;

	@Convert(converter = SkillConverter.class)
	private Skill usedSkill;

	@Convert(converter = ConsumableConverter.class)
	private Consumable usedConsumable;

	private int actionAmount;

	public BattleAction() {}

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

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Consumable getUsedConsumable() {
		return usedConsumable;
	}

	public void setUsedConsumable(Consumable usedConsumable) {
		this.usedConsumable = usedConsumable;
	}

	public BattleInformation getBattle() {
		return battle;
	}

	public void setBattle(BattleInformation battle) {
		this.battle = battle;
	}

	public BattleParticipant getExecuting() {
		return executing;
	}

	public void setExecuting(BattleParticipant executing) {
		this.executing = executing;
	}

	public BattleParticipant getTarget() {
		return target;
	}

	public void setTarget(BattleParticipant target) {
		this.target = target;
	}

	public boolean isFlee() {
		return isFlee;
	}

	public void setFlee(boolean flee) {
		isFlee = flee;
	}

	public boolean isArea() {
		return isArea;
	}

	public void setArea(boolean isArea) {
		this.isArea = isArea;
	}

	public Skill getUsedSkill() {
		return usedSkill;
	}

	public void setUsedSkill(Skill usedSkill) {
		this.usedSkill = usedSkill;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public int getActionAmount() {
		return actionAmount;
	}

	public void setActionAmount(int actionAmount) {
		this.actionAmount = actionAmount;
	}

	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public boolean isDodged() {
		return dodged;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public boolean isCritical() {
		return critical;
	}

	public void setCritical(boolean critical) {
		this.critical = critical;
	}

	public void setDodged(boolean dodged) {
		this.dodged = dodged;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public void setParried(boolean parried) {
		this.parried = parried;
	}

	public boolean isParried() {
		return parried;
	}

	public boolean isIncapacitated() {
		return isIncapacitated;
	}

	public void setIncapacitated(boolean isIncapacitated) {
		this.isIncapacitated = isIncapacitated;
	}

	public ZonedDateTime getActionDate() {
		return actionDate;
	}

	public void setActionDate(ZonedDateTime actionDate) {
		this.actionDate = actionDate;
	}

	public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

	public boolean isDefeated() {
		return isDefeated;
	}

	public void setDefeated(boolean defeated) {
		isDefeated = defeated;
	}
}
