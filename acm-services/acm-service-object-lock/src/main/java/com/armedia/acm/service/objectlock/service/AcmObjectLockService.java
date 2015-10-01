package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.mule.api.MuleException;
import org.springframework.security.core.Authentication;

/**
 * Created by nebojsha on 25.08.2015.
 */
public interface AcmObjectLockService
{
    AcmObjectLock createLock(Long objectId, String objectType, Authentication auth);

    void removeLock(Long objectId, String objectType);

    /**
     * queries documents which doesn't have lock.
     *
     * @param objectType document object type. Can be null, than returns every document.
     * @param auth       Authentication
     * @param firstRow   start row
     * @param maxRows    max rows
     * @param sort       name of the fields with (asc|desc) separated by semicolon. Can be empty.
     * @param fqParams   additional filter. Can be null.
     * @return return response from solr as String
     * @throws MuleException
     */
    String getDocumentsWithoutLock(String objectType, Authentication auth, int firstRow, int maxRows, String sort, String fqParams) throws MuleException;

    /**
     * queries documents which doesn't have lock.
     *
     * @param objectType       document object type. Can be null, than returns every document.
     * @param auth             Authentication
     * @param lockHeldByUser   filter locks by lock held by user. Can be null.
     * @param firstRow         start row
     * @param maxRows          max rows
     * @param sort             name of the fields with (asc|desc) separated by semicolon. Can be empty.
     * @param fqParams         additional filter. Can be null.
     * @return return response from solr as String
     * @throws MuleException
     */
    String getDocumentsWithLock(String objectType, Authentication auth, Authentication lockHeldByUser, int firstRow, int maxRows, String sort, String fqParams) throws MuleException;
}
