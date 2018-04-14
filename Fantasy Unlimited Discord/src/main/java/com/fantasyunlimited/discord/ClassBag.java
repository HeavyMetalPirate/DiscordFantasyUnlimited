package com.fantasyunlimited.discord;

import com.fantasyunlimited.discord.xml.CharacterClass;

public class ClassBag extends GenericsBag<CharacterClass>{

	public ClassBag() {
		super("classes");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean passSanityChecks(CharacterClass item) {
		// TODO Auto-generated method stub
		return true;
	}

}
