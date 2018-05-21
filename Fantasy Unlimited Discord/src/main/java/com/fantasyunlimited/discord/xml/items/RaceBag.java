package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.Race;

public class RaceBag extends GenericsBag<Race> {

	public RaceBag() {
		super("races");
	}

	@Override
	public boolean passSanityChecks(Race item) throws SanityException {
		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}
		
		return true;
	}

}
