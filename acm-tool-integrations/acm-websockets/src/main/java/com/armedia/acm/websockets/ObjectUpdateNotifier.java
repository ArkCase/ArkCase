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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Created by nebojsha on 21.04.2016.
 */
public class ObjectUpdateNotifier
{
    private Logger log = LogManager.getLogger(getClass());

    @Autowired
    private SimpMessagingTemplate template;

    private ObjectNotificationsConfig objectNotificationsConfig;

    public void setTemplate(SimpMessagingTemplate template)
    {
        this.template = template;
    }

    public void notifyChange(Message<AcmObjectEvent> message)
    {
        AcmObjectEvent event = message.getPayload();

        if (objectNotificationsConfig.getIncludedClassNames().contains(event.getClassName())
                || objectNotificationsConfig.getIncludedObjectTypes().contains(event.getObjectType())
                || objectNotificationsConfig.getIncludedParentObjectTypes().contains(event.getParentObjectType()))
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

    public SimpMessagingTemplate getTemplate()
    {
        return template;
    }

    public ObjectNotificationsConfig getObjectNotificationsConfig()
    {
        return objectNotificationsConfig;
    }

    public void setObjectNotificationsConfig(ObjectNotificationsConfig objectNotificationsConfig)
    {
        this.objectNotificationsConfig = objectNotificationsConfig;
    }
}
