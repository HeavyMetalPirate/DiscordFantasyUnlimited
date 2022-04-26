package com.fantasyunlimited.items.entity;

public class Weapon extends Gear {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6658084902076289428L;
	private WeaponType type;
	private Hand hand;

	private int minDamage;
	private int maxDamage;

	public WeaponType getType() {
		return type;
	}

	public void setType(WeaponType type) {
		this.type = type;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public int getMinDamage() {
		return minDamage;
	}

	public void setMinDamage(int minDamage) {
		this.minDamage = minDamage;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public void setMaxDamage(int maxDamage) {
		this.maxDamage = maxDamage;
	}

	public enum WeaponType {
		NONE("None"), SWORD("Sword"), AXE("Axe"), DAGGER("Dagger"), POLEARM("Polearm"), GREATSWORD(
				"Greatsword"), GREATAXE(
						"Greataxe"), BOW("Bow"), CROSSBOW("Crossbow"), STAFF("Staff"), WAND("Wand"), SHIELD("Shield");

		private final String value;

		private WeaponType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		public String toStringWithSuffix() {
			if (value.equals("Shield")) {
				return value + " blocking";
			}
			return value + " damage";
		}
	}

	public enum Hand {
		LEFT, RIGHT, BOTH, TWOHANDED
	}
}
