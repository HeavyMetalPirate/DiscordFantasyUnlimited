package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.Weapon;
import org.springframework.stereotype.Component;

@Component
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

	@Override
	public void initializeItemFields(Weapon item) {
		// NO OP
	}
}
