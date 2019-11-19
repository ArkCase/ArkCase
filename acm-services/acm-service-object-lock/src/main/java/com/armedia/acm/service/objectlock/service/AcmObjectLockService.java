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

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.services.search.exception.SolrException;

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
     * @throws SolrException
     */
    String getDocumentsWithoutLock(String objectType, Authentication auth, int firstRow, int maxRows, String sort, String fqParams)
            throws SolrException;

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
     * @throws SolrException
     */
    String getDocumentsWithLock(String objectType, Authentication auth, String lockHeldByUser, int firstRow, int maxRows, String sort,
            String fqParams) throws SolrException;

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
     * @throws SolrException
     */
    String getObjectLocks(String parentObjectType, Authentication auth, String objectId, String creator, int firstRow, int maxRows,
            String sort,
            String fqParams) throws SolrException;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    void removeExpiredLocks();

    /**
     * removes given lock. If lock is not found will not throw any Exception, transaction will not fail.
     *
     * @param objectLock
     *            instance of AcmObjectLock
     */
    void removeLock(AcmObjectLock objectLock);
}
