package com.armedia.acm.service.objectlock.service;

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
            } else
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
            AcmObjectLock lock = acmObjectLockDao.save(ol);
            AcmObjectLockEvent event = new AcmObjectLockEvent(lock, auth.getName(), true);
            getApplicationEventPublisher().publishEvent(event);

            return lock;
        } else
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
            throw new AcmObjectLockException(
                    "Error removing. Lock for [objectId, objectType] = [" + objectId + ", " + objectType + "] doesn't exists!");

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
    public String getObjectLocks(String parentObjectType, Authentication auth, String lockHeldByUser, int firstRow, int maxRows,
                                 String sort, String fqParams) throws MuleException
    {
        StringBuilder query = new StringBuilder();
        query.append("object_type_s:OBJECT_LOCK");
        if (!StringUtils.isEmpty(parentObjectType))
        {
            query.append(" AND ");
            query.append("parent_type_s").append(":").append(parentObjectType);
        }
        if (lockHeldByUser != null && !StringUtils.isEmpty(lockHeldByUser))
        {
            query.append(" AND ");
            query.append("creator_lcs").append(":").append(lockHeldByUser);
        }
        log.debug("executing query for object locks: {}", query.toString());

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
