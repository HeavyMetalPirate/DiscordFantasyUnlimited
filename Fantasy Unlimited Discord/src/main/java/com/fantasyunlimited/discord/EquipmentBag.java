package com.fantasyunlimited.discord;

import com.fantasyunlimited.discord.xml.Equipment;

public class EquipmentBag extends GenericsBag<Equipment>{

	public EquipmentBag() {
		super("equipment");
	}

	@Override
	public boolean passSanityChecks(Equipment item) {
		// TODO Auto-generated method stub
		return true;
	}
}
