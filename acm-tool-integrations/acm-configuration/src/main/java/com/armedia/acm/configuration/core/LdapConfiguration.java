package com.armedia.acm.configuration.core;

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
