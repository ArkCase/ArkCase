package com.armedia.acm.plugins.person.model;

/*-
 * #%L
 * ACM Default Plugin: Person
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

public class OrganizationAssociationDeletedEvent extends OrganizationAssociationPersistenceEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.organizationAssociation.deleted";

    public OrganizationAssociationDeletedEvent(OrganizationAssociation source, String parentType, Long parentId, String ipAddress)
    {
        super(source);
        setParentObjectId(parentId);
        setParentObjectType(parentType);
        setEventDescription("Organization deleted (" + source.getOrganization().getOrganizationValue() + ")");
        setIpAddress(ipAddress);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
