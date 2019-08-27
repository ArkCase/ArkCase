package com.armedia.acm.configuration.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation at the field or method/constructor parameter level
 * that indicates a default value expression for the affected argument.
 *
 * <p>
 * Used for injection of a property key from configuration
 *
 * <p>
 * A common use case is to assign default list field values using
 * "systemProperties.myProp" style expressions.
 *
 * <p>
 * Note that actual processing of the {@code @ListValue} annotation is performed
 * by the ConfigurationContainer and PropertyNavigatior.
 *
 * @author Mario Gjurcheski
 * @see ListValueAspect
 * @see com.armedia.acm.configuration.core.propertysource.PropertyNavigator
 * @see com.armedia.acm.configuration.core.ConfigurationContainer
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue
{

    /**
     * The actual value expression: e.g. "propertyKey".
     */
    String value();

}
