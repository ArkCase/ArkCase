package com.armedia.acm.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Marker annotation that can be used to define that a property can't be changed in runtime,
 * thus the property should not be included in the serialized object stored in runtime configuration file.
 * </p>
 *
 * <p>
 * Annotated properties are only checked if the configuration holder class implements <code>{@link DynamicApplicationConfig}</code>
 * </p>
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnmodifiableConfigProperty
{
}
