package com.fantasyunlimited.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.fantasyunlimited.data.dao")
@EnableJpaAuditing
public class DataConfiguration {
}
