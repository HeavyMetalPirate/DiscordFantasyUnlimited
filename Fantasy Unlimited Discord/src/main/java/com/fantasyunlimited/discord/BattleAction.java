package com.fantasyunlimited.discord;

import java.io.Serializable;

import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.Skill;

public class BattleAction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1864422563335663870L;
	private boolean isArea;
	private BattlePlayer playerTarget;
	private BattleNPC hostileTarget;
	
	private Skill usedSkill;
	private BattlePlayer executingPlayer;
	
	public boolean isArea() {
		return isArea;
	}
	public void setArea(boolean isArea) {
		this.isArea = isArea;
	}
	public BattlePlayer getPlayerTarget() {
		return playerTarget;
	}
	public void setPlayerTarget(BattlePlayer playerTarget) {
		this.playerTarget = playerTarget;
	}
	public BattleNPC getHostileTarget() {
		return hostileTarget;
	}
	public void setHostileTarget(BattleNPC hostileTarget) {
		this.hostileTarget = hostileTarget;
	}
	public Skill getUsedSkill() {
		return usedSkill;
	}
	public void setUsedSkill(Skill usedSkill) {
		this.usedSkill = usedSkill;
	}
	public BattlePlayer getExecutingPlayer() {
		return executingPlayer;
	}
	public void setExecutingPlayer(BattlePlayer executingPlayer) {
		this.executingPlayer = executingPlayer;
	}
}
