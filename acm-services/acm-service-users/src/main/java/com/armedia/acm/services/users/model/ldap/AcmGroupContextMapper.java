package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.LdapGroup;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

import java.util.HashSet;
import java.util.Set;

public class AcmGroupContextMapper implements ContextMapper
{
    private AcmLdapSyncConfig acmLdapSyncConfig;

    public AcmGroupContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

    @Override
    public LdapGroup mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        LdapGroup group = new LdapGroup();
        String groupName = MapperUtils.getAttribute(adapter, "cn");
        // Throughout the application we use the group names in upper case only, so converting here at mapping level
        group.setGroupName(groupName.toUpperCase());
        group.setDistinguishedName(String.format("%s,%s", adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        group.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getGroupsSortingAttribute()));

        Set<String> potentialParentGroups = new HashSet<>();
        if (adapter.attributeExists("memberOf"))
        {
            String[] groupIsMemberOf = adapter.getStringAttributes("memberOf");
            potentialParentGroups = MapperUtils.arrayToSet(groupIsMemberOf, MapperUtils.MEMBER_TO_COMMON_NAME_UPPERCASE);
        }
        group.setMemberOfGroups(potentialParentGroups);
        return group;
    }

}
