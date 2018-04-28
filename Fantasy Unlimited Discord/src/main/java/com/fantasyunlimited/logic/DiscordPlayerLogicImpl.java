package com.fantasyunlimited.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyunlimited.dao.DiscordUserRepository;
import com.fantasyunlimited.dao.PlayerCharacterRepository;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.fantasyunlimited.entity.PlayerCharacter;
import com.google.common.collect.Lists;

@Component
@Scope("singleton")
public class DiscordPlayerLogicImpl implements DiscordPlayerLogic {

	@Autowired
	private DiscordUserRepository repository;
	@Autowired
	private PlayerCharacterRepository characterRepository;
	
	@Override
	@Transactional
	public DiscordPlayer save(DiscordPlayer entity) {
		return repository.save(entity);
	}

	@Override
	@Transactional
	public void delete(DiscordPlayer entity) {
		repository.delete(entity);
	}

	@Override
	public DiscordPlayer getById(Integer id) {
		return repository.findById(id).orElse(null);
	}
	
	@Override
	public DiscordPlayer findByDiscordId(String discordId) {
		return repository.findByDiscordId(discordId).orElse(null);
	}

	@Override
	public List<DiscordPlayer> getAll() {
		return Lists.newArrayList(repository.findAll());
	}

	@Override
	@Transactional
	public DiscordPlayer addCharacter(DiscordPlayer player, PlayerCharacter character) {
		player = getById(player.getId()); //load into transaction
		player.getCharacters().add(character);
		character.setPlayer(player);
		player.setCurrentCharacter(character);
		player = repository.save(player);
		return player;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PlayerCharacter> getCharactersForPlayer(DiscordPlayer player) {
		return Lists.newArrayList(characterRepository.findAllByPlayer(player));
	}

	@Override
	public boolean isNameAvailable(String name) {
		return characterRepository.findByNameIgnoreCase(name).isPresent() == false;
	}

	@Override
	@Transactional(readOnly = true)
	public PlayerCharacter getCharacterForPlayer(DiscordPlayer player, String name) {
		return characterRepository.findByPlayerAndNameIgnoreCase(player, name).orElse(null);
	}

	@Override
	@Transactional
	public DiscordPlayer selectActiveCharacter(DiscordPlayer player, PlayerCharacter character) {
		player = getById(player.getId()); //load into transaction
		player.setCurrentCharacter(character);
		player = repository.save(player);
		return player;
	}

	@Override
	@Transactional(readOnly = true)
	public PlayerCharacter getCharacter(String name) {
		return characterRepository.findByNameIgnoreCase(name).orElse(null);
	}
}
