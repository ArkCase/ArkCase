
package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailMentionsDTO;
import com.armedia.acm.services.email.model.MentionsEmailConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final Logger log = LogManager.getLogger(getClass());

    private AcmEmailSenderService emailSenderService;
    private AcmApplication acmAppConfiguration;
    private MentionsEmailConfig mentionsEmailConfig;
    private String mentionsEmailBodyTemplate;
    private TemplatingEngine templatingEngine;
    private ApplicationConfig applicationConfig;

    private String buildEmailSubject(EmailMentionsDTO in, String userFullName)
    {
        return String.format(mentionsEmailConfig.getSubject(),
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
        String baseUrl = applicationConfig.getBaseUrl();
        if (in.getUrlPath() != null)
        {
            return baseUrl + in.getUrlPath();
        }
        else
        {
            String subType = !in.getSubType().isEmpty() ? in.getSubType() : in.getObjectType();
            Optional<String> objectUrl = acmAppConfiguration.getObjectTypes().stream()
                    .filter(acmObjectType -> acmObjectType.getName().equals(in.getObjectType()))
                    .map(acmObjectType -> acmObjectType.getUrl().get(subType))
                    .collect(Collectors.toList()).stream().findFirst();
            return objectUrl.map(s -> baseUrl + String.format(s, in.getObjectId())).orElse(baseUrl);
        }
    }

    private EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> buildEmailBody(EmailMentionsDTO in,
            String userFullName)
    {
        EmailBodyBuilder<AbstractMap.SimpleImmutableEntry<String, List<String>>> emailBodyBuilder = emailData -> String.format(
                mentionsEmailConfig.getBodyTemplate(),
                buildObjectUrl(in),
                buildObjectUrl(in),
                buildEmailSubject(in, userFullName),
                in.getTextMentioned());
        return emailBodyBuilder;
    }

    public void sendMentionsEmail(EmailMentionsDTO in, String userFullName) throws AcmEmailServiceException
    {
        in.setTemplatingEngine(getTemplatingEngine());

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

    public MentionsEmailConfig getMentionsEmailConfig()
    {
        return mentionsEmailConfig;
    }

    public void setMentionsEmailConfig(MentionsEmailConfig mentionsEmailConfig)
    {
        this.mentionsEmailConfig = mentionsEmailConfig;
    }

    public TemplatingEngine getTemplatingEngine()
    {
        return templatingEngine;
    }

    public void setTemplatingEngine(TemplatingEngine templatingEngine)
    {
        this.templatingEngine = templatingEngine;
    }
}