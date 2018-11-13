package gov.foia.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import gov.foia.model.FoiaConfiguration;
import gov.foia.model.FoiaConfigurationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FoiaConfigurationService implements FoiaConfigurationConstants
{
    private PropertyFileManager propertyFileManager;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Resource foiaPropertiesResource;
    private Logger log = LoggerFactory.getLogger(getClass());


    public void writeConfiguration(FoiaConfiguration foiaConfiguration)
    {
        Properties foiaProperties = new Properties();
        foiaProperties.put(FoiaConfigurationConstants.MAX_DAYS_IN_BILLING_QUEUE, foiaConfiguration.getMaxDaysInBillingQueue().toString());
        foiaProperties.put(FoiaConfigurationConstants.MAX_DAYS_IN_HOLD_QUEUE, foiaConfiguration.getMaxDaysInHoldQueue().toString());
        foiaProperties.put(FoiaConfigurationConstants.HOLDED_AND_APPEALED_REQUESTS, foiaConfiguration.getHoldedAndAppealedRequestsDueDateUpdateEnabled().toString());
        foiaProperties.put(FoiaConfigurationConstants.EXTENSTION_WORKING_DAYS, foiaConfiguration.getRequestExtensionWorkingDays().toString());
        foiaProperties.put(FoiaConfigurationConstants.DASHBOARD_BANNER_ENABLED, foiaConfiguration.getDashboardBannerEnabled().toString());

        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try (OutputStream propertyOutputStream = new FileOutputStream(foiaPropertiesResource.getFile()))
        {
            foiaProperties.store(propertyOutputStream, String.format("Stored properties %s", propertyOutputStream));
        }
        catch (IOException e)
        {
            log.error("Could not write properties to [{}] file.", foiaPropertiesResource.getFilename());
        }
        finally
        {
            writeLock.unlock();
        }

    }

    public FoiaConfiguration readConfiguration()
    {
        FoiaConfiguration foiaConfiguration = new FoiaConfiguration();
        Properties foiaProperties = loadProperties();

        Set<String> propertyNames = foiaProperties.stringPropertyNames();
        for (String propertyName : propertyNames) {
            String propertyValue = foiaProperties.getProperty(propertyName);
            switch (propertyName) {
                case FoiaConfigurationConstants.MAX_DAYS_IN_BILLING_QUEUE:
                    foiaConfiguration.setMaxDaysInBillingQueue(Integer.valueOf(propertyValue));
                    break;
                case FoiaConfigurationConstants.MAX_DAYS_IN_HOLD_QUEUE:
                    foiaConfiguration.setMaxDaysInHoldQueue(Integer.valueOf(propertyValue));
                    break;
                case FoiaConfigurationConstants.HOLDED_AND_APPEALED_REQUESTS:
                    foiaConfiguration.setHoldedAndAppealedRequestsDueDateUpdateEnabled(Boolean.valueOf(propertyValue));
                    break;
                case FoiaConfigurationConstants.EXTENSTION_WORKING_DAYS:
                    foiaConfiguration.setRequestExtensionWorkingDays(Integer.valueOf(propertyValue));
                    break;
                case FoiaConfigurationConstants.DASHBOARD_BANNER_ENABLED:
                    foiaConfiguration.setDashboardBannerEnabled(Boolean.valueOf(propertyValue));
            }
        }
        return foiaConfiguration;
    }


    private Properties loadProperties()
    {
        Properties properties = new Properties();
        Lock readLock = lock.readLock();
        readLock.lock();
        try (InputStream propertyInputStream = foiaPropertiesResource.getInputStream())
        {
            properties.load(propertyInputStream);
        }
        catch (IOException e)
        {
            log.error("Could not read properties from [{}] file.", foiaPropertiesResource.getFilename());
        }
        finally
        {
            readLock.unlock();

        }
        return properties;
    }


    public PropertyFileManager getPropertyFileManager() {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
        this.propertyFileManager = propertyFileManager;
    }

    public Resource getFoiaPropertiesResource() {
        return foiaPropertiesResource;
    }

    public void setFoiaPropertiesResource(Resource foiaPropertiesResource) {
        this.foiaPropertiesResource = foiaPropertiesResource;
    }
}
