package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.NPC;
import com.fantasyunlimited.items.entity.Race;
import com.fantasyunlimited.items.util.DropableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NPCBag extends GenericsBag<NPC> {
	@Autowired
	private ClassBag classBag;
	@Autowired
	private RaceBag raceBag;
	@Autowired
	private DropableUtils dropableUtils;

	public NPCBag() {
		super("npcs");
	}

	@Override
	public boolean passSanityChecks(NPC item) throws SanityException {

		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}

		// Starting gear availability check - if this fails, check order of bag
		// loading!
		if (classBag.getItems().isEmpty()
				|| raceBag.getItems().isEmpty()) {
			throw new SanityException(
					"Starting class or race bags empty - check the order of initialization in BotInitializedHandler!");
		}

		// check race and class availability
		CharacterClass charClass = classBag.getItem(item.getClassId());
		if (charClass == null) {
			throw new SanityException("Class with id '" + item.getClassId() + "' not found.");
		}
		Race race = raceBag.getItem(item.getRaceId());
		if (race == null) {
			throw new SanityException("Race with id '" + item.getRaceId() + "' not found.");
		}

		// level check
		if (item.getLevel() < 1 || item.getLevel() > 103) {
			throw new SanityException("Invalid level: " + item.getLevel());
		}

		// loot table check
		for (String loot : item.getSelling().keySet()) {
			if (dropableUtils.getDropableItem(loot) == null) {
				throw new SanityException("Selling item '" + loot + "' not found in any items bag!");
			}
		}

		return true;
	}
}
