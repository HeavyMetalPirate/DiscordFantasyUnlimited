package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.TravelConnection;

public class LocationBag extends GenericsBag<Location> {

	public LocationBag() {
		super("locations");
	}

	@Override
	public boolean passSanityChecks(Location item) throws SanityException {
		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}
		
		//Check NPCs
		//TODO friendly NPCs
		if(FantasyUnlimited.getInstance().getHostileNPCBag().getItems().isEmpty()) {
			throw new SanityException("Hostile NPCs haven't been initialized yet!");
		}
		for(String hostile : item.getHostileNPCIds()) {
			if(FantasyUnlimited.getInstance().getHostileNPCBag().getItem(hostile) == null) {
				throw new SanityException("Hostile NPC '" + hostile + "' not found!");
			}
		}
		for(String friendly: item.getNpcIds()) {
			//TODO
		}
		
		for(TravelConnection connection: item.getConnections()) {
			Location location = FantasyUnlimited.getInstance().getLocationsBag().getItem(connection.getTargetLocationId());
			if(location == null) {
				throw new SanityException("Travel connection target not found: " + connection.getTargetLocationId());
			}
			if(connection.getToll() < 0) {
				throw new SanityException("Toll for connection below zero!");
			}
		}
		
		return true;
	}

}
