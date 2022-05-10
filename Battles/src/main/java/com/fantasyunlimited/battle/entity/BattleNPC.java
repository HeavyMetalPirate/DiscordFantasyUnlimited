package com.fantasyunlimited.battle.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fantasyunlimited.items.bags.ClassBag;
import com.fantasyunlimited.items.bags.EquipmentBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.bags.WeaponBag;
import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.HostileNPC;
import com.fantasyunlimited.items.entity.Attributes.Attribute;
import com.fantasyunlimited.items.entity.CharacterClass.EnergyType;
import com.fantasyunlimited.items.entity.Gear;
import com.fantasyunlimited.items.entity.Attributes;
import com.fantasyunlimited.items.util.ItemUtils;

import javax.persistence.Entity;

@Entity
public class BattleNPC extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;

	private String baseId;
	private HostileNPC base;

	public String getName() {
		return getBase().getName();
	}

	public HostileNPC getBase() {
		return base;
	}

	public String getBaseId() {
		return baseId;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	public void setBase(HostileNPC base) {
		this.base = base;
	}
}
