package com.armedia.acm.configuration.core;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.configuration.api.ConfigurationFacade;
import com.armedia.acm.configuration.client.ConfigurationServiceBootClient;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.jms.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mario.gjurcheski
 *
 */
@Configuration
@ManagedResource(objectName = "configuration:name=ldap-service,type=com.armedia.acm.configuration.ConfigurationService,artifactId=ldap-service")
public class LdapConfiguration implements ConfigurationFacade
{
    private static final Logger log = LogManager.getLogger(LdapConfiguration.class);

    private volatile static LdapConfiguration INSTANCE;

    @Autowired
    private ConfigurationServiceBootClient configurationServiceBootClient;

    @Autowired
    private JmsTemplate jmsTemplate;

    private Map<String, Object> ldapDefaultMap = new HashMap<>();

    @Bean
    public static LdapConfiguration ldapConfiguration()
    {

        if (INSTANCE == null)
        {
            initialize();
        }
        return INSTANCE;
    }

    private static synchronized void initialize()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new LdapConfiguration();
        }
    }

    private synchronized void initializeLdapMap()
    {
        ldapDefaultMap = this.configurationServiceBootClient.loadLdapConfiguration("ldap", new HashMap<>(), null);

        sendMessageForRecreatingOfLdapBeans();

    }

    public Map<String, Object> getLdapDefaultMap()
    {
        return this.ldapDefaultMap;
    }

    @Override
    public Object getProperty(String channel, String stage, String instance, String name)
    {
        return null;
    }

    @Override
    public Object getProperty(String name)
    {
        return this.ldapDefaultMap.get(name);
    }

    @Override
    public void refresh()
    {
        initializeLdapMap();
    }

    private void sendMessageForRecreatingOfLdapBeans()
    {
        log.info("Sending configuration change topic message...");
        try
        {
            jmsTemplate.send(new ActiveMQTopic("reload.ldap.beans"), Session::createMessage);
            log.debug("Message successfully sent");
        }
        catch (JmsException e)
        {
            log.warn("Message not sent. [{}]", e.getMessage(), e);
        }
    }

}
