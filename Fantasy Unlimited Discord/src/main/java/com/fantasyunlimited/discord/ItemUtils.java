package com.fantasyunlimited.discord;

import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.Consumable;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.HostileNPC;
import com.fantasyunlimited.discord.xml.Location;
import com.fantasyunlimited.discord.xml.Race;
import com.fantasyunlimited.discord.xml.Weapon;

public final class ItemUtils {
	public static final Weapon getWeapon(String id) {
		return FantasyUnlimited.getInstance().getWeaponBag().getItem(id);
	}
	public static final Equipment getEquipment(String id) {
		return FantasyUnlimited.getInstance().getEquipmentBag().getItem(id);
	}
	public static final Race getRace(String id) {
		return FantasyUnlimited.getInstance().getRaceBag().getItem(id);
	}
	public static final CharacterClass getCharacterClass(String id) {
		return FantasyUnlimited.getInstance().getClassBag().getItem(id);
	}
	public static final HostileNPC getHostileNPC(String id) {
		return FantasyUnlimited.getInstance().getHostileNPCBag().getItem(id);
	}
	public static final Location getLocation(String id) {
		return FantasyUnlimited.getInstance().getLocationsBag().getItem(id);
	}
	public static final Consumable getConsumable(String id) {
		return FantasyUnlimited.getInstance().getConsumablesBag().getItem(id);
	}
}
