package com.fantasyunlimited.discord.event;

import java.util.Properties;

import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.util.EmbedBuilder;

public abstract class EventHandler<T extends Event> implements IListener<T>{
	protected final Properties properties;
	protected EmbedBuilder embedBuilder;
	
	public EventHandler(Properties properties) {
		this.properties = properties;
	}
}
