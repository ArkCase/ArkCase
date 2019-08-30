package com.armedia.acm.services.dataaccess.service.impl;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import com.armedia.acm.data.exceptions.AcmAccessControlException;
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
    public void updateDataAccessPolicy(List<AcmAssignedObject> assignedObjects, AcmObjectDataAccessBatchUpdateLocator locator)
            throws AcmAccessControlException
    {
        for (AcmAssignedObject assignedObject : assignedObjects)
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
