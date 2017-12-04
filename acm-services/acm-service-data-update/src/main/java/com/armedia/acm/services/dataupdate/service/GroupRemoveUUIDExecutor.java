package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.dataupdate.dao.AcmDataUpdateDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupRemoveUUIDExecutor implements AcmDataUpdateExecutor
{
    private AcmGroupDao groupDao;
    private AcmDataUpdateDao dataUpdateDao;

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
        List<AcmGroup> deletedGroups = groupDao.findByStatusAndType(AcmGroupStatus.DELETE, AcmGroupType.LDAP_GROUP);

        invalidateGroups(deletedGroups);

        int rows = dataUpdateDao.deleteLdapInvalidGroups();
        log.debug("[{}] groups were deleted", rows);

        List<AcmGroup> groupsWithUUID = dataUpdateDao.findAdHocGroupsWithUUIDByStatus(AcmGroupStatus.ACTIVE);

        Set<AcmGroup> newGroups = insertAdHocGroupsWithStrippedUUID(groupsWithUUID);

        dataUpdateDao.updateUserMembershipForAdHocGroups();

        dataUpdateDao.updateGroupMembershipForAdHocGroups();

        setAscendantsList(newGroups);

        invalidateGroups(groupsWithUUID);
    }

    private Set<AcmGroup> insertAdHocGroupsWithStrippedUUID(List<AcmGroup> groups)
    {
        Set<AcmGroup> uniqueGroups = groups.stream()
                .map(group -> {
                    String name = StringUtils.substringBeforeLast(group.getName(), "-UUID-");
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
                    newGroup.setModified(new Date());
                    newGroup.setModifier(AcmDataUpdateService.DATA_UPDATE_MODIFIER);
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
            group.setAscendantsList(AcmGroupUtils.buildAncestorsStringForAcmGroup(group));
            log.debug("Set ascendants list for group [{}]", group.getName());
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

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public void setDataUpdateDao(AcmDataUpdateDao dataUpdateDao)
    {
        this.dataUpdateDao = dataUpdateDao;
    }
}