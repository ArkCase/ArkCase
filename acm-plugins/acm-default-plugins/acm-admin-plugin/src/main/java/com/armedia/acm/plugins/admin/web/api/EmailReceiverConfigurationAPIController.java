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

import com.armedia.acm.email.model.EmailReceiverConfig;
import com.armedia.acm.services.email.receiver.service.EmailReceiverConfigurationService;
import com.armedia.acm.services.email.receiver.service.EmailReceiverConfigurationServiceImpl;
import com.armedia.acm.services.email.service.AcmEmailConfigurationException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class EmailReceiverConfigurationAPIController
{

    private Logger log = LogManager.getLogger(getClass());

    private EmailReceiverConfigurationService emailReceiverConfigurationService;

    @RequestMapping(value = "/email/receiver/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailReceiverConfig getConfiguration()
    {
        return emailReceiverConfigurationService.readConfiguration();
    }

    @RequestMapping(value = "/email/receiver/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateConfiguration(@RequestBody EmailReceiverConfig emailReceiverConfiguration)
            throws AcmEmailConfigurationException
    {
        try
        {
            log.debug("Writing email receiver configuration [{}] ", emailReceiverConfiguration);
            emailReceiverConfigurationService.writeConfiguration(emailReceiverConfiguration);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Can not write configuration [{}] ", emailReceiverConfiguration, e);
            throw new AcmEmailConfigurationException("Can not write configuration");
        }

    }

    /**
     * @param emailReceiverConfigurationService
     *            the emailReceiverConfigurationService to set
     */
    public void setEmailReceiverConfigurationService(EmailReceiverConfigurationServiceImpl emailReceiverConfigurationService)
    {
        this.emailReceiverConfigurationService = emailReceiverConfigurationService;
    }
}
