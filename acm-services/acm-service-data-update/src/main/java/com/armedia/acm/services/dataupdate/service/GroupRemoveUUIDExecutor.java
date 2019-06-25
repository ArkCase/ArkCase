package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.dataupdate.dao.GroupUUIDUpdateDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Specific implementation of {@link AcmDataUpdateExecutor} which will insert all
 * ADHOC groups as new groups with removed UUID string and invalidate old ones.
 */
public class GroupRemoveUUIDExecutor implements AcmDataUpdateExecutor
{
    private final Logger log = LogManager.getLogger(getClass());
    private AcmGroupDao groupDao;
    private GroupUUIDUpdateDao uuidUpdateDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public String getUpdateId()
    {
        return "core-remove-UUID-update";
    }

    @Override
    @Transactional
    public void execute()
    {
        auditPropertyEntityAdapter.setUserId(AcmDataUpdateService.DATA_UPDATE_MODIFIER);

        List<AcmGroup> invalidGroups = groupDao.findByStatusAndType(AcmGroupStatus.DELETE, AcmGroupType.LDAP_GROUP);

        invalidGroups.addAll(groupDao.findByStatusAndType(AcmGroupStatus.INACTIVE, AcmGroupType.LDAP_GROUP));

        invalidateGroups(invalidGroups);

        uuidUpdateDao.deleteGroups(invalidGroups);

        List<AcmGroup> groupsWithUUID = uuidUpdateDao.findAdHocGroupsWithUUIDByStatus(AcmGroupStatus.ACTIVE);

        Set<AcmGroup> newGroups = insertAdHocGroupsWithRemovedUUID(groupsWithUUID);

        Map<String, AcmGroup> newGroupsByName = getGroupsPerName(newGroups);

        updateGroupMembershipForAdHocGroups(groupsWithUUID, newGroupsByName);

        invalidateGroups(groupsWithUUID);

        setAscendantsList(newGroups);
    }

    private Set<AcmGroup> insertAdHocGroupsWithRemovedUUID(List<AcmGroup> groups)
    {
        Set<AcmGroup> uniqueGroups = groups.stream()
                .map(group -> {
                    String name = truncateUUID(group.getName());
                    AcmGroup newGroup = new AcmGroup();
                    newGroup.setName(name);
                    newGroup.setSupervisor(group.getSupervisor());
                    newGroup.setDescription(group.getDescription());
                    newGroup.setDistinguishedName(group.getDistinguishedName());
                    newGroup.setType(group.getType());
                    newGroup.setStatus(AcmGroupStatus.ACTIVE);
                    newGroup.setDirectoryName(group.getDirectoryName());
                    newGroup.setDisplayName(name);
                    newGroup.setCreator(group.getCreator());
                    newGroup.setCreated(group.getCreated());
                    newGroup.setUserMembers(new HashSet<>(group.getUserMembers(true)));
                    return newGroup;
                })
                .collect(Collectors.toSet());

        log.debug("Insert [{}] new AdHoc groups for old active ones with UUIDs", uniqueGroups.size());
        uniqueGroups.forEach(group -> {
            log.debug("Insert [{}]", group.getName());
            groupDao.save(group);
        });
        groupDao.getEm().flush();
        return uniqueGroups;
    }

    private void setAscendantsList(Set<AcmGroup> newGroups)
    {
        newGroups.forEach(group -> {
            String ascendantsListString = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
            group.setAscendantsList(ascendantsListString);
            log.debug("Set ascendants list string [{}] for group [{}]", ascendantsListString, group.getName());
            groupDao.save(group);
        });
    }

    private void invalidateGroups(List<AcmGroup> invalidGroups)
    {
        invalidGroups.forEach(group -> {
            group.setModifier(AcmDataUpdateService.DATA_UPDATE_MODIFIER);
            group.setModified(new Date());
            group.getMemberGroups().clear();
            group.getMemberOfGroups().clear();
            group.getUserMembers(false).clear();
            group.setAscendantsList(null);
            group.setStatus(AcmGroupStatus.INACTIVE);
            group.setDistinguishedName(null);
            group.setDirectoryName(null);
            log.debug("Invalidate group [{}]", group.getName());
            groupDao.save(group);
        });
        groupDao.getEm().flush();
    }

    private void updateGroupMembershipForAdHocGroups(List<AcmGroup> groupsWithUUID, Map<String, AcmGroup> newGroupsByName)
    {
        List<AcmGroup> groupsToUpdate = new ArrayList<>();

        groupsWithUUID.forEach(group -> {
            String name = truncateUUID(group.getName());
            if (newGroupsByName.containsKey(name))
            {
                AcmGroup newGroup = newGroupsByName.get(name);
                Set<AcmGroup> memberGroups = group.getMemberGroups();
                memberGroups.forEach(memberGroup -> {
                    String memberGroupName = truncateUUID(memberGroup.getName());
                    if (newGroupsByName.containsKey(memberGroupName))
                    {
                        newGroup.addGroupMember(newGroupsByName.get(memberGroupName));
                    }
                });
                groupsToUpdate.add(newGroup);
            }
        });

        groupsToUpdate.stream()
                .filter(group -> !group.getMemberGroups().isEmpty())
                .forEach(group -> {
                    log.debug("Save group membership for group [{}]", group.getName());
                    groupDao.save(group);
                });
    }

    private String truncateUUID(String name)
    {
        return StringUtils.substringBeforeLast(name, "-UUID-");
    }

    private Map<String, AcmGroup> getGroupsPerName(Set<AcmGroup> groups)
    {
        return groups.stream()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public void setUuidUpdateDao(GroupUUIDUpdateDao uuidUpdateDao)
    {
        this.uuidUpdateDao = uuidUpdateDao;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
