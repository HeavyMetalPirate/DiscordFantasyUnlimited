package com.fantasyunlimited.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fantasyunlimited.dao.TestingRepository;
import com.fantasyunlimited.entity.TestingEntity;

/**
 * Sample implementation of TestingLogic.
 * Is annotated as spring component with the name defined in TestingLogic.
 * 
 * Uses an autowired CrudRepository and the spring {@code @Transactional} annotation.
 * 
 * @author HeavyMetalPirate
 * @version 1.0.0
 *
 */

@Component
@Scope("prototype")
public class TestingLogicImpl implements TestingLogic {
	@Autowired TestingRepository testingRepository;
	
	@Transactional
	public void storeDummyEntityToDB() {
		TestingEntity entity = new TestingEntity();
		entity.setText("asdf" + ((int)(Math.random()*100)));
		testingRepository.save(entity);
	}
}
