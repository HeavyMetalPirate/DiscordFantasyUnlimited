package com.fantasyunlimited.battle;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.fantasyunlimited.battle.dao")
public class BattleConfiguration {
}
