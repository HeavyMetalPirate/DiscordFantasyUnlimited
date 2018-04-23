package com.fantasyunlimited.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;

public interface PlayerCharacterRepository extends CrudRepository<PlayerCharacter, Long>{
	public Optional<PlayerCharacter> findByNameIgnoreCase(String name);
	public Iterable<PlayerCharacter> findAllByPlayer(DiscordPlayer player);
}
