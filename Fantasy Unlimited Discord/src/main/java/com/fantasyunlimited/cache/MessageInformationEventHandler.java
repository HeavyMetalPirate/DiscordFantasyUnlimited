package com.fantasyunlimited.cache;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.MessageInformation;

import sx.blah.discord.util.RequestBuffer;

public class MessageInformationEventHandler implements CacheEventListener<Long, MessageInformation> {

	@Override
	public void onEvent(CacheEvent<? extends Long, ? extends MessageInformation> event) {
		switch(event.getType()) {
		case EXPIRED:
			handleExpiry(event.getOldValue());
			break;
		case CREATED:
			break;
		case EVICTED:
			handleEviction(event.getOldValue());
			break;
		case REMOVED:
			break;
		case UPDATED:
			break;
		default:
			break;
		}
	}
	
	private void handleEviction(MessageInformation information) {
		RequestBuffer.request(() -> {
			information.getMessage().removeAllReactions();
		});
		FantasyUnlimited.getInstance().editMessage(information.getMessage(), "Request evicted from cache.");
	}
	
	private void handleExpiry(MessageInformation information) {
		RequestBuffer.request(() -> {
			information.getMessage().removeAllReactions();
		});
		FantasyUnlimited.getInstance().editMessage(information.getMessage(), "Request timed out.");
	}
}
