package com.armedia.acm.services.users.model.group;

import com.armedia.acm.services.users.service.group.LdapGroupNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LdapGroupNameValidator.class)
public @interface LdapGroupNameValidation
{
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
