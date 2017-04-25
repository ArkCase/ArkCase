package com.armedia.acm.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Simple push notifier for ad-hoc messages
 */
@Component
public class SimplePushNotifier
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpMessagingTemplate template;

    public void receiveMessage(Message<Map<String, Object>> message)
    {
        Map<String, Object> payload = message.getPayload();
        if (payload.containsKey("user"))
        {
            log.debug("Sending ad-hoc message: {}", message);
            template.convertAndSend("/topic/generic/" + payload.get("user"), payload);
        }
    }
}
