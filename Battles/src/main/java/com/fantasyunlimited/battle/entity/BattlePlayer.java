package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.items.bags.ClassBag;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.CharacterClass.EnergyType;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.util.ItemUtils;

public class BattlePlayer extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;

	private PlayerCharacter base;
	private String name;
	private Long characterId;

	public BattlePlayer(PlayerCharacter base, ClassBag classBag, RaceBag raceBag, ItemUtils itemUtils, WeaponBag weaponBag, EquipmentBag equipmentBag) {
		super(classBag, raceBag, itemUtils);
		this.base = base;
		this.characterId = base.getId();

		this.raceId = base.getRaceId();
		this.charClassId = base.getClassId();

		this.name = base.getName();
		this.level = base.getCurrentLevel();
		this.attributes = base.getAttributes().convert();
		this.equipment = new BattleEquipment(base.getEquipment());

		this.maxHealth = base.getMaxHealth(weaponBag, equipmentBag);
		this.maxAtkResource = base.getMaxAtkResource(classBag, weaponBag, equipmentBag);
		this.currentHealth = base.getCurrentHealth();
		
		if(getCharClass().getEnergyType() == EnergyType.RAGE) {
			this.currentAtkResource = 0;
		}
		else {
			this.currentAtkResource = maxAtkResource;
		}
		//this.currentAtkResource = base.getCurrentAtkResource();
		
		calculateRegeneration();
	}

	public String getName() {
		return name;
	}

	public Long getCharacterId() {
		return characterId;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void setCharacterId(Long characterId) {
		this.characterId = characterId;
	}

}
