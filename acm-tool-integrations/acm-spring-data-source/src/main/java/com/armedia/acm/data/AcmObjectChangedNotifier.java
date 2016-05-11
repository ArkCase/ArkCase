package com.armedia.acm.data;

import com.armedia.acm.core.AcmObject;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Created by nebojsha on 06.05.2016.
 */
public class AcmObjectChangedNotifier
{
    private MessageChannel objectEventChannel;

    public void notifyChange(AcmObjectChangedEvent acmObject)
    {
        Message<AcmObjectChangedEvent> insertMessage = MessageBuilder.withPayload(acmObject).build();
        objectEventChannel.send(insertMessage);
    }

    public void setObjectEventChannel(MessageChannel ftpChannel)
    {
        this.objectEventChannel = ftpChannel;
    }
}
