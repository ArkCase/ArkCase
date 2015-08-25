package com.armedia.acm.service.objectlock.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.objectlock.exception.AcmObjectLockException;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by nebojsha on 25.08.2015.
 */
public class AcmObjectLockDao extends AcmAbstractDao<AcmObjectLock> {

    List getAllObjectsByType(String objectType) {
        return null;
    }

    @Override
    protected Class getPersistenceClass() {
        return AcmObjectLock.class;
    }

    public AcmObjectLock findLock(Long objectId, String objectType) {
        String queryText =
                "SELECT ol " +
                        "FROM AcmObjectLock ol " +
                        "WHERE " +
                        "     ol.objectType = :objectType AND ol.objectId = :objectId";
        Query locksQuery = getEm().createQuery(queryText);

        locksQuery.setParameter("objectType", objectType);
        locksQuery.setParameter("objectId", objectId);
        try {
            AcmObjectLock retval = (AcmObjectLock) locksQuery.getSingleResult();
            return retval;
        } catch (NoResultException e) {
            return null;
        } catch (Throwable e) {
            throw new AcmObjectLockException("Error retrieving lock for [objectId, objectType] = [" + objectId + ", " + objectType + "]", e);
        }
    }

    public void remove(AcmObjectLock ol) {
        getEm().remove(ol);
    }
}
