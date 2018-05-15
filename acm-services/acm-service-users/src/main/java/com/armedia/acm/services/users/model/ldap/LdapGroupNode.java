package com.armedia.acm.services.users.model.ldap;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LdapGroupNode that = (LdapGroupNode) o;
        return Objects.equals(ldapGroup, that.ldapGroup);
    }

    @Override
    public String toString()
    {
        return ldapGroup.getName();
    }
}
