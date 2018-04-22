package com.fantasyunlimited.discord.event;

import org.apache.log4j.Logger;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.thoughtworks.xstream.XStream;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class BotInitializedHandler implements IListener<ReadyEvent>{
	private static Logger logger = Logger.getLogger(BotInitializedHandler.class);
	
	@Override
	public void handle(ReadyEvent event) {
		FantasyUnlimited bot = FantasyUnlimited.getInstance();
		bot.setPlayingText("SUCK IT SLAYDEN");
		try {
			XStream xstream = bot.initializeXStream();
			bot.getWeaponBag().initialize(xstream);
			bot.getEquipmentBag().initialize(xstream);
			bot.getRaceBag().initialize(xstream);
			bot.getClassBag().initialize(xstream);
			bot.getLocationsBag().initialize(xstream);
		}
		catch(Exception e) {
			bot.sendExceptionMessage(e);
		}
		
		logger.debug("Initialized: " + bot.getWeaponBag().getItems().size() + " Weapons");
		logger.debug("Initialized: " + bot.getEquipmentBag().getItems().size() + " Equipments");
		logger.debug("Initialized: " + bot.getRaceBag().getItems().size() + " Races");
		logger.debug("Initialized: " + bot.getClassBag() .getItems().size() + " Classes");
		logger.debug("Initialized: " + bot.getLocationsBag() .getItems().size() + " Locations");
		
	}

}
