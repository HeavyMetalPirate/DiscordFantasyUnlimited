package com.fantasyunlimited.items.util;

import com.fantasyunlimited.items.bags.*;
import com.fantasyunlimited.items.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemUtils {
	@Autowired
	private ClassBag classBag;
	@Autowired
	private ConsumablesBag consumablesBag;
	@Autowired
	private EquipmentBag equipmentBag;
	@Autowired
	private HostileNPCBag hostileNPCBag;
	@Autowired
	private LocationBag locationBag;
	@Autowired
	private NPCBag npcBag;
	@Autowired
	private RaceBag raceBag;
	@Autowired
	private WeaponBag weaponBag;

	public Weapon getWeapon(String id) {
		return weaponBag.getItem(id);
	}
	public Equipment getEquipment(String id) {
		return equipmentBag.getItem(id);
	}
	public Race getRace(String id) {
		return raceBag.getItem(id);
	}
	public CharacterClass getCharacterClass(String id) {
		return classBag.getItem(id);
	}
	public HostileNPC getHostileNPC(String id) {
		return hostileNPCBag.getItem(id);
	}
	public NPC getNPC(String id) {
		return npcBag.getItem(id);
	}
	public Location getLocation(String id) {
		return locationBag.getItem(id);
	}
	public Consumable getConsumable(String id) {
		return consumablesBag.getItem(id);
	}
}
