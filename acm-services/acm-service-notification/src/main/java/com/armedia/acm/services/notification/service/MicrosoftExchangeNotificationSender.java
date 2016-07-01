package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.service.UserOrgService;
import com.armedia.acm.service.outlook.dao.impl.ExchangeWebServicesOutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.EmailWithAttachmentsDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.service.outlook.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.service.outlook.service.OutlookService;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.users.model.AcmUser;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MicrosoftExchangeNotificationSender implements NotificationSender
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private MuleContextManager muleContextManager;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;
    private OutlookService outlookService;
    private ExchangeWebServicesOutlookDao dao;
    private EcmFileService ecmFileService;
    private UserOrgService userOrgService;

    @Override
    public Notification send(Notification notification)
    {
        Exception exception = null;

        if (notification == null)
        {
            return null;
        }

        try
        {
            getAuditPropertyEntityAdapter().setUserId(NotificationConstants.SYSTEM_USER);

            String flow = "vm://sendEmailViaOutlook.in";

            EmailWithAttachmentsDTO emailInfo = new EmailWithAttachmentsDTO();
            emailInfo.setHeader("");
            emailInfo.setFooter("");
            emailInfo.setBody(notification.getNote());
            emailInfo.setSubject(notification.getTitle());
            emailInfo.setEmailAddresses(Arrays.asList(notification.getUserEmail()));

            String userId = getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_USER_KEY,
                    null);
            String userEmail = getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_FROM_KEY,
                    null);
            String userPass = getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_PASSWORD_KEY,
                    null);

            AcmOutlookUser user = new AcmOutlookUser(userId, userEmail, userPass);

            Authentication auth = SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication()
                    : null;

            Map<String, Object> messageProps = new HashMap<>();

            messageProps.put("emailInfo", emailInfo);
            messageProps.put("user", user);
            messageProps.put("authentication", auth);

            MuleMessage received = getMuleContextManager().send(flow, notification.getNote(), messageProps);

            exception = received.getInboundProperty("sendEmailException");
        } catch (MuleException e)
        {
            exception = e;
        } catch (AcmEncryptionException e)
        {
            exception = e;
        }

        if (exception == null)
        {
            notification.setState(NotificationConstants.STATE_SENT);
        } else
        {
            LOG.error("Notification message not sent ...", exception);
            notification.setState(NotificationConstants.STATE_NOT_SENT);
        }

        return notification;
    }

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        getOutlookService().sendEmailWithAttachments(in, outlookUser, authentication);
    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception
    {
        OutlookDTO outlookDTO = getUserOrgService().retrieveOutlookPassword(authentication);
        AcmOutlookUser outlookUser = new AcmOutlookUser(authentication.getName(), user.getMail(), outlookDTO.getOutlookPassword());
        return getOutlookService().sendEmailWithEmbeddedLinks(in, outlookUser, authentication);
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getNotificationPropertyFileLocation()
    {
        return notificationPropertyFileLocation;
    }

    public void setNotificationPropertyFileLocation(String notificationPropertyFileLocation)
    {
        this.notificationPropertyFileLocation = notificationPropertyFileLocation;
    }

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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

    public OutlookService getOutlookService()
    {
        return outlookService;
    }

    public void setOutlookService(OutlookService outlookService)
    {
        this.outlookService = outlookService;
    }

    public ExchangeWebServicesOutlookDao getDao()
    {
        return dao;
    }

    public void setDao(ExchangeWebServicesOutlookDao dao)
    {
        this.dao = dao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public UserOrgService getUserOrgService()
    {
        return userOrgService;
    }

    public void setUserOrgService(UserOrgService userOrgService)
    {
        this.userOrgService = userOrgService;
    }

}
