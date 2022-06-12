package com.fantasyunlimited.battle.entity;

import com.fantasyunlimited.data.converter.HostileNPCConverter;
import com.fantasyunlimited.items.entity.HostileNPC;

import javax.persistence.Convert;
import javax.persistence.Entity;

@Entity
public class BattleNPC extends BattleParticipant {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2758409358975067328L;

	private String baseId;
	@Convert(converter = HostileNPCConverter.class)
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
