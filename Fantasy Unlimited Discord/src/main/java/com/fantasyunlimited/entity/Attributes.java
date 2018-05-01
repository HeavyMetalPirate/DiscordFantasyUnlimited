package com.fantasyunlimited.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class Attributes implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1107887434301692708L;
	private int endurance;
	private int strength;
	private int dexterity;
	private int wisdom;
	private int intelligence;
	private int defense;
	private int luck;

	private int unspent;

	public Attributes() {
		
	}
	
	public int getEndurance() {
		return endurance;
	}

	public void setEndurance(int endurance) {
		this.endurance = endurance;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getDexterity() {
		return dexterity;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public int getWisdom() {
		return wisdom;
	}

	public void setWisdom(int wisdom) {
		this.wisdom = wisdom;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getLuck() {
		return luck;
	}

	public void setLuck(int luck) {
		this.luck = luck;
	}

	public int getUnspent() {
		return unspent;
	}

	public void setUnspent(int unspent) {
		this.unspent = unspent;
	}
}