package com.fantasyunlimited.utils;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.log4j.Logger;

/**
 * <p>
 * Simple class that contains generic producer methods to make CDI aware of
 * unmanaged objects for injection.
 * </p>
 * 
 * @author HeavyMetalPirate
 * @version 1.0.0
 *
 */
@Dependent
public class Resources {
	private Logger localLogger = Logger.getLogger(Resources.class);

}
