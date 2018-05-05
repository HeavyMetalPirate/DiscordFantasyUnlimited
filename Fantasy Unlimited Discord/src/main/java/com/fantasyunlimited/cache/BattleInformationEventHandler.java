package com.fantasyunlimited.cache;

import org.apache.log4j.Logger;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import com.fantasyunlimited.discord.BattlePlayerInformation;

public class BattleInformationEventHandler implements CacheEventListener<Long, BattlePlayerInformation> {
	private static final Logger logger = Logger.getLogger(BattleInformationEventHandler.class);

	@Override
	public void onEvent(CacheEvent<? extends Long, ? extends BattlePlayerInformation> event) {
		switch (event.getType()) {
		case EXPIRED:
			break;
		case CREATED:
			break;
		case EVICTED:
			handleEvicted(event.getOldValue());
			break;
		case REMOVED:
			handleRemoved(event.getOldValue());
			break;
		case UPDATED:
			break;
		default:
			break;
		}
	}

	private void handleEvicted(BattlePlayerInformation information) {
		//TODO notification that monsters ran away
	}
	
	private void handleRemoved(BattlePlayerInformation information) {
		logger.trace("Battle Info removed: " + information.getCharacter().getCharacterId() + " for player: "
				+ information.getCharacter().getDiscordId());
	}
}
