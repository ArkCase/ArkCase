package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GroupAscendantsStringUpdateExecutor implements AcmDataUpdateExecutor
{
    private AcmGroupDao groupDao;
    private static final Logger log = LoggerFactory.getLogger(GroupAscendantsStringUpdateExecutor.class);

    @Override
    public String getUpdateId()
    {
        return "update-ascendants-list-string";
    }

    @Override
    public void execute()
    {
        log.info("Start executing {} update", getUpdateId());
        List<AcmGroup> acmGroups = groupDao.findAll();
        acmGroups.forEach(group -> {
            log.info("Recompute ascendants list string for group:[{}]", group.getName());
            String ascendantsListString = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
            group.setAscendantsList(ascendantsListString);
        });
    }

    public AcmGroupDao getGroupDao()
    {
        return groupDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }
}
