package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.model.MessageBodyFactory;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import freemarker.template.TemplateException;

/**
 * @author riste.tutureski
 */
public abstract class NotificationSender
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    protected AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    protected AuthenticationTokenService authenticationTokenService;
    protected AuthenticationTokenDao authenticationTokenDao;
    protected EcmFileService ecmFileService;
    protected NotificationUtils notificationUtils;
    protected String notificationTemplate;
    private AcmEmailSenderService emailSenderService;
    private UserDao userDao;
    private AcmDataService dataService;
    private TemplatingEngine templatingEngine;
    private Map<String, String> notificationTemplates = new HashMap<>();
    private EmailSenderConfig emailSenderConfig;

    /**
     * Sends the notification to user's email. If successful, sets the notification state to
     * {@link NotificationConstants#STATE_SENT}, otherwise it sets it to {@link NotificationConstants#STATE_NOT_SENT}
     *
     * @param notification
     *            the notification to send
     * @return the notification with state set
     */
    public Notification send(Notification notification)
    {
        Exception exception = null;

        if (notification == null)
        {
            return null;
        }

        try
        {
            // Notifications are always send as system user
            getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

            EmailWithAttachmentsDTO in = new EmailWithAttachmentsDTO();
            in.setHeader("");
            in.setFooter("");

            String template = notificationTemplates.get(notification.getNote());
            if (template == null)
            {
                setupDefaultTemplateAndBody(notification, in);
            }
            else
            {
                AcmAbstractDao<AcmObject> dao = getDataService().getDaoByObjectType(notification.getParentType());
                AcmObject object = dao.find(notification.getParentId());
                try
                {
                    String body = getTemplatingEngine().process(template, notification.getNote(), object);
                    in.setBody(body);
                    in.setTemplate(body);
                }
                catch (TemplateException | IOException e)
                {
                    // failing to send an email should not break the flow
                    LOG.error("Unable to generate email for {} about {} with ID [{}]", Arrays.asList(notification.getUserEmail()),
                            object.getObjectType(), object.getId(), e);
                    setupDefaultTemplateAndBody(notification, in);
                }

            }

            in.setSubject(notification.getTitle());
            in.setEmailAddresses(Arrays.asList(notification.getUserEmail()));

            Authentication authentication = SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication()
                    : null;

            String userId = emailSenderConfig.getUsername();

            AcmUser acmUser = userDao.findByUserId(userId);

            getEmailSenderService().sendEmail(in, authentication, acmUser);

        }
        catch (Exception e)
        {
            exception = e;
        }
        if (exception == null)
        {
            notification.setState(NotificationConstants.STATE_SENT);
        }
        else
        {
            LOG.error("Notification message not sent ...", exception);
            notification.setState(NotificationConstants.STATE_NOT_SENT);
        }

        return notification;
    }

    private void setupDefaultTemplateAndBody(Notification notification, EmailWithAttachmentsDTO in)
    {
        in.setTemplate(notificationTemplate);
        String notificationLink = notificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId());

        String messageBody = notificationLink != null ? String.format("%s Link: %s", notification.getNote(), notificationLink)
                : notification.getNote();

        in.setBody(new MessageBodyFactory().buildMessageBodyFromTemplate(messageBody, "", ""));
    }

    public abstract <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception;

    public abstract void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, String userId)
            throws Exception;

    public abstract void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception;

    public abstract void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user)
            throws Exception;

    public abstract List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in,
            Authentication authentication, AcmUser user) throws Exception;

    public void setNotificationUtils(NotificationUtils notificationUtils)
    {
        this.notificationUtils = notificationUtils;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AuthenticationTokenService getAuthenticationTokenService()
    {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService)
    {
        this.authenticationTokenService = authenticationTokenService;
    }

    public AuthenticationTokenDao getAuthenticationTokenDao()
    {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao)
    {
        this.authenticationTokenDao = authenticationTokenDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setNotificationTemplate(Resource notificationTemplate) throws IOException
    {
        DataInputStream resourceStream = new DataInputStream(notificationTemplate.getInputStream());
        byte[] bytes = new byte[resourceStream.available()];
        resourceStream.readFully(bytes);
        this.notificationTemplate = new String(bytes, Charset.forName("UTF-8"));
    }

    public AcmEmailSenderService getEmailSenderService()
    {
        return emailSenderService;
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    /**
     * @return the userDao
     */
    protected UserDao getUserDao()
    {
        return userDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public Map<String, String> getNotificationTemplates()
    {
        return notificationTemplates;
    }

    public void setNotificationTemplates(Map<String, String> notificationTemplates)
    {
        this.notificationTemplates = notificationTemplates;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }

    public AcmDataService getDataService()
    {
        return dataService;
    }

    public void setDataService(AcmDataService dataService)
    {
        this.dataService = dataService;
    }

    public EmailSenderConfig getEmailSenderConfig()
    {
        return emailSenderConfig;
    }

    public void setEmailSenderConfig(EmailSenderConfig emailSenderConfig)
    {
        this.emailSenderConfig = emailSenderConfig;
    }
}
