package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.services.participants.model.AcmAssignedObject;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public interface AcmObjectDataAccessBatchUpdateLocator<T extends AcmAssignedObject>
{
    List<T> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize);

    void save(T assignedObject);
}
