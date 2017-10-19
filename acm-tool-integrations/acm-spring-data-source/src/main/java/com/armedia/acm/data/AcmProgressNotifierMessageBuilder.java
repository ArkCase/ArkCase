package com.armedia.acm.data;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 22, 2017
 *
 */
public interface AcmProgressNotifierMessageBuilder
{

    public static Message<Map<String, Object>> defaultBuildMessage(AcmProgressIndicator progressIndicator)
    {
        Map<String, Object> message = new HashMap<>();
        message.put("current", progressIndicator.getProgress());
        message.put("total", progressIndicator.getTotal());
        message.put("user", progressIndicator.getUser());
        message.put("eventType", "live_progress");
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();
        return progressMessage;
    }

    Message<Map<String, Object>> buildMessage(AcmProgressIndicator progressIndicator);

    String getObjectType();

}
