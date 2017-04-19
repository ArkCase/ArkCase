package com.armedia.acm.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;


public class AcmProgressNotifier implements ApplicationListener<AcmProgressEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private MessageChannel genericMessagesChannel;

    @Override
    public void onApplicationEvent(AcmProgressEvent acmProgressEvent)
    {
        log.debug("On Acm progress event: {}", acmProgressEvent.getClass());
        AcmProgressIndicator progressIndicator = acmProgressEvent.getAcmProgressIndicator();
        sendProgress(progressIndicator.getProgress(), progressIndicator.getTotal(), progressIndicator.getUser());
    }

    private void sendProgress(int current, int total, String user)
    {
        log.debug("Send progress for user: {}", user);
        Map<String, Object> message = new HashMap<>();
        message.put("current", current);
        message.put("total", total);
        message.put("user", user);
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();
        genericMessagesChannel.send(progressMessage);
    }

    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }
}
