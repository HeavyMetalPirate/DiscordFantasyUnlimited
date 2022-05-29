package com.fantasyunlimited.battle.dao;

import com.fantasyunlimited.battle.entity.BattleInformation;
import com.fantasyunlimited.battle.entity.BattlePlayer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface BattleRepository extends CrudRepository<BattleInformation, UUID> {

    @Query("select b from BattleInformation b "
            + " left join BattlePlayer bp on b.battleId = bp.battleInformation.battleId "
            + " where bp.characterId = ?1 "
            + " and b.isActive = true")
    public Optional<BattleInformation> findBattleForCharacter(Long characterId);

}
