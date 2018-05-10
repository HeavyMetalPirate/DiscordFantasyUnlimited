package com.fantasyunlimited.discord.entity;

import java.io.Serializable;

import com.fantasyunlimited.entity.CharacterEquipment;

public class BattleEquipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8394333285512805743L;
	
	private String mainhand;
	private String offhand;
	private String helmet;
	private String chest;
	private String gloves;
	private String pants;
	private String boots;
	private String ring1;
	private String ring2;
	private String neck;
	
	public BattleEquipment() {}
	
	public BattleEquipment(CharacterEquipment base) {
		this.setMainhand(base.getMainhand());
		this.setOffhand(base.getOffhand());
		this.setHelmet(base.getHelmet());
		this.setChest(base.getChest());
		this.setGloves(base.getGloves());
		this.setPants(base.getPants());
		this.setBoots(base.getBoots());
		this.setRing1(base.getRing1());
		this.setRing2(base.getRing2());
		this.setNeck(base.getNeck());
	}
	
	public String getMainhand() {
		return mainhand;
	}
	public void setMainhand(String mainhand) {
		this.mainhand = mainhand;
	}
	public String getOffhand() {
		return offhand;
	}
	public void setOffhand(String offhand) {
		this.offhand = offhand;
	}
	public String getHelmet() {
		return helmet;
	}
	public void setHelmet(String helmet) {
		this.helmet = helmet;
	}
	public String getChest() {
		return chest;
	}
	public void setChest(String chest) {
		this.chest = chest;
	}
	public String getGloves() {
		return gloves;
	}
	public void setGloves(String gloves) {
		this.gloves = gloves;
	}
	public String getPants() {
		return pants;
	}
	public void setPants(String pants) {
		this.pants = pants;
	}
	public String getBoots() {
		return boots;
	}
	public void setBoots(String boots) {
		this.boots = boots;
	}
	public String getRing1() {
		return ring1;
	}
	public void setRing1(String ring1) {
		this.ring1 = ring1;
	}
	public String getRing2() {
		return ring2;
	}
	public void setRing2(String ring2) {
		this.ring2 = ring2;
	}
	public String getNeck() {
		return neck;
	}
	public void setNeck(String neck) {
		this.neck = neck;
	}
}
