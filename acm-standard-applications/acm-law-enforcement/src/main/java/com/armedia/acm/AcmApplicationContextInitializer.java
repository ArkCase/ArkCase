package com.armedia.acm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * @author ivana.shekerova on 8/5/2019.
 */
public class AcmApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>
{

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext)
    {
        String confYamlPath = System.getProperty("acm.configurationserver.propertyfile");
        Resource yamlResource = new FileSystemResource(confYamlPath);

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(yamlResource);
        Properties properties = yaml.getObject();

        System.setProperty("configuration.server.url", properties.getProperty("configuration.server.url"));
        System.setProperty("application.name", properties.getProperty("application.name"));
        System.setProperty("application.profile", getProfilesReversed(properties.getProperty("application.profile")));
    }

    private String getProfilesReversed(String profiles)
    {
        String[] splitedProfiles = profiles.split(",");
        Collections.reverse(Arrays.asList(splitedProfiles));
        return StringUtils.join(splitedProfiles, ",");
    }
}
