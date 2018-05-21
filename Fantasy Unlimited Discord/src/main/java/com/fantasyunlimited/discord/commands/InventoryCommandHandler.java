package com.fantasyunlimited.discord.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageFormatUtils;
import com.fantasyunlimited.discord.MessageInformation;
import com.fantasyunlimited.discord.MessageStatus;
import com.fantasyunlimited.discord.MessageStatus.Name;
import com.fantasyunlimited.discord.xml.Consumable;
import com.fantasyunlimited.discord.xml.Equipment;
import com.fantasyunlimited.discord.xml.Weapon;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class InventoryCommandHandler extends CommandSupportsPaginatorHandler {

	public static final String CMD = "inventory";

	@Autowired
	private DiscordPlayerLogic playerLogic;

	public InventoryCommandHandler(Properties properties) {
		super(properties, CMD, false);
		FantasyUnlimited.autowire(this);
	}

	@Override
	public Triple<Integer, MessageInformation, List<String>> doDelegate(MessageReceivedEvent event) {
		DiscordPlayer player = FantasyUnlimited.getInstance().getRegisteredUserCache()
				.get(event.getAuthor().getLongID());
		PlayerCharacter character = playerLogic.getCharacter(player.getCurrentCharacter().getName());

		List<Weapon> weapons = new ArrayList<>();
		List<Equipment> equipments = new ArrayList<>();
		List<Consumable> consumables = new ArrayList<>();
		// TODO other item types

		List<String> inventory = new ArrayList<String>();

		for (String itemId : character.getInventory().keySet()) {
			if (FantasyUnlimited.getInstance().getWeaponBag().getItem(itemId) != null) {
				weapons.add(FantasyUnlimited.getInstance().getWeaponBag().getItem(itemId));
			} else if (FantasyUnlimited.getInstance().getEquipmentBag().getItem(itemId) != null) {
				equipments.add(FantasyUnlimited.getInstance().getEquipmentBag().getItem(itemId));
			} else if (FantasyUnlimited.getInstance().getConsumablesBag().getItem(itemId) != null) {
				consumables.add(FantasyUnlimited.getInstance().getConsumablesBag().getItem(itemId));
			}
		}

		inventory.add("# Weapons & Shields #");
		weapons.stream()
				.forEach(item -> inventory.add(
						"[" + MessageFormatUtils.fillStringPrefix(character.getInventory().get(item.getId()) + "x", 5)
								+ "][" + MessageFormatUtils.fillStringSuffix(item.getName(), 30) + "]"));
		inventory.add("# Equipment #");
		equipments.stream()
				.forEach(item -> inventory.add(
						"[" + MessageFormatUtils.fillStringPrefix(character.getInventory().get(item.getId()) + "x", 5)
								+ "][" + MessageFormatUtils.fillStringSuffix(item.getName(), 30) + "]"));
		inventory.add("# Consumables #");
		consumables.stream()
				.forEach(item -> inventory.add(
						"[" + MessageFormatUtils.fillStringPrefix(character.getInventory().get(item.getId()) + "x", 5)
								+ "][" + MessageFormatUtils.fillStringSuffix(item.getName(), 30) + "]"));

		inventory.add("# Others #");

		MessageInformation information = new MessageInformation();
		information.setCanBeRemoved(false);
		information.setOriginDate(event.getMessage().getTimestamp());
		information.setOriginator(event.getMessage().getAuthor());
		MessageStatus status = new MessageStatus();
		status.setName(Name.PAGINATION_TEST);
		information.setStatus(status);
		information.getVars().put("pageHeader",
				"Inventory of " + character.getName() + " (" + getDisplayNameForAuthor(event) + ")");

		return Triple.of(15, information, inventory);
	}

	@Override
	public String getDescription() {
		return "Lists the items in your current character's equipment";
	}

	@Override
	public Type getType() {
		return Type.CHARACTER;
	}
}
