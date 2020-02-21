package com.armedia.acm.objectchangestatus.service;

/*-
 * #%L
 * ACM Service: Object Change Status
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

/**
 * @author riste.tutureski
 */
public class ChangeObjectStatusServiceImpl implements ChangeObjectStatusService
{

    private final Logger log = LogManager.getLogger(getClass());
    private AcmDataService acmDataService;
    private UserTrackerService userTrackerService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void change(Long objectId, String objectType, String status)
    {
        log.debug("Changing object status: type [{}], id [{}], new status: [{}]",
                objectType, objectId, status);

        AcmAbstractDao<AcmObject> dao = getAcmDataService().getDaoByObjectType(objectType);

        if (dao != null)
        {
            AcmStatefulEntity entity = getAcmStatefulEntity(dao, objectId);
            if (entity != null)
            {
                setStatusAndSaveEntity(dao, entity, status);
            }
        }
    }

    @Override
    public void changeIfNoPermanentStatusIsSet(Long objectId, String objectType, String status, String permanentStatus)
    {
        log.debug("Changing object status: type [{}], id [{}], new status: [{}]",
                objectType, objectId, status);

        AcmAbstractDao<AcmObject> dao = getAcmDataService().getDaoByObjectType(objectType);

        if (dao != null)
        {
            AcmStatefulEntity entity = getAcmStatefulEntity(dao, objectId);
            if (entity != null && !entity.getStatus().equals(permanentStatus))
            {
                setStatusAndSaveEntity(dao, entity, status);
            }
        }
    }

    private AcmStatefulEntity getAcmStatefulEntity(AcmAbstractDao<AcmObject> dao, Long objectId)
    {
        AcmStatefulEntity entity;
        try
        {
            entity = (AcmStatefulEntity) dao.find(objectId);
        }
        catch (EntityNotFoundException e)
        {
            // try and flush our SQL in case we are trying to operate on a brand new object
            entityManager.flush();
            entity = (AcmStatefulEntity) dao.find(objectId);
        }
        return entity;
    }

    private void setStatusAndSaveEntity(AcmAbstractDao<AcmObject> dao, AcmStatefulEntity entity, String status)
    {
        log.debug("Found object of type [{}], setting status to [{}]", entity.getClass().getName(), status);
        entity.setStatus(status);
        dao.save((AcmObject) entity);

        // now we have to flush our changes so other objects in a workflow will see our changes.
        entityManager.flush();
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }
}
