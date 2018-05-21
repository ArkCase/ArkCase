package com.armedia.acm.plugins.objectassociation.service;

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

import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociationEvent;

import org.springframework.context.ApplicationListener;

public class ReferenceEventListener implements ApplicationListener<ObjectAssociationEvent>
{

    private ObjectAssociationEventPublisher objectAssociationEventPublisher;

    @Override
    public void onApplicationEvent(ObjectAssociationEvent objectAssociationEvent)
    {
        if (isObjectAssociationCreatedEvent(objectAssociationEvent))
        {
            ObjectAssociation objectAssociation = (ObjectAssociation) objectAssociationEvent.getSource();
            ObjectAssociation inverseObjectAssociation = objectAssociation.getInverseAssociation();

            if (checkExecution(objectAssociation))
            {
                publishReferenceEvent(objectAssociationEvent, objectAssociation);
            }

            if (checkExecution(inverseObjectAssociation))
            {
                publishReferenceEvent(objectAssociationEvent, inverseObjectAssociation);
            }
        }
    }

    private void publishReferenceEvent(ObjectAssociationEvent objectAssociationEvent, ObjectAssociation objectAssociation)
    {
        switch (objectAssociationEvent.getObjectAssociationState())
        {
        case NEW:
            getObjectAssociationEventPublisher().publishAddReferenceEvent(objectAssociation, objectAssociationEvent.getAuthentication(),
                    true);
            break;
        case UPDATE:
            getObjectAssociationEventPublisher().publishUpdateReferenceEvent(objectAssociation, objectAssociationEvent.getAuthentication(),
                    true);
            break;

        case DELETE:
            getObjectAssociationEventPublisher().publishDeleteReferenceEvent(objectAssociation, objectAssociationEvent.getAuthentication(),
                    true);
            break;
        }
    }

    private boolean isObjectAssociationCreatedEvent(ObjectAssociationEvent objectAssociationEvent)
    {
        return objectAssociationEvent != null && objectAssociationEvent.getEventType().equals("com.armedia.acm.objectassociation.created");
    }

    private boolean checkExecution(ObjectAssociation objectAssociation)
    {
        return objectAssociation != null && "REFERENCE".equals(objectAssociation.getAssociationType());
    }

    public ObjectAssociationEventPublisher getObjectAssociationEventPublisher()
    {
        return objectAssociationEventPublisher;
    }

    public void setObjectAssociationEventPublisher(ObjectAssociationEventPublisher objectAssociationEventPublisher)
    {
        this.objectAssociationEventPublisher = objectAssociationEventPublisher;
    }
}
