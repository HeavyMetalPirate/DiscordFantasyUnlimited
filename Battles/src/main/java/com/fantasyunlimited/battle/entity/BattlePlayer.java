package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.items.bags.ClassBag;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.CharacterClass.EnergyType;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.items.util.ItemUtils;

import javax.persistence.Convert;
import javax.persistence.Entity;

@Entity
public class BattlePlayer extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;
	private String name;
	private Long characterId;

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
