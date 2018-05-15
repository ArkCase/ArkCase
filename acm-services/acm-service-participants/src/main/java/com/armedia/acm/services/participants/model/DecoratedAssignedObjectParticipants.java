package com.armedia.acm.services.participants.model;

/*-
 * #%L
 * ACM Service: Participants
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
 * -------- RequiredParameters: None
 *
 * ---- {@link List <@{@link AcmParticipant}>}
 * -------- RequiredParameters: (objectType and objectId) or (objectTypeIndex and objectIdIndex)
 * Examples:
 * -- @DecoratedAssignedObjectParticipants(objectType="CaseFile")
 * -- List<AcmParticipant> getParticipantsByCaseId(long caseId)
 *
 * --@DecoratedAssignedObjectParticipants(objectTypeIndex = 0, objectIdIndex = 1)
 * --public List<AcmParticipant> listParticipants( @PathVariable(value = "objectType") String
 * --objectType, @PathVariable(value = "objectId") Long objectId)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DecoratedAssignedObjectParticipants
{
    // the object type to be passed for hardcoded object type in case there is no object type argument passed in method
    // and the object type is known - optional - default from parameters
    String objectType() default "";

    // the object id to be passed for hardcoded object id in case there is no object id argument passed in method and
    // the id is known- optional - default from parameters
    int objectId() default -1;

    // the object type argument index in the method parameters - optional - default to
    // possition -1
    int objectTypeIndex() default -1;

    // the object Id argument index in the method parameters - optional - default to index -1
    int objectIdIndex() default -1;
}
