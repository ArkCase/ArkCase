package com.armedia.acm.services.email.sender.service;

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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.email.model.EmailSenderConfig;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;

/**
 * @author sasko.tanaskoski
 */
public class EmailSenderConfigurationServiceImpl implements EmailSenderConfigurationService
{

    private Logger log = LogManager.getLogger(getClass());

    private EmailSenderConfig emailSenderConfig;

    private ConfigurationPropertyService configurationPropertyService;

    @Override
    public void writeConfiguration(EmailSenderConfig configuration)
    {
        configurationPropertyService.updateProperties(configuration);
    }

    @Override
    public EmailSenderConfig readConfiguration()
    {
        return emailSenderConfig;
    }

    public boolean validateSmtpConfiguration(EmailSenderConfig configuration)
    {
        boolean validation = false;
        AuthenticatingSMTPClient authenticatingSmtpClient = null;

        try
        {
            if (configuration.getEncryption().equals("starttls") || configuration.getEncryption().equals("off"))
            {
                authenticatingSmtpClient = new AuthenticatingSMTPClient();
            }
            else
            {
                authenticatingSmtpClient = new AuthenticatingSMTPClient("TLS", true);
            }

            authenticatingSmtpClient.setConnectTimeout(3 * 1000);
            authenticatingSmtpClient.connect(configuration.getHost(), configuration.getPort());
            if (!isPositiveReply(authenticatingSmtpClient))
            {
                throw new Exception("Rejected connection");
            }

            if (configuration.getEncryption().equals("starttls"))
            {
                authenticatingSmtpClient.ehlo("");
                authenticatingSmtpClient.execTLS();
                if (!isPositiveReply(authenticatingSmtpClient))
                {
                    throw new Exception("STARTTLS not supported");
                }

                validation = true;
            }

            if (configuration.getEncryption().equals("starttls") || configuration.getEncryption().equals("ssl-tls"))
            {
                authenticatingSmtpClient.ehlo("");
                authenticatingSmtpClient.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, configuration.getUsername(),
                        configuration.getPassword());
                if (!isPositiveReply(authenticatingSmtpClient))
                {
                    throw new Exception("Rejected login");
                }
            }

            validation = true;

        }
        catch (Exception e)
        {
            log.error("SMTP Error, [{}]", e.getMessage(), e);
            if (authenticatingSmtpClient != null && authenticatingSmtpClient.isConnected())
            {
                try
                {
                    authenticatingSmtpClient.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }

        return validation;
    }

    private boolean isPositiveReply(SMTPClient smtpClient)
    {
        if (SMTPReply.isPositiveCompletion(smtpClient.getReplyCode()))
        {
            log.info("SMTP Positive Reply [{}] [{}]", smtpClient.getReplyCode(), smtpClient.getReplyString());
            return true;
        }
        else
        {
            log.error("SMTP Error Reply [{}] [{}]", smtpClient.getReplyCode(), smtpClient.getReplyString());
            return false;
        }
    }

    public EmailSenderConfig getEmailSenderConfig()
    {
        return emailSenderConfig;
    }

    public void setEmailSenderConfig(EmailSenderConfig emailSenderConfig)
    {
        this.emailSenderConfig = emailSenderConfig;
    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }
}
