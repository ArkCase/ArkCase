package com.armedia.acm.services.users.service.group;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.AcmGroupEventPublisher;
import com.armedia.acm.services.users.service.ldap.LdapEntryTransformer;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

public class LdapGroupService
{
    private GroupService groupService;

    private SpringLdapGroupDao ldapGroupDao;

    private UserDao userDao;

    private LdapEntryTransformer ldapEntryTransformer;

    private SpringContextHolder acmContextHolder;

    private AcmGroupEventPublisher acmGroupEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @Transactional(rollbackFor = Exception.class)
    public AcmGroup createLdapGroup(AcmGroup group, String directoryName) throws AcmLdapActionFailedException,
            AcmObjectNotFoundException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        if (StringUtils.isNotBlank(ldapSyncConfig.getGroupControlGroup()))
        {
            return createLdapSubgroup(group, ldapSyncConfig.getGroupControlGroup(), directoryName);
        }

        String groupName = MapperUtils.buildGroupName(group.getName(), ldapSyncConfig);

        AcmGroup existingGroup = groupService.findByName(groupName);
        if (existingGroup != null && existingGroup.getStatus() == AcmGroupStatus.ACTIVE)
        {
            log.debug("Group with name: [{}] already exists!", group.getName());
            throw new NameAlreadyBoundException(null);
        }

        String groupDN = buildDnForGroup(groupName, ldapSyncConfig);

        group.setName(groupName);
        group.setDisplayName(groupName);
        group.setType(AcmGroupType.LDAP_GROUP);
        group.setDescription(group.getDescription());
        group.setDistinguishedName(groupDN);
        group.setDirectoryName(directoryName);
        group.setStatus(AcmGroupStatus.ACTIVE);
        log.debug("Saving Group [{}] with DN [{}] in database", group.getName(), group.getDistinguishedName());
        groupService.saveAndFlush(group);

        ldapGroupDao.createGroup(group, ldapSyncConfig);
        log.debug("Group [{}] with DN [{}] saved in DB and LDAP", group.getName(), group.getDistinguishedName());

