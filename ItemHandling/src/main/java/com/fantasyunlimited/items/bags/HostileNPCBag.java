package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.HostileNPC;
import com.fantasyunlimited.items.entity.Race;
import com.fantasyunlimited.items.entity.Weapon;
import com.fantasyunlimited.items.util.DropableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HostileNPCBag extends GenericsBag<HostileNPC> {
	@Autowired
	private ClassBag classBag;
	@Autowired
	private RaceBag raceBag;
	@Autowired
	private WeaponBag weaponBag;
	@Autowired
	private EquipmentBag equipmentBag;
	@Autowired
	private DropableUtils dropableUtils;

	public HostileNPCBag() {
		super("hostiles");
	}

	@Override
	public boolean passSanityChecks(HostileNPC item) throws SanityException {

		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}

		if (classBag.getItems().isEmpty()
				||raceBag.getItems().isEmpty()) {
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
		for (String loot : item.getLoottable().keySet()) {
			Double chance = item.getLoottable().get(loot);
			if (chance == null || chance < 0.1) {
				throw new SanityException("Invalid drop chance for loot '" + loot + "': " + chance);
			}
			if (dropableUtils.getDropableItem(loot) == null) {
				throw new SanityException("Loot '" + loot + "' not found in any items bag!");
			}
		}

		// TODO gear check!

		return true;
	}

	@Override
	public void initializeItemFields(HostileNPC item) {
		CharacterClass charClass = classBag.getItem(item.getClassId());
		Race race = raceBag.getItem(item.getRaceId());

		item.setCharacterClass(charClass);
		item.setRace(race);

		item.setMainhandInstance(weaponBag.getItem(item.getMainhand()));
		item.setOffhandInstance(weaponBag.getItem(item.getOffhand()));

		item.setHelmetInstance(equipmentBag.getItem(item.getHelmet()));
		item.setChestInstance(equipmentBag.getItem(item.getChest()));
		item.setGloveInstance(equipmentBag.getItem(item.getGloves()));
		item.setPantsInstance(equipmentBag.getItem(item.getPants()));
		item.setBootsInstance(equipmentBag.getItem(item.getBoots()));
		item.setRing1Instance(equipmentBag.getItem(item.getRing1()));
		item.setRing2Instance(equipmentBag.getItem(item.getRing2()));
		item.setNeckInstance(equipmentBag.getItem(item.getNeck()));

		// TODO maaaaybe loot table also?
	}
}
