package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.armedia.acm.data.AcmObjectEventConstants.*;

/**
 * Listens for object changes and sends them to the acmObjectNotifier
 * <p>
 * Created by nebojsha on 10.05.2016.
 */
public class AcmObjectChangedAdapter extends DescriptorEventAdapter
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private AcmObjectChangedNotifier acmObjectChangedNotifier;

    @Override
    public void postDelete(DescriptorEvent event)
    {
        updateObjectInfoAndSend(ACTION_DELETE, event.getSource());
    }

    @Override
    public void postInsert(DescriptorEvent event)
    {
        updateObjectInfoAndSend(ACTION_INSERT, event.getSource());
    }

    @Override
    public void postUpdate(DescriptorEvent event)
    {
        updateObjectInfoAndSend(ACTION_UPDATE, event.getSource());
    }

    public void setAcmObjectChangedNotifier(AcmObjectChangedNotifier acmObjectChangedNotifier)
    {
        this.acmObjectChangedNotifier = acmObjectChangedNotifier;
    }

    /**
     * fill data from the original object to the event and send to the notifier
     *
     * @param action what action was performed
     * @param object actual object instance
     */
    private void updateObjectInfoAndSend(String action, Object object)
    {
        AcmObjectEvent objectChangedEvent = new AcmObjectEvent(action);
        updateAcmObjectInfo(objectChangedEvent, object);
        updateAcmEntityInfo(objectChangedEvent, object);
        updateAcmParentObjectInfo(objectChangedEvent, object);

        //send event to the notifier
        acmObjectChangedNotifier.notifyChange(objectChangedEvent);
    }

    /**
     * update objectChanged with acm object info
     *
     * @param objectChangedEvent instance of objectChangedEvent
     * @param object             Object
     */
    private void updateAcmObjectInfo(AcmObjectEvent objectChangedEvent, Object object)
    {
        if (!(object instanceof AcmObject))
        {
            //not an instance of AcmObject, nothing to do
            return;
        }
        AcmObject acmObject = (AcmObject) object;
        objectChangedEvent.setObjectId(acmObject.getId());
        objectChangedEvent.setObjectType(acmObject.getObjectType());
    }

    /**
     * update objectChanged with acm entity info
     *
     * @param objectChangedEvent instance of objectChangedEvent
     * @param object             Object
     */
    private void updateAcmEntityInfo(AcmObjectEvent objectChangedEvent, Object object)
    {
        if (!(object instanceof AcmEntity))
        {
            //not an instance of AcmObject, nothing to do
            return;
        }
        AcmEntity acmEntity = (AcmEntity) object;
        Date date = null;
        String userId = null;
        switch (objectChangedEvent.getAction())
        {
            case ACTION_UPDATE:
                date = acmEntity.getModified();
                userId = acmEntity.getModifier();
                break;
            case ACTION_INSERT:
                date = acmEntity.getCreated();
                userId = acmEntity.getCreator();
                break;
            case ACTION_DELETE:
                date = acmEntity.getModified();
                userId = acmEntity.getModifier();
                break;
            default:
                log.warn("ACTION must be provided before AcmEntity info is chosen.");
        }

        objectChangedEvent.setDate(date);
        objectChangedEvent.setUser(userId);
    }

    /**
     * update objectChanged with parent info
     *
     * @param objectChangedEvent instance of objectChangedEvent
     * @param object             Object
     */
    private void updateAcmParentObjectInfo(AcmObjectEvent objectChangedEvent, Object object)
    {
        if (!(object instanceof AcmParentObjectInfo))
        {
            //not an instance of AcmObject, nothing to do
            return;
        }
        AcmParentObjectInfo parentObjectInfo = (AcmParentObjectInfo) object;

        objectChangedEvent.setParentObjectId(parentObjectInfo.getParentObjectId());
        objectChangedEvent.setParentObjectType(parentObjectInfo.getParentObjectType());
    }
}
