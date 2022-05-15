package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.HostileNPC;
import com.fantasyunlimited.items.entity.Location;
import com.fantasyunlimited.items.entity.TravelConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class LocationBag extends GenericsBag<Location> {
	@Autowired
	private HostileNPCBag hostileNPCBag;
	@Autowired
	private NPCBag npcBag;

	public LocationBag() {
		super("locations");
	}

	@Override
	public boolean passSanityChecks(Location item) throws SanityException {
		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}
		
		if (hostileNPCBag.getItems().isEmpty()
				|| npcBag.getItems().isEmpty()) {
			throw new SanityException("Hostile NPCs haven't been initialized yet!");
		}

		for (String hostile : item.getHostileNPCIds()) {
			HostileNPC hostileNPC = hostileNPCBag.getItem(hostile);
			if (hostileNPC == null) {
				throw new SanityException("Hostile NPC '" + hostile + "' not found!");
			}
		}
		for (String friendly : item.getNpcIds()) {
			if (npcBag.getItem(friendly) == null) {
				throw new SanityException("NPC '" + friendly + "' not found!");
			}
		}

		for (TravelConnection connection : item.getConnections()) {
			Location location = getItem(connection.getTargetLocationId());
			if (location == null) {
				throw new SanityException("Travel connection target not found: " + connection.getTargetLocationId());
			}
			if (connection.getToll() < 0) {
				throw new SanityException("Toll for connection below zero!");
			}
		}

		return true;
	}

	@Override
	public void initializeItemFields(Location item) {
		for (String friendly : item.getNpcIds()) {
			if(item.getNpcs() == null) item.setNpcs(new ArrayList<>());

			item.getNpcs().add(npcBag.getItem(friendly));
		}
	}
}
