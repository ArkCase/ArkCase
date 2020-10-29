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
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class GroupAscendantsStringUpdateExecutor implements AcmDataUpdateExecutor
{
    private AcmGroupDao groupDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private static final Logger log = LogManager.getLogger(GroupAscendantsStringUpdateExecutor.class);

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
