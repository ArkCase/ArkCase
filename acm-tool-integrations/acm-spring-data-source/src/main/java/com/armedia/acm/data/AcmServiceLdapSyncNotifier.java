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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 4, 2018
 *
 */
public class AcmServiceLdapSyncNotifier implements ApplicationListener<AcmServiceLdapSyncEvent>
{

    private Logger log = LogManager.getLogger(getClass());

    private MessageChannel genericMessagesChannel;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AcmServiceLdapSyncEvent event)
    {
        log.debug("On [{}] event, service: [{}].", event.getClass(), event.getSyncResult().getService());

        AcmServiceLdapSyncResult syncResult = event.getSyncResult();
        Map<String, Object> syncMessage = new HashMap<>();
        syncMessage.put("service", syncResult.getService());
        syncMessage.put("user", syncResult.getUser());
        syncMessage.put("result", syncResult.isResult());
        syncMessage.put("message", syncResult.getMessage());
        syncMessage.put("eventType", "sync-progress");

        log.debug("Send progress for user: {}", syncResult.getUser());

        genericMessagesChannel.send(MessageBuilder.withPayload(syncMessage).build());
    }

    /**
     * @param genericMessagesChannel
     *            the genericMessagesChannel to set
     */
    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }

}
