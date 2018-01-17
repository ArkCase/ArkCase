package com.armedia.acm.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Created by nebojsha on 21.04.2016.
 */
@Controller
public class MessageReceiver
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @MessageMapping("/print-message")
    public void messagePrinter(Message<Object> message) throws Exception
    {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        Principal userPrincipal = headerAccessor.getUser();

        log.info("Received Message from: {}, with content: {}", userPrincipal.getName(), message.getPayload());
    }
}
