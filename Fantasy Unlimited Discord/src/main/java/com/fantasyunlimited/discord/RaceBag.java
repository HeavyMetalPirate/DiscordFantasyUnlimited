package com.fantasyunlimited.discord;

import com.fantasyunlimited.discord.xml.Race;

public class RaceBag extends GenericsBag<Race>{

	public RaceBag() {
		super("races");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean passSanityChecks(Race item) {
		// TODO Auto-generated method stub
		return true;
	}

}