        acmGroupEventPublisher.publishLdapGroupCreatedEvent(group);
        return group;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmGroup createLdapSubgroup(AcmGroup group, String parentGroupName, String directoryName)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        AcmLdapSyncConfig ldapSyncConfig = acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));
        Set<String> memberOfGroups = new HashSet<>();

        String givenName = group.getName();
        String groupName = MapperUtils.buildGroupName(givenName, ldapSyncConfig);

        AcmGroup existingGroup = groupService.findByName(groupName);
        if (existingGroup != null && existingGroup.getStatus() == AcmGroupStatus.ACTIVE)
        {
            log.debug("Group with name [{}] already exists!", groupName);
            throw new NameAlreadyBoundException(null);
        }

        AcmGroup parentGroup = groupService.findByName(parentGroupName);
        if (parentGroup == null)
        {
            throw new AcmObjectNotFoundException("LDAP_GROUP", null, "Parent group not found");
        }
        log.debug("Found parent group [{}] for new LDAP sub-group [{}]", parentGroup.getName(), group.getName());
        memberOfGroups.add(parentGroup.getDistinguishedName());

        AcmGroup acmGroup;
        String groupDN = buildDnForGroup(givenName, ldapSyncConfig);
        if (existingGroup == null)
        {
            acmGroup = new AcmGroup();
            acmGroup.setName(groupName);
        }
        else
        {
            existingGroup.setStatus(AcmGroupStatus.ACTIVE);
            acmGroup = existingGroup;
        }
        acmGroup.setDisplayName(groupName);
        acmGroup.setType(AcmGroupType.LDAP_GROUP);
        acmGroup.setDescription(group.getDescription());
        acmGroup.setDistinguishedName(groupDN);
        acmGroup.setDirectoryName(directoryName);

        Set<String> ancestors = parentGroup.getAscendants();
        ancestors.add(parentGroupName);
        acmGroup.setAscendantsList(AcmGroupUtils.getAscendantsString(ancestors));
        parentGroup.addGroupMember(acmGroup);

        String groupsControlGroupName = ldapSyncConfig.getGroupControlGroup();
        if (StringUtils.isNotBlank(groupsControlGroupName) && !parentGroupName.equals(groupsControlGroupName))
        {
            AcmGroup groupsControlGroup = groupService.findByName(groupsControlGroupName);
            acmGroup.addAscendant(groupsControlGroupName);
            groupsControlGroup.addGroupMember(acmGroup);
            log.debug("Updated groups control group [{}] with sub-group [{}] in database",
                    groupsControlGroup.getName(), acmGroup.getName());
            groupService.saveAndFlush(groupsControlGroup);
            memberOfGroups.add(groupsControlGroup.getDistinguishedName());
        }

        log.debug("Updated parent-group [{}] with sub-group [{}] in database", parentGroup.getName(), acmGroup.getName());
        groupService.saveAndFlush(parentGroup);

        log.debug("Saving sub-group [{}] with parent groups [{}] in LDAP server", acmGroup.getDistinguishedName(), memberOfGroups);
        ldapGroupDao.createGroup(acmGroup, ldapSyncConfig);
        log.debug("Sub-group [{}] with DN [{}] saved in LDAP server", acmGroup.getName(), acmGroup.getDistinguishedName());

        try
        {
            log.debug("Update parent groups with the new member [{}] in LDAP server", acmGroup.getDistinguishedName());
            ldapGroupDao.addMemberToGroups(acmGroup.getDistinguishedName(), memberOfGroups, ldapSyncConfig);
        }
        catch (AcmLdapActionFailedException e)
        {
            log.error("Updating parent-group DN [{}] failed! Rollback saved sub-group DN [{}] ",
                    parentGroup.getDistinguishedName(), acmGroup.getDistinguishedName());
            ldapGroupDao.deleteGroupEntry(acmGroup.getDistinguishedName(), ldapSyncConfig);
            throw e;
        }
        acmGroupEventPublisher.publishLdapGroupCreatedEvent(acmGroup);
        return acmGroup;
    }

    @Transactional(rollbackFor = Exception.class)
    public AcmGroup deleteLdapGroup(String group, String directoryName)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException
    {
        log.debug("Deleting LDAP group [{}]", group);
        AcmGroup markedGroup = groupService.markGroupDeleted(group, true);
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);
        ldapGroupDao.deleteGroupEntry(markedGroup.getDistinguishedName(), ldapSyncConfig);
        acmGroupEventPublisher.publishLdapGroupDeletedEvent(markedGroup);
        return markedGroup;
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGroupMembership(String groupName, String parentGroupName, String directoryName)
            throws AcmObjectNotFoundException, AcmLdapActionFailedException
    {
        AcmLdapSyncConfig ldapSyncConfig = getLdapSyncConfig(directoryName);

        AcmGroup acmGroup = groupService.removeGroupMembership(groupName, parentGroupName, true);
        AcmGroup parentGroup = groupService.findByName(parentGroupName);

        if (acmGroup.getStatus() == AcmGroupStatus.DELETE)
        {
            ldapGroupDao.deleteGroupEntry(acmGroup.getDistinguishedName(), ldapSyncConfig);
        }
        else
        {
            ldapGroupDao.removeMemberFromGroup(acmGroup.getDistinguishedName(), parentGroup.getDistinguishedName(), ldapSyncConfig);
        }
    }

    private AcmLdapSyncConfig getLdapSyncConfig(String directoryName)
    {
        return acmContextHolder.getAllBeansOfType(AcmLdapSyncConfig.class).get(String.format("%s_sync", directoryName));
    }

    private String buildDnForGroup(String cn, AcmLdapSyncConfig ldapSyncConfig)
    {
        String cnRdn = String.format("cn=%s", cn);
        return MapperUtils.appendToDn(cnRdn, ldapSyncConfig.getGroupSearchBase(), ldapSyncConfig.getBaseDC());
    }

    public String getControlGroup(String directory)
    {
        return getLdapSyncConfig(directory).getGroupControlGroup();
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    public void setGroupService(GroupService groupService)
    {
        this.groupService = groupService;
    }

    public SpringLdapGroupDao getLdapGroupDao()
    {
        return ldapGroupDao;
    }

    public void setLdapGroupDao(SpringLdapGroupDao ldapGroupDao)
    {
        this.ldapGroupDao = ldapGroupDao;
    }

    public SpringContextHolder getAcmContextHolder()
    {
        return acmContextHolder;
    }

    public void setAcmContextHolder(SpringContextHolder acmContextHolder)
    {
        this.acmContextHolder = acmContextHolder;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public LdapEntryTransformer getLdapEntryTransformer()
    {
        return ldapEntryTransformer;
    }

    public void setLdapEntryTransformer(LdapEntryTransformer ldapEntryTransformer)
    {
        this.ldapEntryTransformer = ldapEntryTransformer;
    }

    public void setAcmGroupEventPublisher(AcmGroupEventPublisher acmGroupEventPublisher)
    {
        this.acmGroupEventPublisher = acmGroupEventPublisher;
    }
}
