package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.service.outlook.model.ExchangeConfiguration;
import com.armedia.acm.service.outlook.model.ExchangeConfigurationConstants;
import com.armedia.acm.service.outlook.service.ExchangeConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author sasko.tanaskoski
 */
public class ExchangeConfigurationServiceImpl implements ExchangeConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource exchangePropertiesResource;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void writeConfiguration(ExchangeConfiguration configuration, Authentication auth)
    {

        Properties exchangeProperties = new Properties();
        exchangeProperties.put(ExchangeConfigurationConstants.INTEGRATION_ENABLED, Boolean.toString(configuration.isIntegrationEnabled()));
        exchangeProperties.put(ExchangeConfigurationConstants.SERVER_VERSION, configuration.getServerVersion());
        exchangeProperties.put(ExchangeConfigurationConstants.ENABLE_AUTODISCOVERY,
                Boolean.toString(configuration.isEnableAutodiscovery()));
        exchangeProperties.put(ExchangeConfigurationConstants.CLIENT_ACCESS_SERVER, configuration.getClientAccessServer());
        exchangeProperties.put(ExchangeConfigurationConstants.DEFAULT_ACCESS, configuration.getDefaultAccess());
        exchangeProperties.put(ExchangeConfigurationConstants.SYSTEM_USER_EMAIL, configuration.getSystemUserEmail());
        exchangeProperties.put(ExchangeConfigurationConstants.SYSTEM_USER_EMAIL_PASSWORD, configuration.getSystemUserEmailPassword());
        exchangeProperties.put(ExchangeConfigurationConstants.SYSTEM_USER_ID, configuration.getSystemUserId());

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try (OutputStream propertyOutputStream = new FileOutputStream(exchangePropertiesResource.getFile()))
        {
            exchangeProperties.store(propertyOutputStream, String.format("Updated by %s", auth.getName()));
        }
        catch (IOException e)
        {
            log.error("Could not write properties to [{}] file.", exchangePropertiesResource.getFilename());
        }
        finally
        {
            writeLock.unlock();
        }
    }

    @Override
    public ExchangeConfiguration readConfiguration()
    {
        ExchangeConfiguration exchangeConfiguration = new ExchangeConfiguration();

        Properties exchangeProperties = loadProperties();

        Set<String> propertyNames = exchangeProperties.stringPropertyNames();

        for (String propertyName : propertyNames)
        {

            String propertyValue = exchangeProperties.getProperty(propertyName);
            switch (propertyName)
            {
            case ExchangeConfigurationConstants.INTEGRATION_ENABLED:
                exchangeConfiguration.setIntegrationEnabled(Boolean.valueOf(propertyValue));
                break;
            case ExchangeConfigurationConstants.SERVER_VERSION:
                exchangeConfiguration.setServerVersion(propertyValue);
                break;
            case ExchangeConfigurationConstants.ENABLE_AUTODISCOVERY:
                exchangeConfiguration.setEnableAutodiscovery(Boolean.valueOf(propertyValue));
                break;
            case ExchangeConfigurationConstants.CLIENT_ACCESS_SERVER:
                exchangeConfiguration.setClientAccessServer(propertyValue);
                break;
            case ExchangeConfigurationConstants.DEFAULT_ACCESS:
                exchangeConfiguration.setDefaultAccess(propertyValue);
                break;
            case ExchangeConfigurationConstants.SYSTEM_USER_EMAIL:
                exchangeConfiguration.setSystemUserEmail(propertyValue);
                break;
            case ExchangeConfigurationConstants.SYSTEM_USER_EMAIL_PASSWORD:
                exchangeConfiguration.setSystemUserEmailPassword(propertyValue);
                break;
            case ExchangeConfigurationConstants.SYSTEM_USER_ID:
                exchangeConfiguration.setSystemUserId(propertyValue);
                break;
            }
        }

        return exchangeConfiguration;
    }

    private Properties loadProperties()
    {
        Properties exchangeProperties = new Properties();
        Lock readLock = lock.readLock();
        readLock.lock();
        try (InputStream propertyInputStream = exchangePropertiesResource.getInputStream())
        {
            exchangeProperties.load(propertyInputStream);
        }
        catch (IOException e)
        {
            log.error("Could not read properties from [{}] file.", exchangePropertiesResource.getFilename());
        }
        finally
        {
            readLock.unlock();

        }
        return exchangeProperties;
    }

    /**
     * @param exchangePropertiesResource
     *            the exchangePropertiesResource to set
     */
    public void setExchangePropertiesResource(Resource exchangePropertiesResource)
    {
        this.exchangePropertiesResource = exchangePropertiesResource;
    }

}
