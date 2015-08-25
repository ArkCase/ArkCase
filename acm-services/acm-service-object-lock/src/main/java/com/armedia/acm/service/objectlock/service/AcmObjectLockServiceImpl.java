package com.armedia.acm.service.objectlock.service;

import com.armedia.acm.service.objectlock.dao.AcmObjectLockDao;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockServiceImpl implements AcmObjectLockService {
    private AcmObjectLockDao acmObjectLockDao;

    @Override
    @Transactional
    public AcmObjectLock createLock(Long objectId, String objectType) {
        AcmObjectLock ol = new AcmObjectLock();
        ol.setObjectId(objectId);
        ol.setObjectType(objectType);
        return (AcmObjectLock) acmObjectLockDao.save(ol);
    }

    @Override
    @Transactional
    public void removeLock(Long objectId, String objectType) {
        AcmObjectLock ol = acmObjectLockDao.findLock(objectId, objectType);
        acmObjectLockDao.remove(ol);
    }

    @Override
    public List<AcmObjectLock> getAllLocksByType(String objectType) {
        return acmObjectLockDao.getAllLocksType(objectType);
    }

    public void setAcmObjectLockDao(AcmObjectLockDao acmObjectLockDao) {
        this.acmObjectLockDao = acmObjectLockDao;
    }
}
