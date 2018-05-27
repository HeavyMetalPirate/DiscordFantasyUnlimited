package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.ItemUtils;
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
		
		if (FantasyUnlimited.getInstance().getHostileNPCBag().getItems().isEmpty()
				|| FantasyUnlimited.getInstance().getNpcBag().getItems().isEmpty()) {
			throw new SanityException("Hostile NPCs haven't been initialized yet!");
		}
		for (String hostile : item.getHostileNPCIds()) {
			if (ItemUtils.getHostileNPC(hostile) == null) {
				throw new SanityException("Hostile NPC '" + hostile + "' not found!");
			}
		}
		for (String friendly : item.getNpcIds()) {
			if (ItemUtils.getNPC(friendly) == null) {
				throw new SanityException("Hostile NPC '" + friendly + "' not found!");
			}
		}

		for (TravelConnection connection : item.getConnections()) {
			Location location = ItemUtils.getLocation(connection.getTargetLocationId());
			if (location == null) {
				throw new SanityException("Travel connection target not found: " + connection.getTargetLocationId());
			}
			if (connection.getToll() < 0) {
				throw new SanityException("Toll for connection below zero!");
			}
		}

		return true;
	}

}
