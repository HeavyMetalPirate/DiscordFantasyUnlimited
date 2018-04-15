package com.fantasyunlimited.discord.xml;

public class Attributes {
	private int endurance;
	private int strength;
	private int dexterity;
	private int wisdom;
	private int intelligence;
	private int defense;
	private int luck;
	
	private int enduranceGrowth;
	private int strenghtGrowth;
	private int dexterityGrowth;
	private int wisdomGrowth;
	private int intelligenceGrowth;
	private int defenseGrowth;
	private int luckGrowth;
	
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
	public int getEnduranceGrowth() {
		return enduranceGrowth;
	}
	public void setEnduranceGrowth(int enduranceGrowth) {
		this.enduranceGrowth = enduranceGrowth;
	}
	public int getStrenghtGrowth() {
		return strenghtGrowth;
	}
	public void setStrenghtGrowth(int strenghtGrowth) {
		this.strenghtGrowth = strenghtGrowth;
	}
	public int getDexterityGrowth() {
		return dexterityGrowth;
	}
	public void setDexterityGrowth(int dexterityGrowth) {
		this.dexterityGrowth = dexterityGrowth;
	}
	public int getWisdomGrowth() {
		return wisdomGrowth;
	}
	public void setWisdomGrowth(int wisdomGrowth) {
		this.wisdomGrowth = wisdomGrowth;
	}
	public int getIntelligenceGrowth() {
		return intelligenceGrowth;
	}
	public void setIntelligenceGrowth(int intelligenceGrowth) {
		this.intelligenceGrowth = intelligenceGrowth;
	}
	public int getDefenseGrowth() {
		return defenseGrowth;
	}
	public void setDefenseGrowth(int defenseGrowth) {
		this.defenseGrowth = defenseGrowth;
	}
	public int getLuckGrowth() {
		return luckGrowth;
	}
	public void setLuckGrowth(int luckGrowth) {
		this.luckGrowth = luckGrowth;
	}
	
	public enum Attribute {
		STRENGTH,
		ENDURANCE,
		DEXTERITY,
		WISDOM,
		INTELLIGENCE,
		DEFENSE,
		LUCK,
		ALL
	}
}
