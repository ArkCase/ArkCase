package com.armedia.acm.services.users.model.ldap;

import com.armedia.acm.services.users.model.LdapGroup;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

import javax.naming.Name;
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

        if (adapter.attributeExists("memberOf"))
        {
            String[] groupIsMemberOf = adapter.getStringAttributes("memberOf");
            Set<String> parentGroups = MapperUtils.mapAttributes(groupIsMemberOf, MapperUtils.getRdnMappingFunction("cn"))
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());
            group.setParentGroups(parentGroups);
        }
        if (adapter.attributeExists("member"))
        {
            String[] members = adapter.getStringAttributes("member");

            Set<String> memberGroupNames = Arrays.stream(members)
                    .filter(dn -> {
                        dn = MapperUtils.stripBaseFromDn(dn, acmLdapSyncConfig.getBaseDC());
                        Name groupSearchDn = new DistinguishedName(acmLdapSyncConfig.getGroupSearchBase());
                        DistinguishedName memberDn = new DistinguishedName(dn);
                        return memberDn.startsWith(groupSearchDn);
                    })
                    .map(MapperUtils.getRdnMappingFunction("cn"))
                    .map(String::toUpperCase)
                    .collect(Collectors.toSet());

            Set<String> memberUserDns = Arrays.stream(members)
                    .filter(dn -> {
                        dn = MapperUtils.stripBaseFromDn(dn, acmLdapSyncConfig.getBaseDC());
                        Name userSearchDn = new DistinguishedName(acmLdapSyncConfig.getUserSearchBase());
                        DistinguishedName memberDn = new DistinguishedName(dn);
                        return memberDn.startsWith(userSearchDn);
                    })
                    .map(DistinguishedName::new)
                    .map(DistinguishedName::toString)
                    .collect(Collectors.toSet());

            group.setMemberGroups(memberGroupNames);
            group.setMemberUsers(memberUserDns);
        }
        return group;
    }
}
