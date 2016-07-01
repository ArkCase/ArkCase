/**
 * 
 */
package com.armedia.acm.services.notification.service;

import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public interface NotificationSender
{

    /**
     * Sends the notification to user's email. If successful, sets the notification state to
     * {@link NotificationConstants#STATE_SENT}, otherwise it sets it to {@link NotificationConstants#STATE_NOT_SENT}
     * 
     * @param notification
     *            the notification to send
     * @return the notification with state set
     */
    public Notification send(Notification notification);

    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception;

    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception;

}
