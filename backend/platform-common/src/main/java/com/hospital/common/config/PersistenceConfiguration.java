package com.hospital.common.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.hospital")
@EnableJpaRepositories("com.hospital")
public class PersistenceConfiguration {}
