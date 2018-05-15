package com.armedia.acm.service.objectlock.service;

/*-
 * #%L
 * ACM Service: Object lock
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

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.exception.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLockEvent;
import com.armedia.acm.service.objectlock.model.AcmObjectUnlockEvent;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockServiceImpl implements AcmObjectLockService, ApplicationEventPublisherAware
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmObjectLockDao acmObjectLockDao;
    private ExecuteSolrQuery executeSolrQuery;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public AcmObjectLock findLock(Long objectId, String objectType)
    {
        return acmObjectLockDao.findLock(objectId, objectType);
    }

    @Override
    @Transactional
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Authentication auth)
    {
        return createLock(objectId, objectType, lockType, Boolean.TRUE, auth);
    }

    @Override
    @Transactional
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Boolean lockInDB, Authentication auth)
    {

        log.debug("[{}] about to create object lock[objectId={}, objectType={}, lockType={}]", auth.getName(), objectId, objectType,
                lockType);
        AcmObjectLock existingLock = acmObjectLockDao.findLock(objectId, objectType);

        if (existingLock != null)
        {
            // if current user is same as creator of the lock than just return existingLock, else throw an exception
            if (existingLock.getCreator().equals(auth.getName()))
            {
                return existingLock;
            }
            else
            {
                log.warn(
                        "[{}] not able to create object lock[objectId={}, objectType={}, lockType={}]. Reason: Object lock already exists for: [{}]",
                        auth.getName(), objectId, objectType, lockType, existingLock.getCreator());
                throw new AcmObjectLockException("Lock already exist for different user.");
            }
        }

        AcmObjectLock ol = new AcmObjectLock();
        ol.setObjectId(objectId);
        ol.setObjectType(objectType);
        ol.setLockType(lockType);

        if (lockInDB)
        {
            log.info("Saving lock [{}] for object [{}:{}]", ol.getLockType(), ol.getObjectType(), ol.getObjectId());
            AcmObjectLock lock = acmObjectLockDao.save(ol);
            AcmObjectLockEvent event = new AcmObjectLockEvent(lock, auth.getName(), true);
            getApplicationEventPublisher().publishEvent(event);

            return lock;
        }
        else
        {
            return ol;
        }
    }

    @Override
    @Transactional
    public void removeLock(Long objectId, String objectType, String lockType, Authentication auth)
    {
        AcmObjectLock ol = acmObjectLockDao.findLock(objectId, objectType);

        if (ol == null)
        {
            // it is not an exception - the caller wanted the lock to be removed - and there is no lock to remove.
            // the object is no longer locked, so we have success.
            log.info("[{}] with id [{}] is already unlocked, no need to unlock it.", objectType, objectId);
            return;
        }

        acmObjectLockDao.remove(ol);

        ol.setLockType(lockType);

        AcmObjectUnlockEvent event = new AcmObjectUnlockEvent(ol, auth.getName(), true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public String getDocumentsWithoutLock(String objectType, Authentication auth, int firstRow, int maxRows, String sort, String fqParams)
            throws MuleException
    {
        StringBuilder query = new StringBuilder();
        query.append("-({!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK)");
        query.append(" AND ");
        query.append("object_type_s").append(":").append(objectType);
        log.debug("executing query for documents without lock: {}", query.toString());

        return executeQuery(query.toString(), auth, firstRow, maxRows, sort, fqParams);
    }

    @Override
    public String getDocumentsWithLock(String objectType, Authentication auth, String lockHeldByUser, int firstRow, int maxRows,
            String sort, String fqParams) throws MuleException
    {
        StringBuilder query = new StringBuilder();
        query.append("{!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK ");
        if (!StringUtils.isEmpty(objectType))
        {
            query.append(" AND ");
            query.append("parent_type_s").append(":").append(objectType);
        }
        if (lockHeldByUser != null && !StringUtils.isEmpty(lockHeldByUser))
        {
            query.append(" AND ");
            query.append("creator_lcs").append(":").append(lockHeldByUser);
        }
        log.debug("executing query for documents with lock: {}", query.toString());

        return executeQuery(query.toString(), auth, firstRow, maxRows, sort, fqParams);
    }

    @Override
    public String getObjectLocks(String parentObjectType, Authentication auth, String objectId, String creator, int firstRow, int maxRows,
            String sort, String fqParams) throws MuleException
    {
        StringBuilder query = new StringBuilder();
        query.append("object_type_s:OBJECT_LOCK");
        if (!StringUtils.isEmpty(parentObjectType))
        {
            query.append(" AND ");
            query.append("parent_type_s").append(":").append(parentObjectType);
        }

        if (objectId != null && !StringUtils.isEmpty(objectId))
        {
            query.append(" AND ");
            query.append("parent_id_s").append(":").append(objectId);
        }
        if (creator != null && !StringUtils.isEmpty(creator))
        {
            query.append(" AND ");
            query.append("creator_partial").append(":").append(creator);
        }
        return executeQuery(query.toString(), auth, firstRow, maxRows, sort, fqParams);
    }

    private String executeQuery(String query, Authentication auth, int firstRow, int maxRows, String sort, String fqParams)
            throws MuleException
    {
        if (!StringUtils.isEmpty(fqParams))
            return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, firstRow, maxRows,
                    sort, fqParams);
        else
            return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, firstRow, maxRows,
                    sort);
    }

    public void setAcmObjectLockDao(AcmObjectLockDao acmObjectLockDao)
    {
        this.acmObjectLockDao = acmObjectLockDao;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
