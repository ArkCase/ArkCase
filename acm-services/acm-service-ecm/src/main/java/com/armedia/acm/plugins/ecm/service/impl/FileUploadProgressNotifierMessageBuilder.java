package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Ecm file service
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

import com.armedia.acm.data.AcmProgressIndicator;
import com.armedia.acm.data.AcmProgressNotifierMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

public class FileUploadProgressNotifierMessageBuilder implements AcmProgressNotifierMessageBuilder
{

    public static final String OBJECT_TYPE = "FILE";

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.data.FileUploadProgressNotifierMessageBuilder#buildMessage(com.armedia.acm.data.
     * AcmProgressIndicator)
     */
    @Override
    public Message<Map<String, Object>> buildMessage(AcmProgressIndicator progressIndicator)
    {
        FileUploadProgressIndicator fileUploadProgressIndicator = (FileUploadProgressIndicator) progressIndicator;
        Map<String, Object> message = new HashMap<>();
        message.put("success", fileUploadProgressIndicator.getProgress());
        message.put("failure", fileUploadProgressIndicator.getProgressFailed());
        message.put("total", fileUploadProgressIndicator.getTotal());
        message.put("user", fileUploadProgressIndicator.getUser());
        message.put("eventType", "file_upload_progress");
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();
        return progressMessage;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.FileUploadProgressNotifierMessageBuilder#getObjectType()
     */
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
