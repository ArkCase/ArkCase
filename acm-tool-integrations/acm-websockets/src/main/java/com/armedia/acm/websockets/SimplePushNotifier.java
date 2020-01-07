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
    private final Logger log = LogManager.getLogger(getClass());

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
