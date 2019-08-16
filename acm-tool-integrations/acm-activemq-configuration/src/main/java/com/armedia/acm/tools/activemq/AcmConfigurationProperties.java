package com.armedia.acm.tools.activemq;

import com.armedia.acm.configuration.client.ConfigurationServiceBootClient;
import com.armedia.acm.configuration.yaml.YamlFileConfiguration;
import com.armedia.acm.configuration.yaml.YamlInitializer;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author ivana.shekerova on 8/14/2019.
 */
public class AcmConfigurationProperties
{
    private final static String CONFIGURATION_SERVER_URL = "configuration.server.url";

    private Environment environment;
    private YamlFileConfiguration yamlFileConfiguration;
    private YamlInitializer yamlInitializer;
    private ConfigurableEnvironment configurableEnvironment;
    private ConfigurationServiceBootClient configurationServiceBootClient;

    public Properties getPropertiesFromActiveMqYaml()
    {
        environment = new StandardEnvironment();
        configurationServiceBootClient = new ConfigurationServiceBootClient();
        yamlInitializer = new YamlInitializer();
        yamlFileConfiguration = yamlInitializer.getYamlFileConfiguration();
        configurableEnvironment = new StandardEnvironment();

        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        propertySources.addFirst(new MapPropertySource("bootstrap", prepareConfigurationMap()));
        configurationServiceBootClient.setConfigurableEnvironment(configurableEnvironment);

        String serverUrl = environment.getProperty(CONFIGURATION_SERVER_URL);

        Map<String, Object> configurationMap = configurationServiceBootClient.loadConfiguration(serverUrl, "activemq-config");
        Properties props = new Properties();
        props.putAll(configurationMap);

        return props;
    }

    private Map<String, Object> prepareConfigurationMap()
    {
        Iterator<String> it = yamlFileConfiguration.getKeys();
        Map<String, Object> result = new HashMap<>();
        it.forEachRemaining(key -> result.put(key, yamlFileConfiguration.getProperty(key)));
        return result;
    }
}
