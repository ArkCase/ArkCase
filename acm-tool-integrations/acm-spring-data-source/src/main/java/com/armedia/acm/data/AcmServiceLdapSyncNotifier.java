package com.armedia.acm.data;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 4, 2018
 *
 */
public class AcmServiceLdapSyncNotifier implements ApplicationListener<AcmServiceLdapSyncEvent>
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private MessageChannel genericMessagesChannel;

    /*
     * (non-Javadoc)
     *
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
