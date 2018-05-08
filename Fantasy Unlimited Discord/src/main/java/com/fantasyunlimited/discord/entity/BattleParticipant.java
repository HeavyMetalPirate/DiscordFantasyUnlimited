package com.fantasyunlimited.discord.entity;

import java.io.Serializable;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.entity.Attributes;

public abstract class BattleParticipant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4019039035977061367L;

	protected String raceId;
	protected String charClassId;

	protected int level;
	protected int currentHealth;
	protected int maxHealth;

	protected int currentAtkResource;
	protected int maxAtkResource;

	protected float regenPercentage;

	protected Attributes attributes;

	public BattleParticipant() {
	}

	protected void calculateRegeneration() {
		// Level bonus (y=(5*2^-x/15) * 2)
		// Y = X ^(1/4) + level bonus

		float levelbonus = (float) (5 * Math.pow(2, (level / 15)) * 2);
		float regen = (float) Math.pow(attributes.getWisdom(), 0.25);
		regenPercentage = regen + levelbonus;
	}

	public void applyDamage(int damage) {
		this.currentHealth -= damage;
		if (this.currentHealth < 0) {
			this.currentHealth = 0;
		}
	}

	public void applyHeal(int amount) {
		this.currentHealth += amount;
		if (this.currentHealth > this.maxHealth) {
			this.currentHealth = this.maxHealth;
		}
	}

	public void consumeAtkResource(int amount) {
		this.currentAtkResource -= amount;
		if (this.currentAtkResource < 0) {
			this.currentAtkResource = 0;
		}
	}

	public void regenAtkResource() {		
		switch(getCharClass().getEnergyType()) {
		case FOCUS:
			//TODO equipment bonus
			this.currentAtkResource += 20;
			break;
		case MANA:
			double regenamount = regenPercentage * getMaxAtkResource() / 100;
			this.currentAtkResource += (int)Math.ceil(regenamount);
			break;
		case RAGE:
			//should not use this method
			throw new UnsupportedOperationException("Rage users should use generateRage(int) instead!");
		}
		
		if (this.currentAtkResource > this.maxAtkResource) {
			this.currentAtkResource = this.maxAtkResource;
		}
	}

	public void generateRage(int damage) {
		switch(getCharClass().getEnergyType()) {
		case FOCUS:
		case MANA:
			throw new UnsupportedOperationException("Mana/Focus users should use regenAtkResource() instead!");
		case RAGE:
			break;
		}
		// lmao formula TODO I totally made this up
		this.currentAtkResource += 20;
		if (this.currentAtkResource > this.maxAtkResource) {
			this.currentAtkResource = this.maxAtkResource;
		}
	}

	public Race getRace() {
		return FantasyUnlimited.getInstance().getRaceBag().getItem(raceId);
	}

	public CharacterClass getCharClass() {
		return FantasyUnlimited.getInstance().getClassBag().getItem(charClassId);
	}

	public int getLevel() {
		return level;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public boolean isDefeated() {
		return currentHealth <= 0;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public int getCurrentAtkResource() {
		return currentAtkResource;
	}

	public void setCurrentAtkResource(int currentAtkResource) {
		this.currentAtkResource = currentAtkResource;
	}

	public int getMaxAtkResource() {
		return maxAtkResource;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setMaxAtkResource(int maxAtkResource) {
		this.maxAtkResource = maxAtkResource;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public float getRegenPercentage() {
		return regenPercentage;
	}
}
