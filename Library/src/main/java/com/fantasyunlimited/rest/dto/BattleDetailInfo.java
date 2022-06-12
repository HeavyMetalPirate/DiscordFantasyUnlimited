package com.fantasyunlimited.rest.dto;

import java.util.List;

public record BattleDetailInfo(
        String id,
        boolean active,
        LocationItem location,
        BattlePlayerDetails playerDetails,
        List<BattleParticipantDetails> players,
        List<BattleParticipantDetails> hostiles,
        BattleLog battleLog,
        BattleResultSummary summary
) {}
