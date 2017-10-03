package com.armedia.acm.services.users.model.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (groupName != null)
        {
            group.setName(groupName.toUpperCase());
        }

        group.setDistinguishedName(MapperUtils.appendBaseToDn(adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        group.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getGroupsSortingAttribute()));
        group.setDescription(MapperUtils.getAttribute(adapter, "description"));
        group.setDirectoryName(acmLdapSyncConfig.getDirectoryName());

        if (adapter.attributeExists("member"))
        {
            String[] members = adapter.getStringAttributes("member");

            Set<String> memberDns = Arrays.stream(members)
                    .map(DistinguishedName::new)
                    .map(DistinguishedName::toString)
                    .collect(Collectors.toSet());

            group.setMembers(memberDns);
        }
        return group;
    }
}
