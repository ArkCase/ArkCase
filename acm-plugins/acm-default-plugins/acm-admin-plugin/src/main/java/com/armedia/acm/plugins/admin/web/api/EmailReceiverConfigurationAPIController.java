package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.services.email.receiver.modal.EmailReceiverConfiguration;
import com.armedia.acm.services.email.receiver.service.EmailReceiverConfigurationServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class EmailReceiverConfigurationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private EmailReceiverConfigurationServiceImpl emailReceiverConfigurationService;

    @RequestMapping(value = "/email/receiver/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailReceiverConfiguration getConfiguration()
    {
        return emailReceiverConfigurationService.readConfiguration();
    }

    @RequestMapping(value = "/email/receiver/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateConfiguration(@RequestBody EmailReceiverConfiguration emailReceiverConfiguration, Authentication auth)
            throws AcmEncryptionException
    {
        emailReceiverConfigurationService.writeConfiguration(emailReceiverConfiguration, auth);
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
