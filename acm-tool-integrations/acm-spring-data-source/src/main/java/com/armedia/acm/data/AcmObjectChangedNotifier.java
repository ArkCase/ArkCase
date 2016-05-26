package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmParentObjectInfo;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.armedia.acm.data.AcmObjectEventConstants.ACTION_DELETE;
import static com.armedia.acm.data.AcmObjectEventConstants.ACTION_INSERT;
import static com.armedia.acm.data.AcmObjectEventConstants.ACTION_UPDATE;

/**
 * Created by nebojsha on 06.05.2016.
 */
public class AcmObjectChangedNotifier
{
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private MessageChannel objectEventChannel;
    private Set<String> includeObjectTypes = new HashSet<>();
    private Set<String> includeParentObjectTypes = new HashSet<>();
    private Set<String> includeClassNames = new HashSet<>();

    public void notifyChange(String action, Object object)
    {
        AcmObjectEvent objectChangedEvent = new AcmObjectEvent(action);
        updateAcmObjectInfo(objectChangedEvent, object);
        updateAcmEntityInfo(objectChangedEvent, object);
        updateAcmParentObjectInfo(objectChangedEvent, object);

        if (includeClassNames.contains(objectChangedEvent.getClassName())
                || includeObjectTypes.contains(objectChangedEvent.getObjectType())
                || includeParentObjectTypes.contains(objectChangedEvent.getParentObjectType()))
        {
            createAndSendMessage(objectChangedEvent);
        } else
        {
            log.debug("Object is not eligible for notifying, didn't pass the filters. {}", objectChangedEvent);
        }
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

    public void setIncludeObjectTypes(String includeObjectTypes)
    {
        this.includeObjectTypes = parseCSV(includeObjectTypes);
    }


    public void setIncludeParentObjectTypes(String includeParentObjectTypes)
    {
        this.includeParentObjectTypes = parseCSV(includeParentObjectTypes);
    }

    public void setIncludeClassNames(String includeClassNames)
    {
        this.includeClassNames = parseCSV(includeClassNames);
    }


    /**
     * parses comma separated values from properties
     *
     * @param includeObjectTypes
     * @return returns Set of parsed values, empty Set if nothing found
     */
    private Set<String> parseCSV(String includeObjectTypes)
    {
        Set<String> parsedCSV = new HashSet<>();
        if (includeObjectTypes == null)
        {
            return parsedCSV;
        }
        for (String s : includeObjectTypes.split(",[\\s]*"))
        {
            if (StringUtils.isNotEmpty(s.trim()))
            {
                parsedCSV.add(s);
            }
        }
        return parsedCSV;
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
