package com.armedia.acm.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

public class AcmProgressNotifier implements ApplicationListener<AcmProgressEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private MessageChannel genericMessagesChannel;
    private Map<String, AcmProgressNotifierMessageBuilder> messageBuilders;

    @Override
    public void onApplicationEvent(AcmProgressEvent acmProgressEvent)
    {
        log.debug("On Acm progress event: {}", acmProgressEvent.getClass());

        AcmProgressIndicator progressIndicator = acmProgressEvent.getAcmProgressIndicator();
        Message<Map<String, Object>> progressMessage;
        if (messageBuilders == null || !messageBuilders.containsKey(progressIndicator.getObjectType()))
        {
            progressMessage = AcmProgressNotifierMessageBuilder.defaultBuildMessage(progressIndicator);
        } else
        {
            progressMessage = messageBuilders.get(progressIndicator.getObjectType()).buildMessage(progressIndicator);
        }
        log.debug("Send progress for user: {}", progressIndicator.getUser());
        sendProgress(progressMessage);
    }

    private void sendProgress(Message<Map<String, Object>> progressMessage)
    {
        genericMessagesChannel.send(progressMessage);
    }

    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }

    /**
     * @param messageBuilders
     *            the messageBuilders to set
     */
    public void setMessageBuilders(Map<String, AcmProgressNotifierMessageBuilder> messageBuilders)
    {
        this.messageBuilders = messageBuilders;
    }

}
