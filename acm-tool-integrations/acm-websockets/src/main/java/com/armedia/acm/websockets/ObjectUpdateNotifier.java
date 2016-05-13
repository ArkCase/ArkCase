package com.armedia.acm.websockets;

import com.armedia.acm.data.AcmObjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by nebojsha on 21.04.2016.
 */
@Component
public class ObjectUpdateNotifier
{
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private SimpMessagingTemplate template;

    public void setTemplate(SimpMessagingTemplate template)
    {
        this.template = template;
    }

    public void notifyChange(Message<AcmObjectEvent> message)
    {
        AcmObjectEvent event = message.getPayload();
        template.convertAndSend("/topic/objects/changed/" + event.getObjectType() + "/" + event.getObjectId(), event);
    }
}
