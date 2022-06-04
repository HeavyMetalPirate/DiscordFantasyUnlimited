package com.fantasyunlimited.battle.service;

import com.fantasyunlimited.battle.dao.BattleNPCRepository;
import com.fantasyunlimited.battle.dao.BattlePlayerRepository;
import com.fantasyunlimited.battle.entity.BattleNPC;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BattleParticipantCrudService {
    @Autowired
    private BattlePlayerRepository playerRepository;
    @Autowired
    private BattleNPCRepository npcRepository;

    @Transactional(readOnly = true)
    public BattlePlayer findBattlePlayer(String id) {
        UUID uuid = UUID.fromString(id);
        return playerRepository.findById(uuid).orElse(null);
    }
    @Transactional(readOnly = true)
    public BattleNPC findBattleNPC(String id) {
        UUID uuid = UUID.fromString(id);
        return npcRepository.findById(uuid).orElse(null);
    }
}
