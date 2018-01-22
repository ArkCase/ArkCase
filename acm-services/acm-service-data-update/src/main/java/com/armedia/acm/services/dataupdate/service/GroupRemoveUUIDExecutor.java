package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.dataupdate.dao.GroupUUIDUpdateDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
    private AcmGroupDao groupDao;
    private GroupUUIDUpdateDao uuidUpdateDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private final Logger log = LoggerFactory.getLogger(getClass());

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

        List<AcmGroup> deletedGroups = groupDao.findByStatusAndType(AcmGroupStatus.DELETE, AcmGroupType.LDAP_GROUP);

        invalidateGroups(deletedGroups);

        List<AcmGroup> groupsWithUUID = uuidUpdateDao.findAdHocGroupsWithUUIDByStatus(AcmGroupStatus.ACTIVE);

        Set<AcmGroup> newGroups = insertAdHocGroupsWithRemovedUUID(groupsWithUUID);

        Map<String, AcmGroup> newGroupsByName = getGroupsPerName(newGroups);

        updateGroupMembershipForAdHocGroups(groupsWithUUID, newGroupsByName);

        updateUserMembershipForAdHocGroups(groupsWithUUID, newGroupsByName);

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
                    return newGroup;
                })
                .collect(Collectors.toSet());

        log.debug("Insert [{}] new AdHoc groups for old active ones with UUIDs", uniqueGroups.size());
        uniqueGroups.forEach(group -> {
            log.debug("Insert [{}]", group.getName());
            groupDao.save(group);
        });
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
            group.getUserMembers().clear();
            group.setAscendantsList(null);
            group.setStatus(AcmGroupStatus.INACTIVE);
            groupDao.save(group);
            log.debug("Invalidate group [{}]", group.getName());
        });
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

    @Transactional
    public void updateUserMembershipForAdHocGroups(List<AcmGroup> groupsWithUUID, Map<String, AcmGroup> newGroupsByName)
    {
        groupsWithUUID.forEach(group -> {
            String name = truncateUUID(group.getName());
            if (newGroupsByName.containsKey(name))
            {
                AcmGroup newGroup = newGroupsByName.get(name);
                Set<AcmUser> userMembers = group.getUserMembers();
                if (!userMembers.isEmpty())
                {
                    newGroup.setUserMembers(userMembers);
                    log.debug("Saving [{}] user members for group [{}]", newGroup.getUserMemberIds(), newGroup.getName());
                    groupDao.save(newGroup);
                }
            }
        });
    }

    private String truncateUUID(String name)
    {
        return StringUtils.substringBeforeLast(name, "-UUID-").toUpperCase();
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