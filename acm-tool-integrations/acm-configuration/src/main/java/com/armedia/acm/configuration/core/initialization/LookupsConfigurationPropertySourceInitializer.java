package com.armedia.acm.configuration.core.initialization;

import com.armedia.acm.configuration.core.LookupsConfigurationContainer;
import com.armedia.acm.configuration.core.propertysource.LookupsConfigServerPropertyResource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author mario.gjurcheski
 *
 */

@Component("lookupsConfig")
@DependsOn("bootstrapConfig")
public class LookupsConfigurationPropertySourceInitializer implements Ordered, InitializingBean
{
    @Autowired
    ConfigurableEnvironment configurableEnvironment;

    @Autowired
    LookupsConfigurationContainer lookupsConfiguration;

    private PropertySource getPropertySource()
    {
        lookupsConfiguration.refresh();
        return new LookupsConfigServerPropertyResource(lookupsConfiguration);
    }

    @Override
    public void afterPropertiesSet()
    {
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        propertySources.addLast(getPropertySource());
    }

    @Override
    public int getOrder()
    {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

}
