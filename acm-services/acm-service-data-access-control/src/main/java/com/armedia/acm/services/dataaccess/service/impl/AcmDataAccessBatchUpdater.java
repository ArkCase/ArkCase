package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class AcmDataAccessBatchUpdater
{

    private DataAccessPrivilegeListener dataAccessPrivilegeListener;

    @Transactional
    public void updateDataAccessPolicy(List<AcmAssignedObject> assignedObjects,
                                       AcmObjectDataAccessBatchUpdateLocator locator)
    {
        for ( AcmAssignedObject assignedObject : assignedObjects )
        {
            getDataAccessPrivilegeListener().applyAssignmentAndAccessRules(assignedObject);
            locator.save(assignedObject);
        }
    }

    public DataAccessPrivilegeListener getDataAccessPrivilegeListener()
    {
        return dataAccessPrivilegeListener;
    }

    public void setDataAccessPrivilegeListener(DataAccessPrivilegeListener dataAccessPrivilegeListener)
    {
        this.dataAccessPrivilegeListener = dataAccessPrivilegeListener;
    }
}
