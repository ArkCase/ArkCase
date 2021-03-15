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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.ObjectLockingProvider;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link ObjectLockingProvider} that handles locks for objects of type FOLDER.
 * 
 * Created by bojan.milenkoski on 10/05/2018.
 */
public class FolderLockingProvider extends DefaultEcmObjectLockingProvider
{
    private AcmFolderService folderService;
    private FileLockingProvider fileLockingProvider;

    @Override
    public String getObjectType()
    {
        return EcmFileConstants.OBJECT_FOLDER_TYPE;
    }

    @Override
    public void checkIfObjectLockCanBeAcquired(Long objectId, String objectType, String lockType, boolean checkChildObjects, String userId)
            throws AcmObjectLockException
    {
        super.checkIfObjectLockCanBeAcquired(objectId, objectType, lockType, checkChildObjects, userId);

        if (checkChildObjects)
        {
            // check if the same lock can be acquired for folder children
            try
            {
                List<AcmObject> folderChildren = folderService.getFolderChildren(objectId);
                for (AcmObject child : folderChildren)
                {
                    if (EcmFileConstants.OBJECT_FOLDER_TYPE.equalsIgnoreCase(child.getObjectType()))
                    {
                        checkIfObjectLockCanBeAcquired(child.getId(), objectType, lockType, true, userId);
                    }
                    if (EcmFileConstants.OBJECT_FILE_TYPE.equalsIgnoreCase(child.getObjectType()))
                    {
                        fileLockingProvider.checkIfObjectLockCanBeAcquired(child.getId(), objectType, lockType, false, userId);
                    }
                }
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                throw new AcmObjectLockException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized AcmObjectLock acquireObjectLock(Long objectId, String objectType, String lockType, Long expiry,
            boolean lockChildObjects, String userId)
            throws AcmObjectLockException
    {

        AcmObjectLock objectLock = super.acquireObjectLock(objectId, objectType, lockType, expiry,
                lockChildObjects, userId);

        if (lockChildObjects)
        {
            // acquire the same lock for folder children
            try
            {
                List<AcmObject> folderChildren = folderService.getFolderChildren(objectId);
                for (AcmObject child : folderChildren)
                {
                    if (EcmFileConstants.OBJECT_FOLDER_TYPE.equalsIgnoreCase(child.getObjectType()))
                    {
                        acquireObjectLock(child.getId(), objectType, lockType, expiry, true, userId);
                    }
                    if (EcmFileConstants.OBJECT_FILE_TYPE.equalsIgnoreCase(child.getObjectType()))
                    {
                        fileLockingProvider.acquireObjectLock(child.getId(), objectType, lockType, expiry, true, userId);
                    }
                }
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                throw new AcmObjectLockException(e.getMessage());
            }
        }

        return objectLock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void releaseObjectLock(Long objectId, String objectType, String lockType, boolean unlockChildObjects, String userId,
            Long lockId)
            throws AcmObjectLockException
    {
        super.releaseObjectLock(objectId, objectType, lockType, unlockChildObjects, userId, lockId);

        if (unlockChildObjects)
        {
            // release the same lock from folder children
            List<AcmObject> folderChildren;
            try
            {
                folderChildren = folderService.getFolderChildren(objectId);
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                // folder does not exist do nothing
                return;
            }

            for (AcmObject child : folderChildren)
            {
                if (EcmFileConstants.OBJECT_FOLDER_TYPE.equalsIgnoreCase(child.getObjectType()))
                {
                    releaseObjectLock(child.getId(), objectType, lockType, true, userId, lockId);
                }
                if (EcmFileConstants.OBJECT_FILE_TYPE.equalsIgnoreCase(child.getObjectType()))
                {
                    fileLockingProvider.releaseObjectLock(child.getId(), objectType, lockType, true, userId, lockId);
                }
            }
        }
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public FileLockingProvider getFileLockingProvider()
    {
        return fileLockingProvider;
    }

    public void setFileLockingProvider(FileLockingProvider fileLockingProvider)
    {
        this.fileLockingProvider = fileLockingProvider;
    }
}
