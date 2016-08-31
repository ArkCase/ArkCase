package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Date;

import static com.armedia.acm.data.AcmObjectEventConstants.*;

/**
 * Created by nebojsha on 06.05.2016.
 */
public class AcmObjectChangedNotifier implements ApplicationListener<AcmDatabaseChangesEvent>
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private MessageChannel objectEventChannel;

    @Override
    public void onApplicationEvent(AcmDatabaseChangesEvent acmDatabaseChangesEvent)
    {
        AcmObjectChangelist changes = acmDatabaseChangesEvent.getObjectChangelist();

        changes.getAddedObjects().stream().forEach(o -> notifyChange(AcmObjectEventConstants.ACTION_INSERT, o));
        changes.getDeletedObjects().stream().forEach(o -> notifyChange(AcmObjectEventConstants.ACTION_DELETE, o));
        changes.getUpdatedObjects().stream().forEach(o -> notifyChange(AcmObjectEventConstants.ACTION_UPDATE, o));
    }

    public void notifyChange(String action, Object object)
    {
        if (!(object instanceof AcmObject))
        {
            //not an instance of AcmObject, nothing to do
            return;
        }
        
        AcmObjectEvent objectChangedEvent = new AcmObjectEvent(action);
        updateAcmObjectInfo(objectChangedEvent, object);
        updateAcmEntityInfo(objectChangedEvent, object);
        updateAcmParentObjectInfo(objectChangedEvent, object);

        createAndSendMessage(objectChangedEvent);
    }

    private void createAndSendMessage(AcmObjectEvent acmObject)
    {
        Message<AcmObjectEvent> insertMessage = MessageBuilder.withPayload(acmObject).build();
        objectEventChannel.send(insertMessage);
    }

    public void setObjectEventChannel(MessageChannel ftpChannel)
    {
        this.objectEventChannel = ftpChannel;
    }

    /**
     * update objectChanged with acm object info
     *
     * @param objectChangedEvent instance of objectChangedEvent
     * @param object             Object
     */
    private void updateAcmObjectInfo(AcmObjectEvent objectChangedEvent, Object object)
    {

        AcmObject acmObject = (AcmObject) object;
        objectChangedEvent.setObjectId(acmObject.getId());
        objectChangedEvent.setObjectType(acmObject.getObjectType());
        objectChangedEvent.setClassName(object.getClass().getName());
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
            //not an instance of AcmEntity, nothing to do
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
            //not an instance of AcmParentObjectInfo, nothing to do
            return;
        }
        AcmParentObjectInfo parentObjectInfo = (AcmParentObjectInfo) object;

        objectChangedEvent.setParentObjectId(parentObjectInfo.getParentObjectId());
        objectChangedEvent.setParentObjectType(parentObjectInfo.getParentObjectType());
    }

}
