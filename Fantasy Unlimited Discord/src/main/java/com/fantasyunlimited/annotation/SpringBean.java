package com.fantasyunlimited.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <p>
 * Annotation to mark an injection point as qualified for a spring bean.
 * </p>
 * <p>
 * The expected type of a spring bean must be provided as name parameter. It
 * must use the same value as provided within the {@code @Component}
 * annotation of spring.
 * </p>
 * <p>
 * If no name has been provided, the default value will be the
 * {@code String "null"}. This will most certainly result in an exception being
 * thrown by the producer function.
 * </p>
 * 
 * @author HeavyMetalPirate
 * @version 1.0.0
 *
 */

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringBean {
	@Nonbinding
	String name() default "null";
}
