package com.armedia.acm.services.users.dao.ldap;

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

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.ldap.LdapEntryTransformer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SpringLdapGroupDao
{
    private LdapEntryTransformer ldapEntryTransformer;

    private LdapCrudDao ldapCrudDao;

    private Logger log = LogManager.getLogger(getClass());

    public void createGroup(AcmGroup group, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        log.debug("Create Ldap Group [{}]", group.getName());
        DirContextAdapter context = ldapEntryTransformer.createContextForNewGroupEntry(ldapSyncConfig.getDirectoryName(),
                group, ldapSyncConfig.getBaseDC());
        ldapCrudDao.create(context, ldapSyncConfig);
    }

    public void removeMemberFromGroups(String memberDn, Set<String> groupDns, AcmLdapSyncConfig ldapSyncConfig)
            throws AcmLdapActionFailedException
    {
        Set<String> updatedGroupDns = new HashSet<>();

        for (String groupDn : groupDns)
        {
            log.debug("Remove Member with DN [{}] from Group with DN [{}] in LDAP", memberDn, groupDn);
            try
            {
                removeMemberFromGroup(memberDn, groupDn, ldapSyncConfig);
                updatedGroupDns.add(groupDn);
            }
            catch (Exception e)
            {
                log.debug("Removing member with DN [{}] from LDAP group(s) failed! Rollback changes on updated ldap groups", memberDn);
                updatedGroupDns.forEach(updatedGroupDn -> {
                    try
                    {
                        log.debug("Rollback changes for Group with DN [{}]", updatedGroupDn);
                        addMemberToGroup(memberDn, updatedGroupDn, ldapSyncConfig);
                    }
                    catch (Exception e1)
                    {
                        log.warn("Failed to rollback changes for Group with DN [{}]", updatedGroupDn, e1);
                    }
                });
                throw new AcmLdapActionFailedException("Removing members from LDAP Group failed", e);
            }
        }
    }

    public void addMemberToGroups(String memberDn, Set<String> groupDns, AcmLdapSyncConfig ldapSyncConfig)
            throws AcmLdapActionFailedException
    {
        Set<String> updatedGroupDns = new HashSet<>();

        for (String groupDn : groupDns)
        {
            log.debug("Add member with DN [{}] to Group with DN [{}] in LDAP", memberDn, groupDn);
            try
            {
                addMemberToGroup(memberDn, groupDn, ldapSyncConfig);
                updatedGroupDns.add(groupDn);
            }
            catch (Exception e)
            {
                log.debug("Adding member with DN [{}] to LDAP group(s) failed! Rollback changes on updated ldap groups", memberDn);
                updatedGroupDns.forEach(updatedGroupDn -> {
                    try
                    {
                        log.debug("Rollback changes for Group with DN [{}]", updatedGroupDn);
                        removeMemberFromGroup(memberDn, updatedGroupDn, ldapSyncConfig);
                    }
                    catch (Exception e1)
                    {
                        log.warn("Failed to rollback changes for Ldap Group with DN [{}]", updatedGroupDn, e1);
                    }
                });
                throw new AcmLdapActionFailedException("Updating LDAP Group with new member failed", e);
            }
        }
    }

    public void addMemberToGroup(String memberDn, String groupDn, AcmLdapSyncConfig ldapSyncConfig)
            throws AcmLdapActionFailedException
    {
        log.debug("Add member [{}] to Group [{}] in LDAP", memberDn, groupDn);
        DirContextOperations groupContext = ldapCrudDao.lookup(groupDn, ldapSyncConfig);
        groupContext.addAttributeValue("member", memberDn);
        ldapCrudDao.update(groupContext, ldapSyncConfig);
    }

    public void removeMemberFromGroup(String memberDn, String groupDn, AcmLdapSyncConfig ldapSyncConfig)
            throws AcmLdapActionFailedException
    {
        log.debug("Remove member [{}] from Group in LDAP", memberDn, groupDn);
        DirContextOperations groupContext = ldapCrudDao.lookup(groupDn, ldapSyncConfig);

        String[] members = groupContext.getStringAttributes("member");
        String member = Arrays.stream(members)
                .filter(m -> m.equalsIgnoreCase(memberDn))
                .findFirst()
                .orElse(null);
        if (member != null)
        {
            groupContext.removeAttributeValue("member", member);
            ldapCrudDao.update(groupContext, ldapSyncConfig);
        }
    }

    public void deleteGroupEntry(String dn, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        ldapCrudDao.delete(dn, ldapSyncConfig);
    }

    public void setLdapEntryTransformer(LdapEntryTransformer ldapEntryTransformer)
    {
        this.ldapEntryTransformer = ldapEntryTransformer;
    }

    public LdapCrudDao getLdapCrudDao()
    {
        return ldapCrudDao;
    }

    public void setLdapCrudDao(LdapCrudDao ldapCrudDao)
    {
        this.ldapCrudDao = ldapCrudDao;
    }
}
