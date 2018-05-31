package com.armedia.acm.services.wopi.service;

/*-
 * #%L
 * ACM Service: Wopi service
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.lock.FileLockType;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.wopi.model.WopiFileInfo;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class WopiAcmService
{
    private EcmFileService ecmFileService;
    private AcmObjectLockingManager objectLockingManager;
    private AcmObjectLockService objectLockService;
    private EcmFileTransaction fileTransaction;
    private ArkPermissionEvaluator permissionEvaluator;
    private static final Logger log = LoggerFactory.getLogger(WopiAcmService.class);

    public Optional<WopiFileInfo> getFileInfo(Long id, Authentication authentication)
    {
        EcmFile file = ecmFileService.findById(id);
        if (file == null)
        {
            log.error("File with id [{}] is not found", id);
            return Optional.empty();
        }
        boolean userCanWrite = permissionEvaluator.hasPermission(authentication, file.getId(),
                "FILE", "write|group-write");
        Optional<EcmFileVersion> ecmFileVersion = file.getVersions().stream()
                .filter(fileVersion -> fileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .findFirst();
        Long fileSize = ecmFileVersion.map(EcmFileVersion::getFileSizeBytes).orElse(0L);

        return Optional.of(new WopiFileInfo(file.getFileId(), file.getFileName(),
                file.getFileExtension(), file.getCreator(), file.getActiveVersionTag(),
                fileSize, authentication.getName(), userCanWrite, !userCanWrite));
    }

    public InputStreamResource getFileContents(Long id) throws AcmUserActionFailedException, MuleException
    {
        InputStream fileContent = ecmFileService.downloadAsInputStream(id);
        return new InputStreamResource(fileContent);
    }

    public void putFile(Long id, InputStreamResource resource, Authentication authentication)
            throws AcmObjectNotFoundException, IOException, MuleException
    {
        EcmFile fileToBeReplaced = ecmFileService.findById(id);
        if (fileToBeReplaced == null)
        {
            throw new AcmObjectNotFoundException("FILE", id, "File not found");
        }
        fileTransaction.updateFileTransactionEventAware(authentication, fileToBeReplaced, resource.getInputStream());
    }

    public Long getSharedLock(Long fileId)
    {
        AcmObjectLock lock = objectLockService.findLock(fileId, "FILE");
        return lock != null && lock.getLockType().equals(FileLockType.SHARED_WRITE.name()) ? lock.getId() : null;
    }

    public Long lock(Long fileId, Authentication authentication)
    {
        AcmObjectLock lock = objectLockingManager.acquireObjectLock(fileId, "FILE",
                FileLockType.SHARED_WRITE.name(), null, false, authentication.getName());
        return lock.getId();
    }

    public Long unlock(Long fileId, Long lockKey, Authentication authentication)
    {
        // should be able to remove lock even if the request did not come from the user who originally created the lock
        objectLockingManager.releaseObjectLock(fileId, "FILE", FileLockType.SHARED_WRITE.name(), false, authentication.getName(), lockKey);
        return 0L;
    }

    public void renameFile(Long fileId, String newName)
            throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        ecmFileService.renameFile(fileId, newName);
    }

    public void deleteFile(Long fileId) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        ecmFileService.deleteFile(fileId);
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public void setFileTransaction(EcmFileTransaction fileTransaction)
    {
        this.fileTransaction = fileTransaction;
    }

    public void setPermissionEvaluator(ArkPermissionEvaluator permissionEvaluator)
    {
        this.permissionEvaluator = permissionEvaluator;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }
}
