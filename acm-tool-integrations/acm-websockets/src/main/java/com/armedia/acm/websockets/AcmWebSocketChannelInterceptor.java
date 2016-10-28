/**
 * 
 */
package com.armedia.acm.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

import java.security.Principal;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmWebSocketChannelInterceptor extends ChannelInterceptorAdapter
{

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel)
    {
        MessageHeaders messageHeaders = message.getHeaders();
        String simpSessionId = (String) messageHeaders.get("simpSessionId");
        SimpMessageType simpSessionType = (SimpMessageType) messageHeaders.get("simpMessageType");

        if (simpSessionType == SimpMessageType.CONNECT)
        {
            Principal principal = (Principal) messageHeaders.get("simpUser");
            log.debug("Web Socket Session {} CONNECT, user {}", simpSessionId, principal.getName());
        } else if (simpSessionType == SimpMessageType.DISCONNECT)
        {
            log.debug("Web Socket Session {} DISCONNECT", simpSessionId);
        } else if (simpSessionType == SimpMessageType.SUBSCRIBE)
        {
            Principal principal = (Principal) messageHeaders.get("simpUser");
            log.debug("Web Socket Session {} SUBSCRIBE, user {}", simpSessionId, principal.getName());
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent)
    {
    }

}