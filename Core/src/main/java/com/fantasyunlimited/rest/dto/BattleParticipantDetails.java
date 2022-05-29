package com.fantasyunlimited.rest.dto;

import java.util.List;

public record BattleParticipantDetails(String id, PlayerCharacterItem details, List<BattleParticipantStatus> statusEffects) {}
