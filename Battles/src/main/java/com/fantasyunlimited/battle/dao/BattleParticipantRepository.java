package com.fantasyunlimited.battle.dao;

import com.fantasyunlimited.battle.entity.BattleParticipant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

@NoRepositoryBean
public interface BattleParticipantRepository<T extends BattleParticipant> extends CrudRepository<T, UUID> {
}
