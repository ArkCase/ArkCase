package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import java.util.List;

/**
 * Created by nebojsha on 25.08.2015.
 */
public interface AcmObjectLockService {
    AcmObjectLock createLock(Long objectId, String objectType);

    void removeLock(Long objectId, String objectType);

    List<AcmObjectLock> getAllLocksByType(String objectType);
}
