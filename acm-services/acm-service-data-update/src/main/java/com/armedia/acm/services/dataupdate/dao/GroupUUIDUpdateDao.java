package com.armedia.acm.services.dataupdate.dao;

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

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

public class GroupUUIDUpdateDao
{
    private final Logger log = LogManager.getLogger(GroupUUIDUpdateDao.class);

    @PersistenceContext
    private EntityManager em;

    public List<AcmGroup> findAdHocGroupsWithUUIDByStatus(AcmGroupStatus status)
    {
        TypedQuery<AcmGroup> findQuery = em.createQuery("SELECT ag "
                + "FROM AcmGroup ag "
                + "WHERE ag.type = com.armedia.acm.services.users.model.group.AcmGroupType.ADHOC_GROUP "
                + "AND ag.status = :status "
                + "AND ag.name LIKE '%-UUID-%' "
                + "AND LENGTH(ag.name) > 42", AcmGroup.class);
        findQuery.setParameter("status", status);
        return findQuery.getResultList();
    }

    @Transactional
    public void deleteGroups(List<AcmGroup> groups)
    {
        groups.forEach(group -> {
            log.debug("Deleting AcmGroup [{}]", group.getName());
            Query deleteQuery = em.createQuery("DELETE FROM AcmGroup g WHERE g.name = :name");
            deleteQuery.setParameter("name", group.getName());
            deleteQuery.setFlushMode(FlushModeType.AUTO);
            deleteQuery.executeUpdate();
            em.detach(group);
        });
    }
}
