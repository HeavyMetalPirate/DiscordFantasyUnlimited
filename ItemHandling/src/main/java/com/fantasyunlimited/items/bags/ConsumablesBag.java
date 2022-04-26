package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.Consumable;
import org.springframework.stereotype.Component;

@Component
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
