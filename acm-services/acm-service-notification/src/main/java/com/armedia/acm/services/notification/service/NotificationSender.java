package com.armedia.acm.services.notification.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
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
import com.armedia.acm.services.email.service.AcmEmailConfigurationIOException;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.TemplatingEngine;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import freemarker.template.TemplateException;

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

/**
 * @author riste.tutureski
 */
public abstract class NotificationSender
{
    private final Logger LOG = LogManager.getLogger(getClass());

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
    private AcmMailTemplateConfigurationService templateService;
    private NotificationConfig notificationConfig;

    /**
     * Sends the notification to user's email. If successful, sets the notification state to
     * {@link NotificationConstants#STATE_SENT}, otherwise it sets it to {@link NotificationConstants#STATE_NOT_SENT}
     *
     * @param notification
     *            the notification to send
     * @return the notification with state set
     */
    public Notification send(Notification notification, Object object)
    {
        if (notification == null)
        {
            return null;
        }

        LOG.debug("Sending notification email [{}] to recipients [{}]", notification.getTitle(), notification.getEmailAddresses());

        try
        {
            // Notifications are always send as system user
            getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

            EmailWithAttachmentsDTO in = new EmailWithAttachmentsDTO();
            in.setHeader("");
            in.setFooter("");

            String templateName = String.format("%s.html", notification.getTemplateModelName());
            try
            {
                String template = templateService.getTemplate(templateName);
                String body = getTemplatingEngine().process(template, notification.getTemplateModelName(), object);
                in.setBody(body);
            }
            catch (AcmEmailConfigurationIOException e)
            {
                LOG.warn("Sending notifications without a template is deprecated!");
                setupDefaultTemplateAndBody(notification, in);
            }
            catch (TemplateException | IOException e)
            {
                // failing to generate the email should not break the flow
                LOG.error("Unable to generate email for notification with template [{}].", notification.getTemplateModelName(), e);
                setupDefaultTemplateAndBody(notification, in);
            }

            if (notification.getAttachFiles())
            {
                List<Long> notificationFileIds = new ArrayList<>();
                for (EcmFileVersion fileVersion : notification.getFiles())
                {
                    notificationFileIds.add(fileVersion.getFile().getId());
                }
                in.setAttachmentIds(notificationFileIds);
            }

            in.setSubject(notification.getTitle());
            in.setEmailAddresses(Arrays.asList(notification.getEmailAddresses().split(",")));
            in.setEmailGroup(notification.getEmailGroup());
            if (notification.getRelatedObjectId() != null && notification.getRelatedObjectType() != null)
            {
                in.setObjectType(notification.getRelatedObjectType());
                in.setObjectId(notification.getRelatedObjectId().toString());
            }
            else
            {
                if (notification.getParentId() != null)
                {
                    in.setObjectId(notification.getParentId().toString());
                }
                in.setObjectType(notification.getParentType());
            }

            Authentication authentication = SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication()
                    : null;

            AcmUser acmUser = notification.getUser() != null ? userDao.findByUserId(notification.getUser()) : null;

            getEmailSenderService().sendEmail(in, authentication, acmUser);
            if (in.getMailSent())
            {
                notification.setState(NotificationConstants.STATE_SENT);
            }
            else
            {
                notification.setState(NotificationConstants.STATE_NOT_SENT);
            }
        }
        catch (Exception e)
        {
            LOG.error("Notification message not sent.", e);
            if (e instanceof TemplateException)
            {
                notification.setState(NotificationConstants.STATE_TEMPLATE_ERROR);
            }
            else
            {
                notification.setState(NotificationConstants.STATE_NOT_SENT);
            }
        }
        return notification;
    }

    private void setupDefaultTemplateAndBody(Notification notification, EmailWithAttachmentsDTO in)
    {
        String relativeNotificationLink = notificationUtils.buildNotificationLink(notification.getParentType(), notification.getParentId(),
                notification.getRelatedObjectType(), notification.getRelatedObjectId());
        String baseUrl = notificationConfig.getBaseUrl();
        String notificationLink = String.format("%s%s", baseUrl, relativeNotificationLink);

        String messageBody = notificationLink != null ? String.format("%s Link: %s", notification.getNote(), notificationLink)
                : notification.getNote();

        in.setBody(new MessageBodyFactory(notificationTemplate).buildMessageBodyFromTemplate(messageBody, "", ""));
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

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public NotificationConfig getNotificationConfig()
    {
        return notificationConfig;
    }

    public void setNotificationConfig(NotificationConfig notificationConfig)
    {
        this.notificationConfig = notificationConfig;
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

    public AcmMailTemplateConfigurationService getTemplateService()
    {
        return templateService;
    }

    public void setTemplateService(AcmMailTemplateConfigurationService templateService)
    {
        this.templateService = templateService;
    }
}
