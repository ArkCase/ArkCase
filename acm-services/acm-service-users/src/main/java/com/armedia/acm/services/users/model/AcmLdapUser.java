package com.armedia.acm.services.users.model;


import java.util.Set;

public interface AcmLdapUser
{
    Set<String> getLdapGroups();
}
