package com.armedia.acm.configuration.core.propertysource;

import com.armedia.acm.configuration.core.LookupsConfigurationContainer;

import org.springframework.core.env.PropertySource;

/**
 * @author mario.gjurcheski
 *
 */
public class LookupsConfigServerPropertyResource extends PropertySource<LookupsConfigurationContainer>
{

    private final static String CONFIGURATION_SERVER_SOURCE_NAME = "lookups-configuration-server";

    public LookupsConfigServerPropertyResource(String name, LookupsConfigurationContainer source)
    {
        super(name, source);
    }

    public LookupsConfigServerPropertyResource(String name)
    {
        super(name);
    }

    public LookupsConfigServerPropertyResource(LookupsConfigurationContainer source)
    {
        super(CONFIGURATION_SERVER_SOURCE_NAME, source);
    }

    @Override
    public Object getProperty(String s)
    {
        return null;
    }
}
