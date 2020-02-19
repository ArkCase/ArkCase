package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.configuration.annotations.MapValue;

import java.util.Map;

public class AcmLdapConfiguration
{
    public static final String LDAP_SYNC_CONFIG_PROP_KEY = "springConfigLdapProperties";

    private Map<String, Map<String, Object>> attributes;

    @MapValue(value = LDAP_SYNC_CONFIG_PROP_KEY, convertFromTheRootKey = true, configurationName = "labelConfiguration")
    public Map<String, Map<String, Object>> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, Map<String, Object>> attributes)
    {
        this.attributes = attributes;
    }
}
