package com.fantasyunlimited.data.entity;

import com.fantasyunlimited.data.converter.EquipmentConverter;
import com.fantasyunlimited.data.converter.WeaponConverter;
import com.fantasyunlimited.items.entity.Equipment;
import com.fantasyunlimited.items.entity.Gear;
import com.fantasyunlimited.items.entity.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;

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
	@Convert(converter = WeaponConverter.class)
	private Weapon mainhand;
	@Column
	@Convert(converter = WeaponConverter.class)
	private Weapon offhand;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment helmet;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment chest;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment gloves;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment pants;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment boots;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment ring1;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment ring2;
	@Column
	@Convert(converter = EquipmentConverter.class)
	private Equipment neck;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Gear> getGear() {
		List<Gear> equip = new ArrayList<>(Arrays.asList(
				mainhand,
				offhand,
				helmet,
				chest,
				gloves,
				pants,
				boots,
				ring1,
				ring2,
				neck));
		equip.removeAll(Collections.singleton(null));
		return equip;
	}


	public PlayerCharacter getCharacter() {
		return character;
	}

	public void setCharacter(PlayerCharacter character) {
		this.character = character;
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
