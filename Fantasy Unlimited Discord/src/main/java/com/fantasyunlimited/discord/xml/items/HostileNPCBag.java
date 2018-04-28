package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.xml.HostileNPC;

public class HostileNPCBag extends GenericsBag<HostileNPC> {
	public HostileNPCBag() {
		super("hostiles");
	}

	@Override
	public boolean passSanityChecks(HostileNPC item) {
		return true;
	}
}
