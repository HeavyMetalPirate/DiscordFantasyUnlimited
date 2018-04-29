package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.Equipment;

public class EquipmentBag extends GenericsBag<Equipment>{

	public EquipmentBag() {
		super("equipment");
	}

	@Override
	public boolean passSanityChecks(Equipment item) throws SanityException {
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}
		return true;
	}
}
