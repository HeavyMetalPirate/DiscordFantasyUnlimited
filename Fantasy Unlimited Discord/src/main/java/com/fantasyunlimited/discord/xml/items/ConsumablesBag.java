package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.Consumable;

public class ConsumablesBag extends GenericsBag<Consumable>{

	public ConsumablesBag() {
		super("consumables");
	}

	@Override
	public boolean passSanityChecks(Consumable item) throws SanityException {
		boolean sanity = true;

		//TODO
		
		return sanity;
	}

}
