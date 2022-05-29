package com.fantasyunlimited.battle.entity;

import java.io.Serializable;
import java.util.UUID;

import com.fantasyunlimited.data.converter.EquipmentConverter;
import com.fantasyunlimited.data.converter.WeaponConverter;
import com.fantasyunlimited.data.entity.CharacterEquipment;
import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Weapon;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class BattleEquipment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8394333285512805743L;

	@Id
	@GeneratedValue
	@Type(type="org.hibernate.type.UUIDCharType")
	private UUID id;

	@Convert(converter = WeaponConverter.class)
	private Weapon mainhand;
	@Convert(converter = WeaponConverter.class)
	private Weapon offhand;
	@Convert(converter = EquipmentConverter.class)
	private Equipment helmet;
	@Convert(converter = EquipmentConverter.class)
	private Equipment chest;
	@Convert(converter = EquipmentConverter.class)
	private Equipment gloves;
	@Convert(converter = EquipmentConverter.class)
	private Equipment pants;
	@Convert(converter = EquipmentConverter.class)
	private Equipment boots;
	@Convert(converter = EquipmentConverter.class)
	private Equipment ring1;
	@Convert(converter = EquipmentConverter.class)
	private Equipment ring2;
	@Convert(converter = EquipmentConverter.class)
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
