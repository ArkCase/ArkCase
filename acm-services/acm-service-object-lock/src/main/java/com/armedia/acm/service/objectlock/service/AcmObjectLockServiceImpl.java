package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Authentication auth)
    {
        return createLock(objectId, objectType, lockType, expiry, Boolean.TRUE, auth.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB, String userId)
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

        objectLock.setExpiry(new Date(objectLock.getCreated().getTime() + expiry));

        if (lockInDB)
        {
            log.info("Saving lock [{}] for object [{}:{}]", objectLock.getLockType(), objectLock.getObjectType(), objectLock.getObjectId());
            AcmObjectLock lock = acmObjectLockDao.save(objectLock);
            AcmObjectLockEvent event = new AcmObjectLockEvent(lock, userId, true);
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
        return createLock(objectId, objectType, lockType, expiry, Boolean.TRUE, userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB, Authentication auth)
    {
        return createLock(objectId, objectType, lockType, expiry, lockInDB, auth.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void removeLock(Long objectId, String objectType, String lockType, String userId)
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

        AcmObjectUnlockEvent event = new AcmObjectUnlockEvent(objectLock, userId, true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void removeLock(Long objectId, String objectType, String lockType, Authentication auth)
    {
        removeLock(objectId, objectType, lockType, auth.getName());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void removeExpiredLocks()
    {
        acmObjectLockDao.getExpiredLocks().forEach(objectLock -> acmObjectLockDao.remove(objectLock));
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
