package com.fantasyunlimited.discord.xml.items;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
import com.fantasyunlimited.discord.xml.GenericItem;

public class ClassBag extends GenericsBag<CharacterClass>{

	public ClassBag() {
		super("classes");
	}

	@Override
	public boolean passSanityChecks(CharacterClass item) throws SanityException {
		boolean sanity = true;

		//Has name, description and id
		if(item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}
		
		//Stats budget check
		int statsbudget = 0;
		statsbudget += item.getAttributes().getDefense();
		statsbudget += item.getAttributes().getDexterity();
		statsbudget += item.getAttributes().getEndurance();
		statsbudget += item.getAttributes().getIntelligence();
		statsbudget += item.getAttributes().getLuck();
		statsbudget += item.getAttributes().getStrength();
		statsbudget += item.getAttributes().getWisdom();
		
		if(statsbudget > 30) {
			throw new SanityException("Stats budget expected: 30; is: " + statsbudget);
		}
		
		//Growth budget check
		int growthbudget = 0;
		growthbudget += item.getAttributes().getDefenseGrowth();
		growthbudget += item.getAttributes().getDexterityGrowth();
		growthbudget += item.getAttributes().getEnduranceGrowth();
		growthbudget += item.getAttributes().getIntelligenceGrowth();
		growthbudget += item.getAttributes().getLuckGrowth();
		growthbudget += item.getAttributes().getStrengthGrowth();
		growthbudget += item.getAttributes().getWisdomGrowth();
		
		if(growthbudget > 5) {
			throw new SanityException("Stats growth budget expected: 5; is: " + growthbudget);
		}
		
		//Starting gear availability check - if this fails, check order of bag loading!
		if(FantasyUnlimited.getInstance().getEquipmentBag().getItems().isEmpty() || 
				FantasyUnlimited.getInstance().getWeaponBag().getItems().isEmpty()) {
			throw new SanityException("Starting gear bags empty - check the order of initialization in BotInitializedHandler!");
		}
		
		if(item.getStartingHelmet().equals("-1") == false && item.getStartingHelmet().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingHelmet());
			if(equipment == null) {
				throw new SanityException("Starting helmet not found!");
			}
		}
		if(item.getStartingChest().equals("-1") == false && item.getStartingChest().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingChest());
			if(equipment == null) {
				throw new SanityException("Starting chest not found!");
			}
		}
		if(item.getStartingGloves().equals("-1") == false && item.getStartingGloves().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingGloves());
			if(equipment == null) {
				throw new SanityException("Starting gloves not found!");
			}
		}
		if(item.getStartingPants().equals("-1") == false && item.getStartingPants().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingPants());
			if(equipment == null) {
				throw new SanityException("Starting pants not found!");
			}
		}
		if(item.getStartingBoots().equals("-1") == false && item.getStartingBoots().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingBoots());
			if(equipment == null) {
				throw new SanityException("Starting boots not found!");
			}
		}
		if(item.getStartingRing1().equals("-1") == false && item.getStartingRing1().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingRing1());
			if(equipment == null) {
				throw new SanityException("Starting ring 1 not found!");
			}
		}
		if(item.getStartingRing2().equals("-1") == false && item.getStartingRing2().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingRing2());
			if(equipment == null) {
				throw new SanityException("Starting ring 2 not found!");
			}
		}
		if(item.getStartingNeck().equals("-1") == false && item.getStartingNeck().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getEquipmentBag().getItem(item.getStartingNeck());
			if(equipment == null) {
				throw new SanityException("Starting neck not found!");
			}
		}
		if(item.getStartingMainhand().equals("-1") == false && item.getStartingMainhand().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getWeaponBag().getItem(item.getStartingMainhand());
			if(equipment == null) {
				throw new SanityException("Starting mainhand not found!");
			}
		}
		if(item.getStartingOffhand().equals("-1") == false && item.getStartingOffhand().isEmpty() == false) {
			GenericItem equipment = FantasyUnlimited.getInstance().getWeaponBag().getItem(item.getStartingOffhand());
			if(equipment == null) {
				throw new SanityException("Starting offhand not found!");
			}
		}
		
		return sanity;
	}

}
