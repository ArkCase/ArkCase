package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.springframework.security.core.Authentication;

/**
 * Created by nebojsha on 25.08.2015.
 */
public interface AcmObjectLockService {
    AcmObjectLock createLock(Long objectId, String objectType, Authentication auth);

    void removeLock(Long objectId, String objectType);
}
