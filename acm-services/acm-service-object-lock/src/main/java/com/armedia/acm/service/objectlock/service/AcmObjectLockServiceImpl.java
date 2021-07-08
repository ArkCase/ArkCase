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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_TYPE_S;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLockEvent;
import com.armedia.acm.service.objectlock.model.AcmObjectUnlockEvent;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockServiceImpl implements AcmObjectLockService, ApplicationEventPublisherAware
{

    private Logger log = LogManager.getLogger(getClass());

    private AcmObjectLockDao acmObjectLockDao;
    private ExecuteSolrQuery executeSolrQuery;
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public AcmObjectLock findLock(Long objectId, String objectType)
    {
        return acmObjectLockDao.findLock(objectId, objectType);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Authentication auth)
    {
        return createLockInternal(objectId, objectType, lockType, expiry, Boolean.TRUE, auth.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB, String userId)
    {
        return createLockInternal(objectId, objectType, lockType, expiry, lockInDB, userId);
    }

    private AcmObjectLock createLockInternal(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB,
            String userId)
    {
        log.debug("[{}] about to create object lock[objectId={}, objectType={}, lockType={}]", userId, objectId, objectType,
                lockType);

        AcmObjectLock objectLock = null;

        AcmObjectLock existingLock = acmObjectLockDao.findLock(objectId, objectType);

        if (existingLock != null)
        {
            objectLock = existingLock;
        }
        else
        {
            objectLock = new AcmObjectLock();
            objectLock.setObjectId(objectId);
            objectLock.setObjectType(objectType);
        }

        objectLock.setCreated(new Date());
        objectLock.setCreator(userId);
        objectLock.setLockType(lockType);
        objectLock.setModifier(userId);

        objectLock.setExpiry(new Date(objectLock.getCreated().getTime() + expiry));

        if (lockInDB)
        {
            log.info("Saving lock [{}] for object [{}:{}]", objectLock.getLockType(), objectLock.getObjectType(), objectLock.getObjectId());
            AcmObjectLock lock = acmObjectLockDao.save(objectLock);
            AcmObjectLockEvent event = new AcmObjectLockEvent(lock, userId, true, AuthenticationUtils.getUserIpAddress());
            getApplicationEventPublisher().publishEvent(event);

            return lock;
        }
        else
        {
            return objectLock;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, String userId)
    {
        return createLockInternal(objectId, objectType, lockType, expiry, Boolean.TRUE, userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB, Authentication auth)
    {
        return createLockInternal(objectId, objectType, lockType, expiry, lockInDB, auth.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void removeLock(Long objectId, String objectType, String lockType, String userId)
    {
        removeLockInternal(objectId, objectType, lockType, userId);
    }

    private void removeLockInternal(Long objectId, String objectType, String lockType, String userId)
    {
        AcmObjectLock objectLock = acmObjectLockDao.findLock(objectId, objectType);

        if (objectLock == null)
        {
            // it is not an exception - the caller wanted the lock to be removed - and there is no lock to remove.
            // the object is no longer locked, so we have success.
            log.info("[{}] with id [{}] is already unlocked, no need to unlock it.", objectType, objectId);
            return;
        }

        if (!objectLock.getLockType().equals(lockType))
        {
            log.info("[{}] with id [{}] does not have lock of type [{}] , no need to unlock it.", objectType, objectId, lockType);
            return;
        }

        if (!objectLock.getCreator().equals(userId))
        {
            log.info("[{}] with id [{}] is not locked by user [{}], no need to unlock it.", objectType, objectId, userId);
            return;
        }

        acmObjectLockDao.remove(objectLock);

        AcmObjectUnlockEvent event = new AcmObjectUnlockEvent(objectLock, userId, true, AuthenticationUtils.getUserIpAddress());
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void removeLock(Long objectId, String objectType, String lockType, Authentication auth)
    {
        removeLockInternal(objectId, objectType, lockType, auth.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void removeExpiredLocks()
    {
        acmObjectLockDao.getExpiredLocks().forEach(objectLock -> acmObjectLockDao.remove(objectLock));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void removeLock(AcmObjectLock objectLock)
    {
        try
        {
            acmObjectLockDao.remove(objectLock);
        }
        catch (Exception e)
        {
            log.warn("Lock can't be removed. Reason: [{}]", e.getMessage());
        }
    }

    @Override
    public String getDocumentsWithoutLock(String objectType, Authentication auth, int firstRow, int maxRows, String sort, String fqParams)
            throws SolrException
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
            String sort, String fqParams) throws SolrException
    {
        StringBuilder query = new StringBuilder();
        query.append("{!join from=parent_ref_s to=id}object_type_s:OBJECT_LOCK ");
        if (!StringUtils.isEmpty(objectType))
        {
            query.append(" AND ");
            query.append(PARENT_TYPE_S).append(":").append(objectType);
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
            String sort, String fqParams) throws SolrException
    {
        StringBuilder query = new StringBuilder();
        query.append("object_type_s:OBJECT_LOCK");
        if (!StringUtils.isEmpty(parentObjectType))
        {
            query.append(" AND ");
            query.append(PARENT_TYPE_S).append(":").append(parentObjectType);
        }

        if (objectId != null && !StringUtils.isEmpty(objectId))
        {
            query.append(" AND ");
            query.append(PARENT_ID_S).append(":").append(objectId);
        }
        if (creator != null && !StringUtils.isEmpty(creator))
        {
            query.append(" AND ");
            query.append("creator_partial").append(":").append(creator);
        }
        return executeQuery(query.toString(), auth, firstRow, maxRows, sort, fqParams);
    }

    private String executeQuery(String query, Authentication auth, int firstRow, int maxRows, String sort, String fqParams)
            throws SolrException
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
