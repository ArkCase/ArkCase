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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

public class AcmProgressNotifier implements ApplicationListener<AcmProgressEvent>
{
    private Logger log = LogManager.getLogger(getClass());
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
        }
        else
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
