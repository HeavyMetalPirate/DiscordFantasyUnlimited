package com.fantasyunlimited.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.fantasyunlimited.entity.DiscordPlayer;

/**
 * 
 * @author HeavyMetalPirate
 * @version 1.0.0
 *
 */
public interface DiscordUserRepository extends CrudRepository<DiscordPlayer, Integer>{
	public Optional<DiscordPlayer> findByDiscordId(String discordId);
}
