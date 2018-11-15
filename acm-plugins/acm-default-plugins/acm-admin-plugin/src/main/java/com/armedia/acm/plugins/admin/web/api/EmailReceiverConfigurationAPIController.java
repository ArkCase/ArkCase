package com.armedia.acm.plugins.admin.web.api;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.services.email.receiver.modal.EmailReceiverConfiguration;
import com.armedia.acm.services.email.receiver.service.EmailReceiverConfigurationService;
import com.armedia.acm.services.email.receiver.service.EmailReceiverConfigurationServiceImpl;
import com.armedia.acm.services.email.service.AcmEmailConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    private EmailReceiverConfigurationService emailReceiverConfigurationService;

    @RequestMapping(value = "/email/receiver/configuration", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmailReceiverConfiguration getConfiguration() throws AcmEncryptionException
    {
        return emailReceiverConfigurationService.readConfiguration();
    }

    @RequestMapping(value = "/email/receiver/configuration", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateConfiguration(@RequestBody EmailReceiverConfiguration emailReceiverConfiguration, Authentication auth)
            throws AcmEncryptionException, AcmEmailConfigurationException
    {
        try
        {
            log.debug("Writing email receiver configuration [{}] ", emailReceiverConfiguration);
            emailReceiverConfigurationService.writeConfiguration(emailReceiverConfiguration, auth);
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
