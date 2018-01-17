package com.armedia.acm.services.users.model.ldap;

/**
 * Created by sharmilee.sivakumaran on 6/8/17.
 */

import com.armedia.acm.services.users.service.ldap.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface PasswordValidation
{
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
