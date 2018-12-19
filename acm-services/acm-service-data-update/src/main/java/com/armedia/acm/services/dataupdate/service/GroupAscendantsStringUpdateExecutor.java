package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class GroupAscendantsStringUpdateExecutor implements AcmDataUpdateExecutor
{
    private AcmGroupDao groupDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private static final Logger log = LoggerFactory.getLogger(GroupAscendantsStringUpdateExecutor.class);

    @Override
    public String getUpdateId()
    {
        return "update-ascendants-list-string";
    }

    @Override
    @Transactional
    public void execute()
    {
        auditPropertyEntityAdapter.setUserId(AcmDataUpdateService.DATA_UPDATE_MODIFIER);

        log.info("Start executing {} update", getUpdateId());
        List<AcmGroup> acmGroups = groupDao.findAll();
        acmGroups.forEach(group -> {
            log.info("Recompute ascendants list string for group:[{}]", group.getName());
            String ascendantsListString = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
            group.setAscendantsList(ascendantsListString);
            groupDao.save(group);
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

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
