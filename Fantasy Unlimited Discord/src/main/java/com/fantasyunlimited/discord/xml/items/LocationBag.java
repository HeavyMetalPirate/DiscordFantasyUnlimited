package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.Location;

public class LocationBag extends GenericsBag<Location> {

	public LocationBag() {
		super("locations");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean passSanityChecks(Location item) {
		// TODO Auto-generated method stub
		return true;
	}

}
