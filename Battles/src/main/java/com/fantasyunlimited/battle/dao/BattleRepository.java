package com.fantasyunlimited.battle.dao;

import com.fantasyunlimited.battle.entity.BattleInformation;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BattleRepository extends CrudRepository<BattleInformation, UUID> {}
