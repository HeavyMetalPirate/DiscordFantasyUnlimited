package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.Weapon;

public class WeaponBag extends GenericsBag<Weapon> {
	public WeaponBag() {
		super("weapons");
	}

	@Override
	public boolean passSanityChecks(Weapon item) throws SanityException {
		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}

		return true;
	}
}
