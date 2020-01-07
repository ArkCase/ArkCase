/**
 * 
 */
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    private Logger log = LogManager.getLogger(getClass());

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel)
    {
        MessageHeaders messageHeaders = message.getHeaders();
        String simpSessionId = (String) messageHeaders.get("simpSessionId");
        SimpMessageType simpSessionType = (SimpMessageType) messageHeaders.get("simpMessageType");

        if (simpSessionType == SimpMessageType.CONNECT)
        {
            Principal principal = (Principal) messageHeaders.get("simpUser");
            log.trace("Web Socket Session {} CONNECT, user {}", simpSessionId, principal.getName());
        }
        else if (simpSessionType == SimpMessageType.DISCONNECT)
        {
            log.trace("Web Socket Session {} DISCONNECT", simpSessionId);
        }
        else if (simpSessionType == SimpMessageType.SUBSCRIBE)
        {
            Principal principal = (Principal) messageHeaders.get("simpUser");
            log.trace("Web Socket Session {} SUBSCRIBE, user {}", simpSessionId, principal.getName());
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent)
    {
    }

}
