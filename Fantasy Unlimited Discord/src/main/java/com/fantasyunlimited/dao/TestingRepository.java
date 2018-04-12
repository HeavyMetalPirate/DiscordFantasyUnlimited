package com.fantasyunlimited.dao;

import org.springframework.data.repository.CrudRepository;

import com.fantasyunlimited.entity.TestingEntity;

/**
 * Simple CrudRepository extension for usage with {@code TestingEntity}.
 * Defines no further functionality.
 * 
 * @author HeavyMetalPirate
 * @version 1.0.0
 *
 */
public interface TestingRepository extends CrudRepository<TestingEntity, Long>{

}
