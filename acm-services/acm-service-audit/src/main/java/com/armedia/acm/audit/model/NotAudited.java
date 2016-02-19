package com.armedia.acm.audit.model;

/**
 * NotAudited annotation. Changes in marked {@link javax.persistence.Entity} types will not be audited.
 * <p>
 * Created by Bojan Milenkoski on 27.1.2016.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NotAudited
{
}
