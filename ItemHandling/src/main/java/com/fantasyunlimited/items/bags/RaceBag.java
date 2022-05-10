package com.fantasyunlimited.items.bags;

import com.fantasyunlimited.items.entity.Location;
import com.fantasyunlimited.items.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RaceBag extends GenericsBag<Race> {

	public RaceBag() {
		super("races");
	}

	@Override
	public boolean passSanityChecks(Race item) throws SanityException {
		// Has name, description and id
		if (item.valuesFilled() == false) {
			throw new SanityException("At least one basic data (id, name, description) is missing!");
		}

		return true;
	}

	@Override
	public void initializeItemFields(Race item) {
		// NO OP
		// Additional configuration in CommandLineRunner Bean @ ItemConfiguration.java
	}
}
