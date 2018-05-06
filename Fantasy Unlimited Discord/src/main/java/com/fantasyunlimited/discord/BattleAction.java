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
	private boolean isPass;
	
	private BattlePlayer playerTarget;
	private BattleNPC hostileTarget;
	
	private Skill usedSkill;
	private BattlePlayer executingPlayer;
	private BattleNPC executingHostile;
	
	public void executeAction() {
		//TODO actual battle, lmao
		
		//if you die in that round, don't do actions because u ded
		if(executingPlayer != null && executingPlayer.isDefeated()) {
			return;
		}
		if(executingHostile != null && executingHostile.isDefeated()) {
			return;
		}
		
		if(playerTarget != null) {
			playerTarget.setCurrentHealth(playerTarget.getCurrentHealth() - 1);
		}
		if(hostileTarget != null) {
			hostileTarget.setCurrentHealth(hostileTarget.getCurrentHealth() - 15);
		}
	}
	
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
	public BattleNPC getExecutingHostile() {
		return executingHostile;
	}
	public void setExecutingHostile(BattleNPC executingHostile) {
		this.executingHostile = executingHostile;
	}
	public boolean isPass() {
		return isPass;
	}
	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
}
