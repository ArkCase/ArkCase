package com.armedia.acm.services.email.service;

import com.armedia.acm.auth.web.ForgotUsernameEvent;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.stream.Stream;

public class OnForgotUsername implements ApplicationListener<ForgotUsernameEvent>
{
    private AcmEmailSenderService emailSenderService;

    private AcmApplication acmAppConfiguration;

    private String forgotUsernameEmailSubject;

    /**
     * Formatting string to be used for producing text to inserted as a body in the forgot username email. The formatting
     * string accepts the login link string twice.
     * e.g: "Proceed to login <a href='%s'>%s</a>"
     */
    private String forgotUsernameEmailBodyTemplate;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ForgotUsernameEvent forgotUsernameEvent)
    {
        AcmUser user = forgotUsernameEvent.getAcmUser();
        sendUsernameEmail(user);
    }

    private void sendUsernameEmail(AcmUser user)
    {
        try
        {
            log.debug("Sending email with found username: [{}]", user.getUserId());
            emailSenderService.sendPlainEmail(Stream.of(user), emailBuilder, emailBodyBuilder);
        }
        catch (Exception e)
        {
            log.error("Email with username was not sent for user: [{}]", user.getUserId());
        }
    }

    private EmailBuilder<AcmUser> emailBuilder = (acmUser, messageProps) ->
    {
        messageProps.put("to", acmUser.getMail());
        messageProps.put("subject", forgotUsernameEmailSubject);
    };

    private EmailBodyBuilder<AcmUser> emailBodyBuilder = user ->
            String.format(forgotUsernameEmailBodyTemplate, user.getUserId(),
                    acmAppConfiguration.getBaseUrl(), acmAppConfiguration.getBaseUrl());

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void setForgotUsernameEmailSubject(String forgotUsernameEmailSubject)
    {
        this.forgotUsernameEmailSubject = forgotUsernameEmailSubject;
    }

    public void setForgotUsernameEmailBodyTemplate(String forgotUsernameEmailBodyTemplate)
    {
        this.forgotUsernameEmailBodyTemplate = forgotUsernameEmailBodyTemplate;
    }
}
