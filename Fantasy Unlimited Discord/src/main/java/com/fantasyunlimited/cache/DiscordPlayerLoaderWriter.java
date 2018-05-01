package com.fantasyunlimited.cache;

import org.apache.log4j.Logger;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

public class DiscordPlayerLoaderWriter implements CacheLoaderWriter<Long, DiscordPlayer>{
	private static final Logger logger = Logger.getLogger(DiscordPlayerLoaderWriter.class);
	
	@Autowired
	private DiscordPlayerLogic playerLogic;
	
	public DiscordPlayerLoaderWriter() {
		FantasyUnlimited.autowire(this);
	}
	
	@Override
	public DiscordPlayer load(Long key) throws Exception {
		logger.debug("In Load(" + key + ")");
		return playerLogic.findByDiscordId(Long.toString(key));
	}

	@Override
	public void write(Long key, DiscordPlayer value) throws Exception {
		logger.debug("In write(" + key + ", " + value + ")");
		playerLogic.save(value);		
	}

	@Override
	public void delete(Long key) throws Exception {
		logger.debug("In delete(" + key + ")");
		DiscordPlayer player = load(key);
		playerLogic.delete(player);
	}

}
