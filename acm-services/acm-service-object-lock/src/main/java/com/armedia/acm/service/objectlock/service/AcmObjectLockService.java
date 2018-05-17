package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nebojsha on 25.08.2015.
 */
public interface AcmObjectLockService
{
    AcmObjectLock findLock(Long objectId, String objectType);

    @Deprecated
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB, Authentication auth);

    /**
     * This version of createLock is to maintain backwards compatibility with the method that takes a "LockInDB"
     * parameter. It calls the new method with a value of TRUE for lockInDB.
     *
     * @param objectId
     * @param objectType
     * @param lockType
     * @param auth
     * @return
     */
    AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Authentication auth);

    @Deprecated
    AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, Boolean lockInDB, String userId);

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    AcmObjectLock createLock(Long objectId, String objectType, String lockType, Long expiry, String userId);

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    void removeLock(Long objectId, String objectType, String lockType, Authentication auth);

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    void removeLock(Long objectId, String objectType, String lockType, String userId);

    /**
     * queries documents which doesn't have lock.
     *
     * @param objectType
     *            document object type. Can be null, than returns every document.
     * @param auth
     *            Authentication
     * @param firstRow
     *            start row
     * @param maxRows
     *            max rows
     * @param sort
     *            name of the fields with (asc|desc) separated by semicolon. Can be empty.
     * @param fqParams
     *            additional filter. Can be null.
     * @return return response from solr as String
     * @throws MuleException
     */
    String getDocumentsWithoutLock(String objectType, Authentication auth, int firstRow, int maxRows, String sort, String fqParams)
            throws MuleException;

    /**
     * queries documents which have lock.
     *
     * @param objectType
     *            document object type. Can be null, than returns every document.
     * @param auth
     *            Authentication
     * @param lockHeldByUser
     *            filter locks by lock held by user. Can be null.
     * @param firstRow
     *            start row
     * @param maxRows
     *            max rows
     * @param sort
     *            name of the fields with (asc|desc) separated by semicolon. Can be empty.
     * @param fqParams
     *            additional filter. Can be null.
     * @return return response from solr as String
     * @throws MuleException
     */
    String getDocumentsWithLock(String objectType, Authentication auth, String lockHeldByUser, int firstRow, int maxRows, String sort,
            String fqParams) throws MuleException;

    /**
     * queries documents which holders for object lock
     *
     * @param parentObjectType
     *            parent document object type. Can be null, than returns every document.
     * @param auth
     *            Authentication
     * @param objectId
     *            filter locks by object id.
     * @param creator
     *            filter locks by lock held by user. Can be null.
     * @param firstRow
     *            start row
     * @param maxRows
     *            max rows
     * @param sort
     *            name of the fields with (asc|desc) separated by semicolon. Can be empty.
     * @param fqParams
     *            additional filter. Can be null.
     * @return return response from solr as String
     * @throws MuleException
     */
    String getObjectLocks(String parentObjectType, Authentication auth, String objectId, String creator, int firstRow, int maxRows,
            String sort,
            String fqParams) throws MuleException;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    void removeExpiredLocks();

}
