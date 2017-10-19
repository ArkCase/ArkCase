package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.data.AcmProgressIndicator;
import com.armedia.acm.data.AcmProgressNotifierMessageBuilder;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 24, 2017
 *
 */
public class AcmRecreateOutlookFoldersProgressNotifierMessageBuilder implements AcmProgressNotifierMessageBuilder
{

    public static final String OBJECT_TYPE = "outlook_folder";

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.data.AcmProgressNotifierMessageBuilder#buildMessage(com.armedia.acm.data.AcmProgressIndicator)
     */
    @Override
    public Message<Map<String, Object>> buildMessage(AcmProgressIndicator progressIndicator)
    {
        AcmRecreateOutlookFolderProgressIndicator outlookFolderProgressIndicator = (AcmRecreateOutlookFolderProgressIndicator) progressIndicator;
        Map<String, Object> message = new HashMap<>();
        message.put("success", outlookFolderProgressIndicator.getProgress());
        message.put("failure", outlookFolderProgressIndicator.getProgressFailed());
        message.put("total", outlookFolderProgressIndicator.getTotal());
        message.put("user", outlookFolderProgressIndicator.getUser());
        message.put("eventType", "recreate_outlook_folders_progress");
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();
        return progressMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.armedia.acm.data.AcmProgressNotifierMessageBuilder#getObjectType()
     */
    @Override
    public String getObjectType()
    {
        return OBJECT_TYPE;
    }

}
