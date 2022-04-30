package com.fantasyunlimited.data.entity;

import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Gear;
import com.fantasyunlimited.items.entity.Weapon;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class CharacterEquipment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5618236600523177160L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@OneToOne(cascade = CascadeType.ALL)
	private PlayerCharacter character;

	@Column
	private String mainhand;
	@Column
	private String offhand;
	@Column
	private String helmet;
	@Column
	private String chest;
	@Column
	private String gloves;
	@Column
	private String pants;
	@Column
	private String boots;
	@Column
	private String ring1;
	@Column
	private String ring2;
	@Column
	private String neck;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public Weapon getWeapon(String id, WeaponBag weaponBag) {
		return weaponBag.getItem(id);
	}

	public Equipment getEquipment(String id, EquipmentBag equipmentBag) {
		return equipmentBag.getItem(id);
	}

	public List<Gear> getGear(WeaponBag weaponBag, EquipmentBag equipmentBag) {
		List<Gear> equip = new ArrayList<>(Arrays.asList(
				getWeapon(mainhand, weaponBag),
				getWeapon(offhand, weaponBag),
				getEquipment(helmet, equipmentBag),
				getEquipment(chest, equipmentBag),
				getEquipment(gloves, equipmentBag),
				getEquipment(pants, equipmentBag),
				getEquipment(boots, equipmentBag),
				getEquipment(ring1, equipmentBag),
				getEquipment(ring2, equipmentBag),
				getEquipment(neck, equipmentBag)));
		equip.removeAll(Collections.singleton(null));
		return equip;
	}


	public PlayerCharacter getCharacter() {
		return character;
	}

	public void setCharacter(PlayerCharacter character) {
		this.character = character;
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
