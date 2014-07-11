package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.LdapGroup;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * Created by armdev on 5/28/14.
 */
public class GroupMembersContextMapper implements ContextMapper
{
    @Override
    public LdapGroup mapFromContext(Object ctx)
    {
        LdapGroup group = new LdapGroup();

        DirContextAdapter adapter = (DirContextAdapter) ctx;
        String groupName = adapter.getStringAttribute("cn");
        group.setGroupName(groupName);

        if ( adapter.attributeExists("member"))
        {
            String[] members = adapter.getStringAttributes("member");
            group.setMemberDistinguishedNames(members);
        }
        return group;
    }
}
