package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.exception.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockServiceImpl implements AcmObjectLockService {
    private AcmObjectLockDao acmObjectLockDao;

    @Override
    @Transactional
    public AcmObjectLock createLock(Long objectId, String objectType, Authentication auth) {

        AcmObjectLock existingLock = acmObjectLockDao.findLock(objectId, objectType);

        if (existingLock != null) {
            //if current user is same as creator of the lock than just return existingLock, else throw an exception
            if (existingLock.getCreator().equals(auth.getName())) {
                return existingLock;
            } else
                throw new AcmObjectLockException("Lock already exist for different user.");
        }

        AcmObjectLock ol = new AcmObjectLock();
        ol.setObjectId(objectId);
        ol.setObjectType(objectType);
        return acmObjectLockDao.save(ol);
    }

    @Override
    @Transactional
    public void removeLock(Long objectId, String objectType) {
        AcmObjectLock ol = acmObjectLockDao.findLock(objectId, objectType);
        if (ol == null)
            throw new AcmObjectLockException("Error removing. Lock for [objectId, objectType] = [" + objectId + ", " + objectType + "] doesn't exists!");
        acmObjectLockDao.remove(ol);
    }

    public void setAcmObjectLockDao(AcmObjectLockDao acmObjectLockDao) {
        this.acmObjectLockDao = acmObjectLockDao;
    }
}
