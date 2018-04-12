package com.fantasyunlimited.utils;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fantasyunlimited.annotation.SpringBean;
import com.fantasyunlimited.logic.TestingLogic;

/**
 * <p>
 * Class that connects the world of spring beans with the world of CDI managed
 * beans.
 * </p>
 * <p>
 * Uses a producer function to fetch a spring bean from the Web Application
 * Context and returns the fetched bean to the CDI container manager. The CDI
 * container manager then is able to use the produced bean to inject it at a
 * qualified position.
 * </p>
 * 
 * @author HeavyMetalPirate
 * @version 1.0.0
 * @see #produceSpringBeanAsCDIBean(InjectionPoint) internal producer method
 * @see SpringBean SpringBean custom annotation
 * @see Produces Produces CDI annotation 
 * @see Inject Inject CDI annotation
 * @see InjectionPoint InjectionPoint reference
 *
 */

@Dependent
public class SpringCDIConnector {
	private static final Logger localLogger = Logger.getLogger(SpringCDIConnector.class);
	/**
	 * Pre-defined beans in JEE
	 * {@linkplain https://docs.oracle.com/javaee/7/tutorial/cdi-adv004.htm}
	 */
	private @Inject ServletContext servletContext;

	@Produces
	@SpringBean
	public TestingLogic produceTestingLogic() {
		return getRequestedSpringBean(TestingLogic.class);
	}
		
	private <T> T getRequestedSpringBean(Class<T> clazz) {
		try {
			return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext).getBean(clazz);
		} catch (Exception e) {
			localLogger.error("Error fetching bean from context.", e);
			return null;
		}
	}
}
