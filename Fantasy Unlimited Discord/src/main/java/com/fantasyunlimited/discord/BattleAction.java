package com.fantasyunlimited.discord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.fantasyunlimited.discord.BattleStatus.ModifierType;
import com.fantasyunlimited.discord.entity.BattleNPC;
import com.fantasyunlimited.discord.entity.BattleParticipant;
import com.fantasyunlimited.discord.entity.BattlePlayer;
import com.fantasyunlimited.discord.xml.CharacterClass.EnergyType;
import com.fantasyunlimited.discord.xml.Skill;
import com.fantasyunlimited.discord.xml.Skill.SkillType;
import com.fantasyunlimited.discord.xml.Skill.TargetType;
import com.fantasyunlimited.discord.xml.SkillRank;
import com.fantasyunlimited.discord.xml.Weapon;

public class BattleAction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1864422563335663870L;
	private boolean isArea;
	private boolean isPass;
	private boolean isIncapacitated;
	private boolean executed;

	private boolean dodged;
	private boolean blocked;
	private boolean critical;
	private boolean parried;

	private BattleParticipant executing;
	private BattleParticipant target;

	private Map<Long, BattleParticipant> areaTargets = new HashMap<>();
	private Map<Long, Integer> areaDamage = new HashMap<>();

	public static final Integer DODGED = -2 ^ 22;
	public static final Integer BLOCKED = -2 ^ 23;
	public static final Integer PARRIED = -2 ^ 24;

	private Skill usedSkill;

	private int actionAmount;

	public void executeAction() {
		if (executed) {
			return;
		}
		executed = true;
		actionAmount = 0;
		if (isPass) {
			performAtkUsageAndRegen(0);
			return;
		}
		// if you die in that round, don't do actions because u ded
		if (executing.isDefeated()) {
			return;
		}
		if (executing.getStatusModifiers().parallelStream().anyMatch(status -> status.isIncapacitated())) {
			setIncapacitated(true);
			return;
		}

		// plus one to get to the actual max
		SkillRank rank = usedSkill.getHighestAvailable(executing.getLevel(), executing.getAttributes());
		actionAmount = ThreadLocalRandom.current().nextInt(usedSkill.getMinDamage(), usedSkill.getMaxDamage() + 1);
		actionAmount += rank.getDamageModifier();

		String weaponId = null;
		if (usedSkill.getWeaponModifier() != null) {
			switch (usedSkill.getWeaponModifier()) {
			case WEAPON_MAINHAND:
				weaponId = executing.getEquipment().getMainhand();
				if (weaponId == null) {
					break;
				}
				break;
			case WEAPON_OFFHAND:
				weaponId = executing.getEquipment().getMainhand();

			case NONE:
			default:
				weaponId = null;
				break;
			}
		}

		if (weaponId != null) {
			Weapon weapon = FantasyUnlimited.getInstance().getWeaponBag().getItem(weaponId);
			actionAmount += ThreadLocalRandom.current().nextInt(weapon.getMinDamage(), weapon.getMaxDamage() + 1);
		}

		float chance = ThreadLocalRandom.current().nextFloat();
		float crit = executing.calculateCritChance() / 100;
		if (chance < crit) {
			critical = true;
			actionAmount *= 2;
		}

		// below needs to be done once we decided which type of attack (single/area) it
		// is
		// TODO stats multiplier
		// TODO def reducer
		// TODO level multiplier

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
		totalcost += rank.getCostModifier();
		performAtkUsageAndRegen(totalcost);
	}

	private void performAtkUsageAndRegen(int totalcost) {
		if (executing.getCharClass().getEnergyType() == EnergyType.RAGE) {
			executing.consumeAtkResource(totalcost);
			if (dodged || parried || blocked) {
				// no rage gain for attacks evaded
				return;
			}
			if (usedSkill.getTargetType() == TargetType.OWN || usedSkill.getTargetType() == TargetType.FRIEND) {
				// no rage gain for supportive attacks
				return;
			}
			executing.generateRage(actionAmount);
		} else {
			executing.regenAtkResource();
			executing.consumeAtkResource(totalcost);
		}
	}

	private void executeStatusChanging() {
		switch (usedSkill.getTargetType()) {
		case AREA:

			break;
		case ENEMY:
			if (hasEvadedAttack(1L, target)) {
				actionAmount = 0;
				return;
			}
			break;
		case FRIEND:
		case OWN:
		default:
			break;

		}

		BattleStatus status = new BattleStatus();
		status.setStatusName(usedSkill.getName());
		if (usedSkill.getType() == SkillType.BUFF) {
			status.setModifierType(ModifierType.RAISE);
		} else {
			status.setModifierType(ModifierType.LOWER);
		}

		status.setIncapacitated(usedSkill.isSkillIncapacitates());

		if (usedSkill.getPreparationRounds() > 0) {
			// wind up attack
			status.setAmountModifier(0);
			status.setHealthchangePerRound(0);
			status.setHealthchangeOnEnd(actionAmount);
			status.setRoundsRemaining(usedSkill.getPreparationRounds());
		} else {
			if (usedSkill.getBuffModifiesAttribute() != null) {
				// stat modifier
				status.setModifiedAttribute(usedSkill.getBuffModifiesAttribute());
			} else if (usedSkill.getBuffModifiesCombatSkill() != null) {
				// combat skill mod
				status.setModifiedSkill(usedSkill.getBuffModifiesCombatSkill());
			} else {
				// damage / heal over time
				// nothing because all other things are added anyways
			}
			status.setAmountModifier(usedSkill.getBuffModifier());
			status.setRoundsRemaining(usedSkill.getDurationInTurns());
			status.setHealthchangePerRound(actionAmount);
		}

		switch (usedSkill.getTargetType()) {
		case AREA:
			for (Long targetId : areaTargets.keySet()) {
				BattleParticipant target = areaTargets.get(targetId);
				if (hasEvadedAttack(targetId, target)) {
					continue;
				}
				target.getStatusModifiers().add(status);
			}
			break;
		case ENEMY:
		case FRIEND:
		case OWN:
		default:
			target.getStatusModifiers().add(status);
		}
	}

	private boolean hasEvadedAttack(Long targetId, BattleParticipant target) {
		float chance = ThreadLocalRandom.current().nextFloat();
		float dodge = target.calculateDodgeChance() / 100;

		if (chance < dodge) {
			dodged = true;
			areaDamage.put(targetId, DODGED);
			return true;
		}

		chance = ThreadLocalRandom.current().nextFloat();
		float block = target.calculateBlockChance() / 100;
		if (chance < block) {
			blocked = true;

			areaDamage.put(targetId, BLOCKED);
			return true;
		}

		chance = ThreadLocalRandom.current().nextFloat();
		float parry = target.calculateParryChance() / 100;
		if (chance < parry) {
			parried = true;
			areaDamage.put(targetId, PARRIED);
			return true;
		}
		return false;
	}

	public String getNameOfParticipant(BattleParticipant participant) {
		String name = "";
		if (participant instanceof BattleNPC) {
			name = ((BattleNPC) participant).getBase().getName();
		} else {
			name = ((BattlePlayer) participant).getName();
		}
		return name;
	}

	private void executeHealthChanging() {
		switch (usedSkill.getTargetType()) {
		case AREA:

			break;
		case ENEMY:
			if (hasEvadedAttack(1L, target)) {
				actionAmount = 0;
				return;
			}
			break;
		case FRIEND:
		case OWN:
		default:
			break;

		}

		switch (usedSkill.getTargetType()) {
		case AREA:
			for (Long targetId : areaTargets.keySet()) {
				BattleParticipant target = areaTargets.get(targetId);
				if (hasEvadedAttack(targetId, target)) {
					continue;
				}
				// TODO target defensive calulations
				applyDefensiveModifiers(target);
				areaDamage.put(targetId, actionAmount);
				target.applyDamage(actionAmount);
			}
			break;
		case ENEMY:
			applyDefensiveModifiers(target);
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

	private void applyDefensiveModifiers(BattleParticipant target) {

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

	public Map<Long, BattleParticipant> getAreaTargets() {
		return areaTargets;
	}

	public void setAreaTargets(Map<Long, BattleParticipant> areaTargets) {
		this.areaTargets = areaTargets;
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

	public boolean isParried() {
		return parried;
	}

	public void setAreaDamage(Map<Long, Integer> areaDamage) {
		this.areaDamage = areaDamage;
	}

	public Map<Long, Integer> getAreaDamage() {
		return areaDamage;
	}

	public boolean isIncapacitated() {
		return isIncapacitated;
	}

	public void setIncapacitated(boolean isIncapacitated) {
		this.isIncapacitated = isIncapacitated;
	}
}
