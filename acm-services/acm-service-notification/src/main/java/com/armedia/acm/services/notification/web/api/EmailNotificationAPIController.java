package com.armedia.acm.services.notification.web.api;

/**
 * Created by manoj.dhungana on 5/4/2015.
 */

import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.services.authenticationtoken.dao.AuthenticationTokenDao;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.notification.exception.AcmNotificationException;
import com.armedia.acm.services.notification.model.EmailNotificationDto;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.EmailNotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/api/v1/service/notification", "/api/latest/service/notification"})
public class EmailNotificationAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());
    private EmailNotificationSender emailNotificationSender;
    private AuthenticationTokenService authenticationTokenService;
    private AuthenticationTokenDao authenticationTokenDao;

    @RequestMapping(value = "/email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Notification> sendEmail(
            @RequestBody List<EmailNotificationDto> in,
            Authentication authentication
    ) throws AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Sending email to recipients");
        }
        List<Notification> notificationList = new ArrayList<Notification>();
        try {
            if (in == null) {
                throw new AcmNotificationException("Could not create notification for email, missing email addresses and file information");
            }
            for(EmailNotificationDto emailNotification : in){
                Notification notification = new Notification();
                notification.setTitle(emailNotification.getTitle());
                notification.setNote(makeNote(emailNotification, authentication));

                for (String emailAddress: emailNotification.getEmailAddresses()) {
                    notification.setUserEmail(emailAddress);
                    notification.setStatus(NotificationConstants.STATUS_NEW);
                    notificationList.add(getEmailNotificationSender().send(notification));
                }
            }

            return notificationList;
        }
        catch (Exception e) {
            throw new AcmUserActionFailedException("Unable to send emails ", null, null, e.getMessage(), e);
        }
    }

    public String makeNote(EmailNotificationDto emailNotificationDto, Authentication authentication){
        String note="";
        String token = generateAndSaveAuthenticationToken(authentication, emailNotificationDto);
        note += emailNotificationDto.getHeader();

        for(String url: emailNotificationDto.getUrls()){
            note+= url+ "?acm_email_ticket=" + token + "\n";
        }
        note+= emailNotificationDto.getFooter();
        return note;
    }

    public String generateAndSaveAuthenticationToken(Authentication authentication, EmailNotificationDto emailNotificationDto){
        String token = getAuthenticationTokenService().getTokenForAuthentication(authentication);
        for(String email : emailNotificationDto.getEmailAddresses()){
            AuthenticationToken authenticationToken = new AuthenticationToken();
            authenticationToken.setKey(token);
            authenticationToken.setStatus(EcmFileConstants.ACTIVE);
            authenticationToken.setEmail(email);
            //authenticationToken.setFileId(emailNotificationDto.get);
            getAuthenticationTokenDao().save(authenticationToken);
            getAuthenticationTokenDao().findAuthenticationTokenByKey(token);
        }
        return token;
    }

    public EmailNotificationSender getEmailNotificationSender() {
        return emailNotificationSender;
    }

    public void setEmailNotificationSender(EmailNotificationSender emailNotificationSender) {
        this.emailNotificationSender = emailNotificationSender;
    }


    public AuthenticationTokenService getAuthenticationTokenService() {
        return authenticationTokenService;
    }

    public void setAuthenticationTokenService(AuthenticationTokenService authenticationTokenService) {
        this.authenticationTokenService = authenticationTokenService;
    }

    public AuthenticationTokenDao getAuthenticationTokenDao() {
        return authenticationTokenDao;
    }

    public void setAuthenticationTokenDao(AuthenticationTokenDao authenticationTokenDao) {
        this.authenticationTokenDao = authenticationTokenDao;
    }
}