package com.armedia.acm.services.participants.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Annotation:
 * --name: @{@link DecoratedAssignedObjectParticipants}
 * --Handled Responses:
 * ---- {@link AcmAssignedObject}
 * -------- RequiredParameters: (objectType and objectId) or (objectTypeIndex and objectIdIndex)
 *
 * ---- {@link List <@{@link AcmParticipant}>}
 * -------- RequiredParameters: None
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DecoratedAssignedObjectParticipants
{
    // the object type to be passed for hardcoded object type - optional - default from parameters
    String objectType() default "";

    // the object id to be passed for hardcoded object id - optional - default from parameters
    int objectId() default -1;

    // the object type index to be passed and to get the object type from GET parameters - optional - default to
    // possition 0
    int objectTypeIndex() default 0;

    // the object Id index to be passed and to get the object id from GET parameters - optional - default to index 1
    int objectIdIndex() default 1;
}
