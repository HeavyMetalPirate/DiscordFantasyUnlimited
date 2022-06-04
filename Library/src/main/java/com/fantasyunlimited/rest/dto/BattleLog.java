package com.fantasyunlimited.rest.dto;

import java.util.List;
import java.util.Map;

public record BattleLog(Map<Integer, List<BattleLogItem>> rounds) {
}
