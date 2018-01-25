package com.armedia.acm.plugins.objectassociation.service;

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
