package com.fantasyunlimited.battle.entity;

import java.io.Serializable;
import java.util.UUID;

import com.fantasyunlimited.data.entity.CharacterEquipment;
import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Weapon;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BattleEquipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8394333285512805743L;

	@Id
	@GeneratedValue
	private UUID id;

	private Weapon mainhand;
	private Weapon offhand;
	private Equipment helmet;
	private Equipment chest;
	private Equipment gloves;
	private Equipment pants;
	private Equipment boots;
	private Equipment ring1;
	private Equipment ring2;
	private Equipment neck;
	
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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Weapon getMainhand() {
		return mainhand;
	}
	public void setMainhand(Weapon mainhand) {
		this.mainhand = mainhand;
	}
	public Weapon getOffhand() {
		return offhand;
	}
	public void setOffhand(Weapon offhand) {
		this.offhand = offhand;
	}
	public Equipment getHelmet() {
		return helmet;
	}
	public void setHelmet(Equipment helmet) {
		this.helmet = helmet;
	}
	public Equipment getChest() {
		return chest;
	}
	public void setChest(Equipment chest) {
		this.chest = chest;
	}
	public Equipment getGloves() {
		return gloves;
	}
	public void setGloves(Equipment gloves) {
		this.gloves = gloves;
	}
	public Equipment getPants() {
		return pants;
	}
	public void setPants(Equipment pants) {
		this.pants = pants;
	}
	public Equipment getBoots() {
		return boots;
	}
	public void setBoots(Equipment boots) {
		this.boots = boots;
	}
	public Equipment getRing1() {
		return ring1;
	}
	public void setRing1(Equipment ring1) {
		this.ring1 = ring1;
	}
	public Equipment getRing2() {
		return ring2;
	}
	public void setRing2(Equipment ring2) {
		this.ring2 = ring2;
	}
	public Equipment getNeck() {
		return neck;
	}
	public void setNeck(Equipment neck) {
		this.neck = neck;
	}
}
