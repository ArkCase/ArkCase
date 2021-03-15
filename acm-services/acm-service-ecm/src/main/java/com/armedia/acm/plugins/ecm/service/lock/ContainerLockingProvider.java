package com.armedia.acm.plugins.ecm.service.lock;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;

import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ObjectLockingProvider} that handles locks for objects of type CONTAINER.
 * 
 * Created by bojan.milenkoski on 10/05/2018.
 */
public class ContainerLockingProvider extends DefaultEcmObjectLockingProvider
{
    private AcmContainerDao containerDao;
    private FolderLockingProvider folderLockingProvider;

    @Override
    public String getObjectType()
    {
        return EcmFileConstants.OBJECT_CONTAINER_TYPE;
    }

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        super.checkIfObjectLockCanBeAcquired(objectId, objectType, lockType, checkChildObjects, userId);
        if (checkChildObjects)
        {
            // check if the same lock can be acquired for root and attachment folders
            AcmContainer container = containerDao.find(objectId);
            folderLockingProvider.checkIfObjectLockCanBeAcquired(container.getFolder().getId(), objectType, lockType, true, userId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.checkIfObjectLockCanBeAcquired(container.getAttachmentFolder().getId(), objectType, lockType,
                        true, userId);
            }
        }
    }

    @Override
    @Transactional
    public synchronized AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry,
            boolean lockChildObjects, String userId)
            throws AcmObjectLockException
    {
        AcmObjectLock objectLock = super.acquireObjectLock(objectId, objectType, lockType, expiry,
                lockChildObjects, userId);

        if (lockChildObjects)
        {
            // acquire the same lock for root and attachment folders
            AcmContainer container = containerDao.find(objectId);
            folderLockingProvider.acquireObjectLock(container.getFolder().getId(), objectType, lockType, expiry, true, userId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.acquireObjectLock(container.getAttachmentFolder().getId(), objectType, lockType, expiry,
                        true, userId);
            }
        }

        return objectLock;
    }

    @Override
    @Transactional
    public synchronized void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId,
            Long lockId)
            throws AcmObjectLockException
    {
        super.releaseObjectLock(objectId, objectType, lockType, unlockChildObjects, userId, lockId);

        if (unlockChildObjects)
        {
            // release the same lock from root and attachment folders
            AcmContainer container = containerDao.find(objectId);
            if (container == null)
            {
                // container does not exist do nothing
                return;
            }

            folderLockingProvider.releaseObjectLock(container.getFolder().getId(), objectType, lockType, unlockChildObjects, userId,
                    lockId);
            if (!container.getFolder().equals(container.getAttachmentFolder()))
            {
                folderLockingProvider.releaseObjectLock(container.getAttachmentFolder().getId(), objectType, lockType, unlockChildObjects,
                        userId, lockId);
            }
        }
    }

    public AcmContainerDao getContainerDao()
    {
        return containerDao;
    }

    public void setContainerDao(AcmContainerDao containerDao)
    {
        this.containerDao = containerDao;
    }

    public FolderLockingProvider getFolderLockingProvider()
    {
        return folderLockingProvider;
    }

    public void setFolderLockingProvider(FolderLockingProvider folderLockingProvider)
    {
        this.folderLockingProvider = folderLockingProvider;
    }
}
