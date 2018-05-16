package com.armedia.acm.services.wopi.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
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

    public Long refreshLock(Long lockKey)
    {
        AcmObjectLock lock = objectLockService.findLock(lockKey);
        return lock != null ? lock.getId() : null;
    }

    public Long unlock(Long fileId, Long lockKey, Authentication authentication)
    {
        // should be able to remove lock even if the request did not come from the user who originally created the lock
        objectLockService.removeLock(fileId, "FILE", AcmObjectLockConstants.SHARED_LOCK, authentication);
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
