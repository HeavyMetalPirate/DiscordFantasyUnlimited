package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.Equipment;
import org.springframework.stereotype.Component;

@Component
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
