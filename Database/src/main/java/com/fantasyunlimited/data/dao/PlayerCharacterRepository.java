package com.fantasyunlimited.data.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.fantasyunlimited.data.entity.PlayerCharacter;

public interface PlayerCharacterRepository extends CrudRepository<PlayerCharacter, Long>{
	public Optional<PlayerCharacter> findByNameIgnoreCase(String name);
}
