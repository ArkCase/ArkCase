package com.armedia.acm.websockets;

/*-
 * #%L
 * Tool Integrations: ArkCase Web Sockets
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
        String classNames = getObjectChangeNotificationProperties()
                .getProperty("acm.object.changed.notification.filter.include.classNames");
        setIncludeClassNames(Arrays.asList(classNames.split(",")));

        String objectTypes = getObjectChangeNotificationProperties()
                .getProperty("acm.object.changed.notification.filter.include.object_types");
        setIncludeObjectTypes(Arrays.asList(objectTypes.split(",")));

        String parentObjectTypes = getObjectChangeNotificationProperties()
                .getProperty("acm.object.changed.notification.filter.include.parent_object_types");
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
        }
        else
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

    public Properties getObjectChangeNotificationProperties()
    {
        return objectChangeNotificationProperties;
    }

    public void setObjectChangeNotificationProperties(Properties objectChangeNotificationProperties)
    {
        this.objectChangeNotificationProperties = objectChangeNotificationProperties;
    }

}
