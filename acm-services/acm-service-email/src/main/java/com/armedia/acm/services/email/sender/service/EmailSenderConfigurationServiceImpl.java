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
import com.armedia.acm.services.email.exception.MailRefusedConnectionException;
import com.armedia.acm.services.email.exception.RejectedLoginException;
import com.armedia.acm.services.email.exception.StartTLSNotSupportedException;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public boolean validateSmtpConfiguration(EmailSenderConfig configuration) throws MailRefusedConnectionException, RejectedLoginException,
            StartTLSNotSupportedException
    {
        boolean validation = false;
        AuthenticatingSMTPClient authenticatingSmtpClient = null;

        try
        {
            if (configuration.getEncryption().equals("off") || configuration.getEncryption().equals("starttls"))
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
                throw new MailRefusedConnectionException("Rejected connection");
            }

            if (configuration.getEncryption().equals("starttls"))
            {
                authenticatingSmtpClient.ehlo("localhost");
                authenticatingSmtpClient.execTLS();
                if (!isPositiveReply(authenticatingSmtpClient))
                {
                    throw new StartTLSNotSupportedException("STARTTLS is not supported");
                }

                validation = true;
            }

            if (configuration.getEncryption().equals("starttls") || configuration.getEncryption().equals("ssl-tls"))
            {
                authenticatingSmtpClient.ehlo("localhost");
                authenticatingSmtpClient.auth(AuthenticatingSMTPClient.AUTH_METHOD.LOGIN, configuration.getUsername(),
                        configuration.getPassword());
                if (!isPositiveReply(authenticatingSmtpClient))
                {
                    validation = false;
                    throw new RejectedLoginException("Invalid username or password");
                }
            }

            validation = true;

        }
        catch (MailRefusedConnectionException | RejectedLoginException | StartTLSNotSupportedException e)
        {
            log.error("SMTP Error, [{}]", e.getMessage(), e);
            throw e;
        }

        catch (Exception e)
        {
            throw new MailRefusedConnectionException("Invalid server address and port");
        }

        finally
        {
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
