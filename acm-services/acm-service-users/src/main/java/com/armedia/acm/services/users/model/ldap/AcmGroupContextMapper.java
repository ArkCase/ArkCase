package com.armedia.acm.services.users.model.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DistinguishedName;

import javax.naming.directory.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
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
        group.setName(MapperUtils.buildGroupName(groupName, Optional.of(acmLdapSyncConfig.getUserDomain())));

        group.setDistinguishedName(MapperUtils.appendToDn(adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        group.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getGroupsSortingAttribute()));
        group.setDescription(MapperUtils.getAttribute(adapter, "description"));
        group.setDirectoryName(acmLdapSyncConfig.getDirectoryName());
        group.setDisplayName(MapperUtils.getAttribute(adapter, "displayName"));

        // AFDP-5761 Support 'range' in member attribute for large group sizes.
        ArrayList<? extends Attribute> list = Collections.list(adapter.getAttributes().getAll());
        String rangedMember = list.stream().map(Attribute::getID).filter(id -> id.contains("range=")).findFirst().orElse("member");
        if (adapter.attributeExists(rangedMember))
        {
            String[] members = adapter.getStringAttributes(rangedMember);

            Set<String> memberDns = Arrays.stream(members)
                    .map(DistinguishedName::new)
                    .map(DistinguishedName::toString)
                    .collect(Collectors.toSet());

            group.setMembers(memberDns);
        }
        return group;
    }
}
