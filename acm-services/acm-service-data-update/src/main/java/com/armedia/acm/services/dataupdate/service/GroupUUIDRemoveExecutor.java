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

public class GroupUUIDRemoveExecutor implements AcmDataUpdateExecutor
{
    private AcmGroupDao groupDao;
    private AcmDataUpdateDao dataUpdateDao;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public String getUpdateId()
    {
        return "core-UUID-groupName-remove-update";
    }

    @Override
    @Transactional
    public void execute()
    {
        insertAdHocGroupsWithStrippedUUID();

        dataUpdateDao.updateUserMembershipForAdHocGroups();

        dataUpdateDao.updateGroupMembershipForAdHocGroups();

        int affectedRows = markInactiveActiveAdHocGroupsWithUUID();
        log.debug("[{}] AdHoc groups marked INACTIVE", affectedRows);

        List<AcmGroup> adHocGroups = groupDao.findByTypeAndStatus(AcmGroupType.ADHOC_GROUP, AcmGroupStatus.ACTIVE);
        adHocGroups.forEach(group -> {
            group.setAscendantsList(AcmGroupUtils.buildAncestorsStringForAcmGroup(group));
            log.debug("Set ascendants list for [{}] group", group.getName());
        });
    }

    @Transactional
    public void insertAdHocGroupsWithStrippedUUID()
    {
        List<AcmGroup> adHocGroups = dataUpdateDao.findAllActiveAdHocGroupsWithUUID();

        Set<AcmGroup> uniqueGroups = adHocGroups.stream()
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
    }

    private int markInactiveActiveAdHocGroupsWithUUID()
    {
        return dataUpdateDao.markInactiveActiveAdHocGroupsWithUUID();
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