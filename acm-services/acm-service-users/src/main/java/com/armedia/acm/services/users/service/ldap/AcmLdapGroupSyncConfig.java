package com.armedia.acm.services.users.service.ldap;

import java.util.Map;

public class AcmLdapGroupSyncConfig
{
    private Map<String, String> attributes;

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes)
    {
        this.attributes = attributes;
    }
}
