package com.armedia.acm.websockets;

import com.armedia.acm.data.AcmObjectEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by nebojsha on 21.04.2016.
 */
@Component
public class ObjectUpdateNotifier
{
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private SimpMessagingTemplate template;

    private List<String> includeObjectTypes;
    private List<String> includeParentObjectTypes;
    private List<String> includeClassNames;
    private Properties objectChangeNotificationProperties;

    public void initBean()
    {
        String classNames = getObjectChangeNotificationProperties().getProperty("acm.object.changed.notification.filter.include.classNames");
        setIncludeClassNames(Arrays.asList(classNames.split(",")));

        String objectTypes = getObjectChangeNotificationProperties().getProperty("acm.object.changed.notification.filter.include.object_types");
        setIncludeObjectTypes(Arrays.asList(objectTypes.split(",")));

        String parentObjectTypes = getObjectChangeNotificationProperties().getProperty("acm.object.changed.notification.filter.include.parent_object_types");
        setIncludeParentObjectTypes(Arrays.asList(parentObjectTypes.split(",")));

    }

    public void setTemplate(SimpMessagingTemplate template)
    {
        this.template = template;
    }

    public void notifyChange(Message<AcmObjectEvent> message)
    {
        AcmObjectEvent event = message.getPayload();

        if (includeClassNames.contains(event.getClassName())
                || includeObjectTypes.contains(event.getObjectType())
                || includeParentObjectTypes.contains(event.getParentObjectType()))
        {
            log.debug("Sending a message. {}", message);
            template.convertAndSend("/topic/objects/changed", event);
            // following topics are not used/listened on client side
            // template.convertAndSend("/topic/objects/" + event.getObjectType() + "/" + event.getObjectId(), event);
            // template.convertAndSend("/topic/objects/changed/" + event.getUser(), event);
        } else
        {
            log.debug("Object is not eligible for notifying, didn't pass the filters. {}", message);
        }

    }

    public List<String> getIncludeObjectTypes()
    {
        return includeObjectTypes;
    }

    public void setIncludeObjectTypes(List<String> includeObjectTypes)
    {
        this.includeObjectTypes = includeObjectTypes;
    }

    public List<String> getIncludeParentObjectTypes()
    {
        return includeParentObjectTypes;
    }

    public void setIncludeParentObjectTypes(List<String> includeParentObjectTypes)
    {
        this.includeParentObjectTypes = includeParentObjectTypes;
    }

    public List<String> getIncludeClassNames()
    {
        return includeClassNames;
    }

    public void setIncludeClassNames(List<String> includeClassNames)
    {
        this.includeClassNames = includeClassNames;
    }

    public void setObjectChangeNotificationProperties(Properties objectChangeNotificationProperties)
    {
        this.objectChangeNotificationProperties = objectChangeNotificationProperties;
    }

    public Properties getObjectChangeNotificationProperties()
    {
        return objectChangeNotificationProperties;
    }

}
