package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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
