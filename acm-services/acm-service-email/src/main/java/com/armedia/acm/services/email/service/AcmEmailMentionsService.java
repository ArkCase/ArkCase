package com.armedia.acm.services.email.service;

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailMentionsDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ivana.shekerova on 11/13/2018.
 */
public class AcmEmailMentionsService
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmEmailSenderService emailSenderService;
    private AcmApplication acmAppConfiguration;
    private String mentionsEmailSubject;
    private String mentionsEmailBodyTemplate;

    private String buildEmailSubject(EmailMentionsDTO in, String userFullName)
    {
        return String.format(mentionsEmailSubject,
                userFullName,
                in.getObjectType() + " " + in.getObjectId());
    }

    private EmailBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> buildEmail(EmailMentionsDTO in, String userFullName)
    {
        EmailBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBuilder = (emailUserData, messageProps) -> {
            messageProps.put("to", emailUserData.getKey());
            messageProps.put("subject", buildEmailSubject(in, userFullName));
        };
        return emailBuilder;
    }

    private String buildObjectUrl(EmailMentionsDTO in)
    {
        String baseUrl = acmAppConfiguration.getBaseUrl();
        String subType = !in.getSubType().isEmpty() ? in.getSubType() : in.getObjectType();
        Optional<String> objectUrl = acmAppConfiguration.getObjectTypes().stream()
                .filter(acmObjectType -> acmObjectType.getName().equals(in.getObjectType()))
                .map(acmObjectType -> acmObjectType.getUrl().get(subType))
                .collect(Collectors.toList()).stream().findFirst();
        return objectUrl.isPresent() ? baseUrl + String.format(objectUrl.get(), in.getObjectId()) : baseUrl;
    }

    private EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> buildEmailBody(EmailMentionsDTO in,
            String userFullName)
    {
        EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBodyBuilder = emailData -> String.format(
                mentionsEmailBodyTemplate,
                buildObjectUrl(in),
                buildObjectUrl(in),
                buildEmailSubject(in, userFullName),
                in.getTextMentioned());
        return emailBodyBuilder;
    }

    public void sendMentionsEmail(EmailMentionsDTO in, String userFullName) throws AcmEmailServiceException
    {
        EmailBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBuilder = buildEmail(in, userFullName);
        EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBodyBuilder = buildEmailBody(in, userFullName);

        List<String> emailAddresses = in.getEmailAddresses();
        for (String emailAddress : emailAddresses)
        {
            try
            {
                log.debug("Sending email on address: [{}] because user was mentioned in: [{}] [{}]", emailAddress, in.getObjectType(),
                        in.getObjectId());
                List<String> userAccounts = new ArrayList<>();
                userAccounts.add(emailAddress);
                AbstractMap.SimpleImmutableEntry<String, List<String>> emailUserData = new AbstractMap.SimpleImmutableEntry<>(emailAddress,
                        userAccounts);
                emailSenderService.sendPlainEmail(Stream.of(emailUserData), emailBuilder, emailBodyBuilder);
            }
            catch (Exception e)
            {
                log.error("Email was not sent to address: [{}]", emailAddress, e);
                throw new AcmEmailServiceException(
                        "Could not send emails with attachment, among other things check your request body. Exception message is : "
                                + e.getMessage(),
                        e);
            }
        }
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public void setMentionsEmailSubject(String mentionsEmailSubject)
    {
        this.mentionsEmailSubject = mentionsEmailSubject;
    }

    public void setMentionsEmailBodyTemplate(String mentionsEmailBodyTemplate)
    {
        this.mentionsEmailBodyTemplate = mentionsEmailBodyTemplate;
    }
}
