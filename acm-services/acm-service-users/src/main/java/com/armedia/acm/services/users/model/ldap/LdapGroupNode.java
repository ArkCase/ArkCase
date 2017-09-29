package com.armedia.acm.services.users.model.ldap;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapGroupNode
{
    private LdapGroup ldapGroup;

    public LdapGroupNode(LdapGroup ldapGroup)
    {
        this.ldapGroup = ldapGroup;
    }

    public Set<LdapGroupNode> getNodes()
    {
        return ldapGroup.getMemberGroups().stream()
                .map(LdapGroupNode::new)
                .collect(Collectors.toSet());
    }

    public LdapGroup getLdapGroup()
    {
        return ldapGroup;
    }

    public String getName()
    {
        return ldapGroup.getName();
    }

    @Override
    public int hashCode()
    {
        return ldapGroup.getName().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LdapGroupNode that = (LdapGroupNode) o;
        return Objects.equals(ldapGroup, that.ldapGroup);
    }

    @Override
    public String toString()
    {
        return ldapGroup.getName();
    }
}
