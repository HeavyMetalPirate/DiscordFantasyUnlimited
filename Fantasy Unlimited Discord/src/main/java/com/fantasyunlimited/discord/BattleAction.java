package com.fantasyunlimited.discord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fantasyunlimited.discord.entity.BattleParticipant;
import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.SkillType;
import com.fantasyunlimited.discord.xml.Skill.TargetType;

public class BattleAction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1864422563335663870L;
	private boolean isArea;
	private boolean isPass;
	private boolean executed;

	private BattleParticipant executing;
	private BattleParticipant target;

	private List<BattleParticipant> areaTargets = new ArrayList<>();

	private Skill usedSkill;

	private int actionAmount;

	public void executeAction() {
		// TODO actual battle, lmao
		if (executed) {
			return;
		}
		executed = true;
		actionAmount = 0;
		// if you die in that round, don't do actions because u ded
		if (executing.isDefeated()) {
			return;
		}

		// TODO
		actionAmount = usedSkill.getMaxDamage();
		actionAmount += usedSkill.getHighestAvailable(executing.getLevel(), executing.getAttributes())
				.getDamageModifier();

		if (usedSkill.getTargetType() == null
				&& (usedSkill.getType() == SkillType.OFFENSIVE || usedSkill.getType() == SkillType.DEBUFF)) {
			usedSkill.setTargetType(TargetType.ENEMY); // fallback
		} else if (usedSkill.getTargetType() == null
				&& (usedSkill.getType() == SkillType.DEFENSIVE || usedSkill.getType() == SkillType.BUFF)) {
			usedSkill.setTargetType(TargetType.FRIEND); // fallback
		}

		switch (usedSkill.getType()) {
		case BUFF:
		case DEBUFF:
			executeStatusChanging();
			break;
		case DEFENSIVE:
		case OFFENSIVE:
			executeHealthChanging();
			break;
		default:
			break;
		}
		int totalcost = usedSkill.getCostOfExecution();
		totalcost += usedSkill.getHighestAvailable(executing.getLevel(), executing.getAttributes())
				.getCostModifier();
		
		if (executing.getCharClass().getEnergyType() == EnergyType.RAGE) {
			executing.consumeAtkResource(totalcost);
			executing.generateRage(actionAmount);
		} else {
			executing.regenAtkResource();
			executing.consumeAtkResource(totalcost);
		}
	}

	private void executeStatusChanging() {
		// TODO how to deal with buffs
	}

	private void executeHealthChanging() {
		switch (usedSkill.getTargetType()) {
		case AREA:
			for (BattleParticipant target : areaTargets) {
				target.applyDamage(actionAmount);
			}
			break;
		case ENEMY:
			target.applyDamage(actionAmount);
			break;
		case FRIEND:
			target.applyHeal(actionAmount);
			break;
		case OWN:
			executing.applyHeal(actionAmount);
			break;
		}
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

	public List<BattleParticipant> getAreaTargets() {
		return areaTargets;
	}

	public void setAreaTargets(List<BattleParticipant> areaTargets) {
		this.areaTargets = areaTargets;
	}
}
