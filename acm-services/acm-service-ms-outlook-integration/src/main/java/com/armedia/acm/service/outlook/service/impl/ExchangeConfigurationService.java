package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.service.outlook.model.ExchangeConfiguration;
import com.armedia.acm.service.outlook.model.ExchangeConfigurationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author sasko.tanaskoski
 *
 */
public class ExchangeConfigurationService
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Resource exchangePropertiesResource;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public void writeConfiguration(ExchangeConfiguration configuration)
    {

        Properties exchangeProperties = new Properties();
        exchangeProperties.put(ExchangeConfigurationProperties.INTEGRATION_ENABLED, Boolean.toString(configuration.isIntegrationEnabled()));
        exchangeProperties.put(ExchangeConfigurationProperties.SERVER_VERSION, configuration.getServerVersion());
        exchangeProperties.put(ExchangeConfigurationProperties.ENABLE_AUTODISCOVERY,
                Boolean.toString(configuration.isEnableAutodiscovery()));
        exchangeProperties.put(ExchangeConfigurationProperties.CLIENT_ACCESS_SERVER, configuration.getClientAccessServer());
        exchangeProperties.put(ExchangeConfigurationProperties.DEFAULT_ACCESS, configuration.getDefaultAccess());

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try
        {
            exchangeProperties.store(new FileOutputStream(exchangePropertiesResource.getFile()),
                    String.format("Updated from ", getClass().getName()));
        } catch (IOException e)
        {
            log.error("Could not write properties to {} file.", exchangePropertiesResource.getFilename());
        } finally
        {
            writeLock.unlock();
        }
    }

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
            case ExchangeConfigurationProperties.INTEGRATION_ENABLED:
                exchangeConfiguration.setIntegrationEnabled(Boolean.valueOf(propertyValue));
                break;
            case ExchangeConfigurationProperties.SERVER_VERSION:
                exchangeConfiguration.setServerVersion(propertyValue);
                break;
            case ExchangeConfigurationProperties.ENABLE_AUTODISCOVERY:
                exchangeConfiguration.setEnableAutodiscovery(Boolean.valueOf(propertyValue));
                break;
            case ExchangeConfigurationProperties.CLIENT_ACCESS_SERVER:
                exchangeConfiguration.setClientAccessServer(propertyValue);
                break;
            case ExchangeConfigurationProperties.DEFAULT_ACCESS:
                exchangeConfiguration.setDefaultAccess(propertyValue);
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
        try
        {
            exchangeProperties.load(exchangePropertiesResource.getInputStream());
        } catch (IOException e)
        {
            log.error("Could not read properties from {} file.", exchangePropertiesResource.getFilename());
        } finally
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
