package com.fantasyunlimited.logic;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyunlimited.dao.DiscordUserRepository;
import com.fantasyunlimited.dao.PlayerCharacterRepository;
import com.fantasyunlimited.discord.FantasyUnlimited;
import com.fantasyunlimited.discord.xml.CharacterClass;
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
		player = getById(player.getId()); // load into transaction
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
		player = getById(player.getId()); // load into transaction
		player.setCurrentCharacter(character);
		player = repository.save(player);
		return player;
	}

	@Override
	@Transactional(readOnly = true)
	public PlayerCharacter getCharacter(String name) {
		return characterRepository.findByNameIgnoreCase(name).orElse(null);
	}

	@Override
	@Transactional(rollbackFor = IllegalStateException.class)
	public boolean addExperience(PlayerCharacter character, int amount) {
		return addExperience(character.getId(), amount);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = IllegalStateException.class)
	public void addItemsToInventory(PlayerCharacter character, Pair<String, Integer>... itemAndAmount) {
		addItemsToInventory(character.getId(), itemAndAmount);
	}

	@Override
	@Transactional(rollbackFor = IllegalStateException.class)
	public boolean addExperience(Long characterId, int amount) {
		// load into session
		PlayerCharacter character = characterRepository.findById(characterId).orElse(null);
		if (character == null) {
			throw new IllegalStateException("Provided character not in database!");
		}
		if (character.getCurrentLevel() == 100) {
			return false;
		}
		character.setCurrentXp(character.getCurrentXp() + amount);

		boolean ret = false;
		long xpForNextLevel = FantasyUnlimited.getInstance().getNextLevelExperience(character.getCurrentLevel());
		if (character.getCurrentXp() >= xpForNextLevel & character.getCurrentLevel() < 100) {
			character.setCurrentLevel(character.getCurrentLevel() + 1);
			character.getAttributes().raiseUnspent(5);

			// do class attribute raise
			CharacterClass charClass = FantasyUnlimited.getInstance().getClassBag().getItem(character.getClassId());
			character.getAttributes().raiseEndurance(charClass.getAttributes().getEnduranceGrowth());
			character.getAttributes().raiseStrength(charClass.getAttributes().getStrengthGrowth());
			character.getAttributes().raiseDexterity(charClass.getAttributes().getDexterityGrowth());
			character.getAttributes().raiseWisdom(charClass.getAttributes().getWisdomGrowth());
			character.getAttributes().raiseIntelligence(charClass.getAttributes().getIntelligenceGrowth());
			character.getAttributes().raiseDefense(charClass.getAttributes().getDefenseGrowth());
			character.getAttributes().raiseLuck(charClass.getAttributes().getLuckGrowth());

			character.setCurrentHealth(character.getMaxHealth());

			ret = true;
		}

		characterRepository.save(character);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = IllegalStateException.class)
	public void addItemsToInventory(Long characterId, Pair<String, Integer>... itemAndAmount) {
		// load into session
		PlayerCharacter character = characterRepository.findById(characterId).orElse(null);
		if (character == null) {
			throw new IllegalStateException("Provided character not in database!");
		}
		for (Pair<String, Integer> item : itemAndAmount) {
			if (character.getInventory().containsKey(item.getLeft())) {
				int currentAmount = character.getInventory().get(item.getLeft());
				character.getInventory().put(item.getLeft(), currentAmount + item.getRight());
			} else {
				character.getInventory().put(item.getLeft(), item.getRight());
			}
		}
		characterRepository.save(character);
	}

	@Override
	@Transactional(rollbackFor = IllegalStateException.class)
	public void saveNewHealth(Long characterId, int currentHealth) {
		// load into session
		PlayerCharacter character = characterRepository.findById(characterId).orElse(null);
		if (character == null) {
			throw new IllegalStateException("Provided character not in database!");
		}
		character.setCurrentHealth(currentHealth);
		if(character.getCurrentHealth() > character.getMaxHealth()) {
			character.setCurrentHealth(character.getMaxHealth());
		}
		characterRepository.save(character);
	}

	@Override
	@Transactional(rollbackFor = IllegalStateException.class)
	public void saveNewHealth(PlayerCharacter character, int currentHealth) {
		saveNewHealth(character.getId(), currentHealth);
	}
}
