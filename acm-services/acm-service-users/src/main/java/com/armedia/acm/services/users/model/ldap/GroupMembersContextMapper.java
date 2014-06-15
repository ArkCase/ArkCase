package com.armedia.acm.services.users.model.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * Created by armdev on 5/28/14.
 */
public class GroupMembersContextMapper implements ContextMapper
{
    @Override
    public String[] mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;
        if ( adapter.attributeExists("member"))
        {
            String[] members = adapter.getStringAttributes("member");
            return members;
        }
        return new String[0];
    }
}
