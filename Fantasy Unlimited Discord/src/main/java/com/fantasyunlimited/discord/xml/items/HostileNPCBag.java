package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Race;

public class HostileNPCBag extends GenericsBag<HostileNPC> {
	public HostileNPCBag() {
		super("hostiles");
	}

	@Override
	public boolean passSanityChecks(HostileNPC item) throws SanityException {

		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}

		// Starting gear availability check - if this fails, check order of bag
		// loading!
		if (FantasyUnlimited.getInstance().getClassBag().getItems().isEmpty()
				|| FantasyUnlimited.getInstance().getRaceBag().getItems().isEmpty()) {
			throw new SanityException(
					"Starting class or race bags empty - check the order of initialization in BotInitializedHandler!");
		}

		// check race and class availability
		CharacterClass charClass = FantasyUnlimited.getInstance().getClassBag().getItem(item.getClassId());
		if (charClass == null) {
			throw new SanityException("Class with id '" + item.getClassId() + "' not found.");
		}
		Race race = FantasyUnlimited.getInstance().getRaceBag().getItem(item.getRaceId());
		if (race == null) {
			throw new SanityException("Race with id '" + item.getRaceId() + "' not found.");
		}

		// level check
		if (item.getLevel() < 1 || item.getLevel() > 103) {
			throw new SanityException("Invalid level: " + item.getLevel());
		}

		// loot table check
		for (String loot : item.getLoottable().keySet()) {
			Double chance = item.getLoottable().get(loot);
			if (chance == null || chance < 0.1) {
				throw new SanityException("Invalid drop chance for loot '" + loot + "': " + chance);
			}
			if (FantasyUnlimited.getInstance().getDropableItem(loot) == null) {
				throw new SanityException("Loot '" + loot + "' not found in any items bag!");
			}
		}

		return true;
	}
}
