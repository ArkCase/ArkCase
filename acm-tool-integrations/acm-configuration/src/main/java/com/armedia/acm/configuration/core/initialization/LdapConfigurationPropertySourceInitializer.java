package com.armedia.acm.configuration.core.initialization;

import com.armedia.acm.configuration.core.LdapConfiguration;
import com.armedia.acm.configuration.core.propertysource.LdapConfigServerPropertyResource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

@Component("ldapConfig")
@DependsOn("bootstrapConfig")
public class LdapConfigurationPropertySourceInitializer implements Ordered, InitializingBean
{

    @Autowired
    ConfigurableEnvironment configurableEnvironment;

    @Autowired
    LdapConfiguration ldapConfiguration;

    private PropertySource getPropertySource()
    {
        ldapConfiguration.refresh();
        return new LdapConfigServerPropertyResource(ldapConfiguration);
    }

    @Override
    public void afterPropertiesSet() throws Exception
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
