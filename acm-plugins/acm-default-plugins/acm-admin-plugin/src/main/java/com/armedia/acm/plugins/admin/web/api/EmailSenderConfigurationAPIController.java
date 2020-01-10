package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.services.email.exception.MailRefusedConnectionException;
import com.armedia.acm.services.email.exception.RejectedLoginException;
import com.armedia.acm.services.email.exception.StartTLSNotSupportedException;
import com.armedia.acm.services.email.sender.service.EmailSenderConfigurationServiceImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class EmailSenderConfigurationAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private EmailSenderConfigurationServiceImpl emailSenderConfigurationService;

    @RequestMapping(value = "/email/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailSenderConfig getConfiguration()
    {
        return emailSenderConfigurationService.readConfiguration();
    }

    @RequestMapping(value = "/email/sender/allowdocuments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean getSenderAllowDocuments()
    {
        return emailSenderConfigurationService.readConfiguration().getAllowDocuments();

    }

    @RequestMapping(value = "/email/sender/allowattachments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean getSenderAllowAttachments()
    {
        return emailSenderConfigurationService.readConfiguration().getAllowAttachments();
    }

    @RequestMapping(value = "/email/sender/allowhyperlinks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean getSenderAllowHyperlinks()
    {
        return emailSenderConfigurationService.readConfiguration().getAllowHyperlinks();

    }

    @RequestMapping(value = "/email/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateConfiguration(@RequestBody EmailSenderConfig configuration)
    {
        emailSenderConfigurationService.writeConfiguration(configuration);
    }

    @RequestMapping(value = "/email/configuration/validate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean validateSmtpConfiguration(@RequestBody EmailSenderConfig configuration)
            throws MailRefusedConnectionException, RejectedLoginException, StartTLSNotSupportedException
    {
        return emailSenderConfigurationService.validateSmtpConfiguration(configuration);
    }

    /**
     * @param emailSenderConfigurationService
     *            the emailSenderConfigurationService to set
     */
    public void setEmailSenderConfigurationService(EmailSenderConfigurationServiceImpl emailSenderConfigurationService)
    {
        this.emailSenderConfigurationService = emailSenderConfigurationService;
    }

}
