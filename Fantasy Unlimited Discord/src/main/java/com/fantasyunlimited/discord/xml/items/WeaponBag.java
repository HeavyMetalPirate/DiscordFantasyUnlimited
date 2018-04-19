package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.Weapon;

public class WeaponBag extends GenericsBag<Weapon> {
	public WeaponBag() {
		super("weapons");
	}

	@Override
	public boolean passSanityChecks(Weapon item) {
		// TODO Auto-generated method stub
		return true;
	}
}
