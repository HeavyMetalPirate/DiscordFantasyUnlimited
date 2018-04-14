package com.fantasyunlimited.logic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyunlimited.dao.DiscordUserRepository;
import com.fantasyunlimited.entity.DiscordPlayer;
import com.google.common.collect.Lists;

@Component
@Scope("singleton")
public class DiscordPlayerLogicImpl implements DiscordPlayerLogic {

	@Autowired
	private DiscordUserRepository repository;
	
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
}
