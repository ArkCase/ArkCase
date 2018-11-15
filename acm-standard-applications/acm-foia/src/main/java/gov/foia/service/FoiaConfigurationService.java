package gov.foia.service;

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import gov.foia.model.FoiaConfiguration;
import gov.foia.model.FoiaConfigurationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FoiaConfigurationService implements ApplicationListener<ConfigurationFileChangedEvent>
{
    private PropertyFileManager propertyFileManager;
    private String propertiesFile;
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, String> foiaProperties = new HashMap<>();

    public void initBean()
    {
        try
        {
          foiaProperties = getPropertyFileManager().readFromFileAsMap((new File(getPropertiesFile())));
        }
        catch(IOException e)
        {
            log.error("Could not read properties file [{}]", getPropertiesFile());
        }
    }


    public void writeConfiguration(FoiaConfiguration foiaConfiguration)
    {
        Map<String,String> properties = new HashMap<>();
        properties.put(FoiaConfigurationConstants.MAX_DAYS_IN_BILLING_QUEUE, foiaConfiguration.getMaxDaysInBillingQueue().toString());
        properties.put(FoiaConfigurationConstants.MAX_DAYS_IN_HOLD_QUEUE, foiaConfiguration.getMaxDaysInHoldQueue().toString());
        properties.put(FoiaConfigurationConstants.HOLDED_AND_APPEALED_REQUESTS, foiaConfiguration.getHoldedAndAppealedRequestsDueDateUpdateEnabled().toString());
        properties.put(FoiaConfigurationConstants.EXTENSTION_WORKING_DAYS, foiaConfiguration.getRequestExtensionWorkingDays().toString());
        properties.put(FoiaConfigurationConstants.DASHBOARD_BANNER_ENABLED, foiaConfiguration.getDashboardBannerEnabled().toString());

        getPropertyFileManager().storeMultiple(properties, getPropertiesFile(), true);
    }

    public FoiaConfiguration readConfiguration()
    {
        FoiaConfiguration foiaConfiguration = new FoiaConfiguration();
        for (String property : foiaProperties.keySet())
        {
            switch (property)
            {
                case FoiaConfigurationConstants.MAX_DAYS_IN_BILLING_QUEUE:
                    foiaConfiguration.setMaxDaysInBillingQueue(Integer.valueOf(foiaProperties.get(property)));
                    break;
                case FoiaConfigurationConstants.MAX_DAYS_IN_HOLD_QUEUE:
                    foiaConfiguration.setMaxDaysInHoldQueue(Integer.valueOf(foiaProperties.get(property)));
                    break;
                case FoiaConfigurationConstants.HOLDED_AND_APPEALED_REQUESTS:
                    foiaConfiguration.setHoldedAndAppealedRequestsDueDateUpdateEnabled(Boolean.valueOf(foiaProperties.get(property)));
                    break;
                case FoiaConfigurationConstants.EXTENSTION_WORKING_DAYS:
                    foiaConfiguration.setRequestExtensionWorkingDays(Integer.valueOf(foiaProperties.get(property)));
                    break;
                case FoiaConfigurationConstants.DASHBOARD_BANNER_ENABLED:
                    foiaConfiguration.setDashboardBannerEnabled(Boolean.valueOf(foiaProperties.get(property)));
            }
        }

        return foiaConfiguration;
    }

    public PropertyFileManager getPropertyFileManager() {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager) {
        this.propertyFileManager = propertyFileManager;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    @Override
    public void onApplicationEvent(ConfigurationFileChangedEvent event)
    {
        if(event.getConfigFile().getAbsolutePath().equals(getPropertiesFile()))
        {
            initBean();
        }

    }
}
