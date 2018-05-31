package com.armedia.acm.plugins.objectassociation.model;

/*-
 * #%L
 * ACM Default Plugin: Object Associations
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

import com.armedia.acm.core.model.AcmEvent;

import org.springframework.security.core.Authentication;

import java.util.Date;

public class ObjectAssociationEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.objectassociation.created";
    private ObjectAssociationState objectAssociationState;
    private Authentication authentication;

    public ObjectAssociationEvent(ObjectAssociation source)
    {
        super(source);
        setObjectId(source.getParentId());
        setObjectType(source.getParentType());
        setEventDate(new Date());
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

    public ObjectAssociationState getObjectAssociationState()
    {
        return objectAssociationState;
    }

    public void setObjectAssociationState(ObjectAssociationState objectAssociationState)
    {
        this.objectAssociationState = objectAssociationState;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public enum ObjectAssociationState
    {
        NEW, UPDATE, DELETE
    }
}
