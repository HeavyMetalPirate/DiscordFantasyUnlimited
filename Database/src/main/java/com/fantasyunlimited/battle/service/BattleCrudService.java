package com.fantasyunlimited.battle.service;

import com.fantasyunlimited.battle.dao.BattleRepository;
import com.fantasyunlimited.battle.entity.BattleInformation;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BattleCrudService {
    @Autowired
    private BattleRepository repository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BattleInformation saveBattle(BattleInformation battle) {
        return repository.save(battle);
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public BattleInformation findBattle(UUID id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public boolean characterInActiveBattle(PlayerCharacter character) {
        return repository.findBattleForCharacter(character.getId()).isPresent();
    }

    @Transactional(readOnly = true, isolation = Isolation.SERIALIZABLE)
    public BattleInformation getCurrentCharacterBattle(PlayerCharacter character) {
        return repository.findBattleForCharacter(character.getId()).orElse(null);
    }
}
