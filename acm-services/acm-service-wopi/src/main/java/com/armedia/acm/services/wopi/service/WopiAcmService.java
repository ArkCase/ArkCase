package com.armedia.acm.services.wopi.service;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.model.AcmObjectLockConstants;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
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
        try
        {
            Optional<EcmFileVersion> ecmFileVersion = file.getVersions().stream()
                    .filter(fileVersion -> fileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                    .findFirst();
            Long fileSize = ecmFileVersion.map(EcmFileVersion::getFileSizeBytes).orElse(0L);

            return Optional.of(new WopiFileInfo(file.getFileId(), file.getFileName(),
                    file.getFileExtension(), file.getCreator(), file.getActiveVersionTag(),
                    fileSize, authentication.getName(), userCanWrite));
        }
        catch (Exception e)
        {
            log.error("File with id [{}] is not found", id);
            return Optional.empty();
        }
    }

    public Optional<InputStreamResource> getFileContents(Long id)
    {
        try
        {
            InputStream fileContent = ecmFileService.downloadAsInputStream(id);
            return Optional.of(new InputStreamResource(fileContent));
        }
        catch (MuleException | AcmUserActionFailedException e)
        {
            log.error("Failed to download file with id [{}]", id);
            return Optional.empty();
        }
    }

    public boolean putFile(Long id, InputStreamResource resource, Authentication authentication)
    {
        EcmFile fileToBeReplaced = ecmFileService.findById(id);
        if (fileToBeReplaced == null)
        {
            log.error("File with id [{}] is not found", id);
            return false;
        }
        try
        {
            fileTransaction.updateFileTransactionEventAware(authentication, fileToBeReplaced, resource.getInputStream());
            return true;
        }
        catch (MuleException | IOException e)
        {
            log.error("Can't update file with id [{}]", id);
            return false;
        }
    }

    public Long getLock(Long fileId)
    {
        AcmObjectLock lock = objectLockService.findLock(fileId, "FILE");
        return lock != null ? lock.getId() : null;
    }

    public Long lock(Long fileId, Authentication authentication)
    {
        AcmObjectLock lock = objectLockService.createLock(fileId, "FILE",
                AcmObjectLockConstants.SHARED_LOCK, authentication);
        return lock.getId();
    }

    public Long refreshLock(String lockKey)
    {
        AcmObjectLock lock = objectLockService.findLock(Long.parseLong(lockKey));
        return lock.getId();
    }

    public Long unlock(Long fileId, String lockKey, Authentication authentication)
    {
        // should be able to remove lock even if the request did not come from the user who originally created the lock
        objectLockService.removeLock(fileId, "FILE", AcmObjectLockConstants.SHARED_LOCK, authentication);
        return 0L;
    }

    public Long manageLock(String overrideType, Long fileId, String lockKey, Authentication authentication)
    {
        switch (overrideType)
        {
        case "LOCK":
            return lock(fileId, authentication);
        case "GET_LOCK":
            return getLock(fileId);
        case "REFRESH_LOCK":
            return refreshLock(lockKey);
        case "UNLOCK":
            return unlock(fileId, lockKey, authentication);
        default:
            return null;
        }
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public void setFileTransaction(EcmFileTransaction fileTransaction)
    {
        this.fileTransaction = fileTransaction;
    }

    public void setPermissionEvaluator(ArkPermissionEvaluator permissionEvaluator)
    {
        this.permissionEvaluator = permissionEvaluator;
    }
}
