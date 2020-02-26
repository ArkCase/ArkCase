package com.armedia.acm.configuration.core.propertysource;

import com.armedia.acm.configuration.core.LdapConfiguration;

import org.springframework.core.env.PropertySource;

/**
 * @author mario.gjurcheski
 *
 */
public class LdapConfigServerPropertyResource extends PropertySource<LdapConfiguration>
{

    private final static String CONFIGURATION_SERVER_SOURCE_NAME = "ldap-configuration-server";

    public LdapConfigServerPropertyResource(String name, LdapConfiguration source)
    {
        super(name, source);
    }

    public LdapConfigServerPropertyResource(String name)
    {
        super(name);
    }

    public LdapConfigServerPropertyResource(LdapConfiguration source)
    {
        super(CONFIGURATION_SERVER_SOURCE_NAME, source);
    }

    @Override
    public Object getProperty(String s)
    {
        return null;
    }
}
