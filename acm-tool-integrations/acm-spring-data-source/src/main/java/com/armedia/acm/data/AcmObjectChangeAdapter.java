package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for object changes and sends them to the acmObjectNotifier
 * <p>
 * Created by nebojsha on 10.05.2016.
 */
public class AcmObjectChangeAdapter extends DescriptorEventAdapter
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AcmObjectChangedNotifier acmObjectChangedNotifier;

    @Override
    public void postDelete(DescriptorEvent event)
    {
        if (event.getSource() instanceof AcmObject)
        {
            AcmObject acmObject = (AcmObject) event.getSource();
            AcmEntity acmEntity = null;
            if (event.getSource() instanceof AcmEntity)
            {
                acmEntity = (AcmEntity) event.getSource();
            }
            acmObjectChangedNotifier.notifyChange(generateObjectChangedEvent(acmObject, "DELETE", acmEntity != null ? acmEntity.getModifier() : null));
        }
    }

    @Override
    public void postInsert(DescriptorEvent event)
    {
        if (event.getSource() instanceof AcmObject)
        {
            AcmObject acmObject = (AcmObject) event.getSource();
            AcmEntity acmEntity = null;
            if (event.getSource() instanceof AcmEntity)
            {
                acmEntity = (AcmEntity) event.getSource();
            }
            acmObjectChangedNotifier.notifyChange(generateObjectChangedEvent(acmObject, "INSERT", acmEntity != null ? acmEntity.getCreator() : null));

        }
    }

    @Override
    public void postUpdate(DescriptorEvent event)
    {
        if (event.getSource() instanceof AcmObject)
        {
            AcmObject acmObject = (AcmObject) event.getSource();
            AcmEntity acmEntity = null;
            if (event.getSource() instanceof AcmEntity)
            {
                acmEntity = (AcmEntity) event.getSource();
            }
            acmObjectChangedNotifier.notifyChange(generateObjectChangedEvent(acmObject, "UPDATE", acmEntity != null ? acmEntity.getModifier() : null));
        }
    }

    public void setAcmObjectChangedNotifier(AcmObjectChangedNotifier acmObjectChangedNotifier)
    {
        this.acmObjectChangedNotifier = acmObjectChangedNotifier;
    }

    /**
     * generates AcmObjectChangedEvent object from AcmObject and sets which action was performed to that object, and which user
     *
     * @param acmObject acmObject
     * @param action    action could be: insert, update, delete
     * @param userId    userId user which performed the action
     * @return AcmObjectChangedEvent instance
     */
    private AcmObjectChangedEvent generateObjectChangedEvent(AcmObject acmObject, String action, String userId)
    {
        AcmObjectChangedEvent acmObjectChangedEvent = new AcmObjectChangedEvent();
        acmObjectChangedEvent.setAction(action);
        acmObjectChangedEvent.setObjectId(acmObject.getId());
        acmObjectChangedEvent.setObjectType(acmObject.getObjectType());
        acmObjectChangedEvent.setUser(userId);
        return acmObjectChangedEvent;
    }
}
