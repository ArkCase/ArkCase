package com.armedia.acm.services.email.service;

import com.armedia.acm.auth.web.ForgotUsernameEvent;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
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
        if (forgotUsernameEvent.isSucceeded())
        {
            AbstractMap.SimpleImmutableEntry<String, List<String>> emailUserData = (AbstractMap.SimpleImmutableEntry<String, List<String>>)
                    forgotUsernameEvent.getSource();
            sendUsernameEmail(emailUserData);
        }
    }

    private void sendUsernameEmail(AbstractMap.SimpleImmutableEntry<String, List<String>> emailUserData)
    {
        String userEmailAddress = emailUserData.getKey();
        String userAccounts = toUserAccountsString(emailUserData.getValue());
        try
        {
            log.debug("Sending email on address: [{}] with found user accounts: [{}]", userEmailAddress, userAccounts);
            emailSenderService.sendPlainEmail(Stream.of(emailUserData), emailBuilder, emailBodyBuilder);
        }
        catch (Exception e)
        {
            log.error("Email with username was not sent to address: [{}]", userEmailAddress);
        }
    }

    private EmailBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBuilder = (emailUserData, messageProps) ->
    {
        messageProps.put("to", emailUserData.getKey());
        messageProps.put("subject", forgotUsernameEmailSubject);
    };

    private EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBodyBuilder = emailUserData -> {

        int userAccountsNum = emailUserData.getValue().size();
        String userAccounts = toUserAccountsString(emailUserData.getValue());

        return String.format(forgotUsernameEmailBodyTemplate, userAccountsNum, userAccounts,
                acmAppConfiguration.getBaseUrl(), acmAppConfiguration.getBaseUrl());
    };

    private String toUserAccountsString(List<String> accounts)
    {
        return accounts.stream()
                .collect(Collectors.joining(","));
    }

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
