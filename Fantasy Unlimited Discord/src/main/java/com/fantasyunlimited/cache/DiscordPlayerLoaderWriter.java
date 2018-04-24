package com.fantasyunlimited.cache;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.logic.DiscordPlayerLogic;

public class DiscordPlayerLoaderWriter implements CacheLoaderWriter<Long, DiscordPlayer>{

	@Autowired
	private DiscordPlayerLogic playerLogic;
	
	public DiscordPlayerLoaderWriter() {
		FantasyUnlimited.autowire(this);
	}
	
	@Override
	public DiscordPlayer load(Long key) throws Exception {
		return playerLogic.findByDiscordId(Long.toString(key));
	}

	@Override
	public void write(Long key, DiscordPlayer value) throws Exception {
		playerLogic.save(value);		
	}

	@Override
	public void delete(Long key) throws Exception {
		DiscordPlayer player = load(key);
		playerLogic.delete(player);
	}

}
