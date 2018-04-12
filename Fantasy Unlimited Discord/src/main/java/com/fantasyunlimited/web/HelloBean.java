package com.fantasyunlimited.web;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.fantasyunlimited.annotation.SpringBean;
import com.fantasyunlimited.logic.TestingLogic;

/**
 * Simple CDI managed bean to test Spring CDI connectivity
 * @author HeavyMetalPirate
 *
 */

@ApplicationScoped
@Named
public class HelloBean {
	Logger log;
	
	//Declare Testing Logic
	@Inject
	@SpringBean
	private TestingLogic testingLogic;
		
	@PostConstruct
	public void init() {
		log.debug("HelloBean constructed.");
		log.debug("Logger instance: " + log.toString());
		log.debug("Testing Logic instance:" + testingLogic == null? "null" : testingLogic.toString());
	}
	
	public String sayHello() {
		log.debug("Testing method call by XHTML page.");
		return "Hello W�rld, says the bean.";
	}
	
	public void testLogic() {
		log.debug("Testing Logic spring bean.");
		testingLogic.storeDummyEntityToDB();
	}
}
