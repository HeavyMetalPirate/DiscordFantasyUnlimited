package com.fantasyunlimited.discord.xml;

public class RarityClassifiedItem extends Dropable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3183450038184576126L;
	private ItemRarity rarity;

	public ItemRarity getRarity() {
		return rarity;
	}

	public void setRarity(ItemRarity rarity) {
		this.rarity = rarity;
	}
}
