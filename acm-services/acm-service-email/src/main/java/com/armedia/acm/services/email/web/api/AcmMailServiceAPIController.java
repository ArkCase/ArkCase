package com.armedia.acm.services.email.web.api;

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

import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.service.AcmEmailConfigurationException;
import com.armedia.acm.services.email.service.AcmEmailContentGeneratorService;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.email.service.AcmEmailServiceException;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.email.service.EmailSource;
import com.armedia.acm.services.email.service.EmailTemplateConfiguration;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 20, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/email/send", "/api/latest/service/email/send" })
public class AcmMailServiceAPIController
{
    private AcmMailTemplateConfigurationService templateService;
    private AcmEmailContentGeneratorService contentService;
    private AcmEmailSenderService emailSenderService;

    @RequestMapping(value = "/withattachments/{objectType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailWithAttachmentsDTO createEmailWithAttachments(@RequestBody EmailWithAttachmentsDTO in,
            @PathVariable(value = "objectType") String objectType, Authentication authentication, HttpSession session)
            throws AcmEmailServiceException
    {
        if (null == in)
        {
            throw new AcmEmailServiceException("Could not create email message, invalid input : " + in);
        }

        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            List<String> emailAddresses = in.getEmailAddresses();
            String emailAddress = String.join(",", in.getEmailAddresses());
            List<String> templates = loadTemplates(objectType, emailAddress, Arrays.asList("sendAsAttachments"));
            for (String template : templates)
            {
                String body = contentService.generateEmailBody(in, template);
                in.setBody(body);
                in.setEmailAddresses(emailAddresses);
                emailSenderService.sendEmailWithAttachments(in, authentication, user);
            }
        }
        catch (Exception e)
        {
            throw new AcmEmailServiceException(
                    "Could not send emails with attachment,among other things check your request body. Exception message is : "
                            + e.getMessage(),
                    e);
        }

        return in;
    }

    @RequestMapping(value = "/withembeddedlinks/{objectType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmailWithEmbeddedLinksResultDTO> createEmailWithEmbeddedLinks(@RequestBody EmailWithEmbeddedLinksDTO in,
            @PathVariable(value = "objectType") String objectType, Authentication authentication, HttpSession session)
            throws AcmEmailServiceException
    {
        if (null == in)
        {
            throw new AcmEmailServiceException("Could not create email message, invalid input : " + in);
        }

        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            List<EmailWithEmbeddedLinksResultDTO> result = new ArrayList<>();
            List<String> emailAddresses = in.getEmailAddresses();
            String emailAddress = String.join(",", in.getEmailAddresses());
            List<String> templates = loadTemplates(objectType, emailAddress, Arrays.asList("sendAsLinks"));
            for (String template : templates)
            {
                in.setTemplate(template);
                in.setEmailAddresses(emailAddresses);
                result.addAll(emailSenderService.sendEmailWithEmbeddedLinks(in, authentication, user));
            }
            return result;
        }
        catch (Exception e)
        {
            throw new AcmEmailServiceException(
                    "Could not send emails with embedded links, among other things check your request body. Exception message is : "
                            + e.getMessage(),
                    e);
        }
    }

    @RequestMapping(value = "/withattachmentsandlinks/{objectType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailWithAttachmentsAndLinksDTO createEmailWithAttachmentsAndLinks(@RequestBody EmailWithAttachmentsAndLinksDTO in,
            @PathVariable(value = "objectType") String objectType, Authentication authentication, HttpSession session)
            throws AcmEmailServiceException
    {
        if (null == in)
        {
            throw new AcmEmailServiceException("Could not create email message, invalid input : " + in);
        }

        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            List<String> emailAddresses = in.getEmailAddresses();
            String emailAddress = String.join(",", in.getEmailAddresses());
            List<String> templates = loadTemplates(objectType, emailAddress, Arrays.asList("sendAsAttachmentsAndLinks"));
            for (String template : templates)
            {
                in.setTemplate(template);
                in.setEmailAddresses(emailAddresses);
                emailSenderService.sendEmailWithAttachmentsAndLinks(in, authentication, user);
            }

        }
        catch (Exception e)
        {
            throw new AcmEmailServiceException(
                    "Could not send emails with attachment, among other things check your request body. Exception message is : "
                            + e.getMessage(),
                    e);
        }

        return in;
    }

    @RequestMapping(value = "/plainEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmailWithEmbeddedLinksResultDTO> createPlainEmail(@RequestBody EmailWithEmbeddedLinksDTO in, Authentication authentication,
            HttpSession session) throws AcmEmailServiceException
    {
        if (null == in)
        {
            throw new AcmEmailServiceException("Could not create email message, invalid input : " + in);
        }
        // the user is stored in the session during login.
        AcmUser user = (AcmUser) session.getAttribute("acm_user");

        try
        {
            List<EmailWithEmbeddedLinksResultDTO> result = new ArrayList<>();
            List<String> emailAddresses = in.getEmailAddresses();
            String templateName = String.format("%s.html", in.getModelReferenceName());
            String template = templateService.getTemplate(templateName);
            in.setTemplate(template);
            in.setEmailAddresses(emailAddresses);
            result.addAll(emailSenderService.sendEmailWithEmbeddedLinks(in, authentication, user));
            return result;
        }
        catch (Exception e)
        {
            throw new AcmEmailServiceException(
                    "Could not send plain emails, among other things check your request body. Exception message is : "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * @param objectType
     * @param emailAddress
     * @return
     * @throws AcmEmailConfigurationException
     */
    private List<String> loadTemplates(String objectType, String emailAddress, List<String> actions) throws AcmEmailConfigurationException
    {
        List<String> templates = new ArrayList<>();
        List<EmailTemplateConfiguration> configurations = templateService.getMatchingTemplates(emailAddress, objectType, EmailSource.MANUAL,
                actions);
        for (EmailTemplateConfiguration configuration : configurations)
        {
            String template = templateService.getTemplate(configuration.getTemplateName());
            templates.add(template);
        }
        return templates;
    }

    /**
     * @param templateService
     *            the templateService to set
     */
    public void setTemplateService(AcmMailTemplateConfigurationService templateService)
    {
        this.templateService = templateService;
    }

    /**
     * @param emailSenderService
     *            the emailSenderService to set
     */
    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public AcmEmailContentGeneratorService getContentService()
    {
        return contentService;
    }

    public void setContentService(AcmEmailContentGeneratorService contentService)
    {
        this.contentService = contentService;
    }
}
