package com.armedia.acm.web.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AsyncApplicationListener annotation. Marked {@link org.springframework.context.ApplicationListener} implementation
 * will have its onApplicationEvent() method executed asynchronously.
 * <p>
 * Created by Bojan Milenkoski on 15.1.2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AsyncApplicationListener
{
}
