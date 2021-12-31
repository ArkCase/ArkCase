package com.armedia.acm.plugins.wopi.service;

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

import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pluginmanager.service.AcmConfigurablePlugin;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.lock.FileLockType;
import com.armedia.acm.plugins.wopi.model.WopiConfig;
import com.armedia.acm.plugins.wopi.model.WopiFileInfo;
import com.armedia.acm.plugins.wopi.model.WopiLockInfo;
import com.armedia.acm.plugins.wopi.model.WopiSessionInfo;
import com.armedia.acm.plugins.wopi.model.WopiUserInfo;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Period;
import java.util.Date;
import java.util.Optional;

public class WopiAcmService implements AcmConfigurablePlugin
{
    private WopiConfig wopiConfig;
    private EcmFileService ecmFileService;
    private AcmObjectLockingManager objectLockingManager;
    private AcmObjectLockService objectLockService;
    private EcmFileTransaction fileTransaction;
    private ArkPermissionEvaluator permissionEvaluator;
    private AuthenticationTokenService tokenService;
    private static final Logger log = LogManager.getLogger(WopiAcmService.class);
    private static final String WOPI_PLUGIN = "WOPI";

    public WopiSessionInfo getSessionInfo(Authentication authentication, Long fileId, String accessToken)
    {
        boolean canWrite = permissionEvaluator.hasPermission(authentication, fileId, "FILE", "write|group-write");
        return new WopiSessionInfo(accessToken, authentication.getName(), fileId.toString(), !canWrite, canWrite);
    }

    public WopiUserInfo getUserInfo(AcmUser user, String token)
    {
        AuthenticationToken authToken = tokenService.findByKey(token);

        Long tokenTtl = 0L;
        if (authToken != null && authToken.isActive())
        {
            tokenTtl = tokenService.calculateTokenTimeToLive(authToken,
                    Period.ofDays(AuthenticationTokenService.WOPI_TICKET_EXPIRATION_DAYS));
        }
        return new WopiUserInfo(user.getFullName(), user.getUserId(), user.getLang(), tokenTtl);
    }

    public Optional<WopiFileInfo> getFileInfo(Long id)
    {
        EcmFile file = ecmFileService.findById(id);
        if (file == null)
        {
            log.error("File with id [{}] is not found", id);
            return Optional.empty();
        }

        Optional<EcmFileVersion> ecmFileVersion = file.getVersions().stream()
                .filter(fileVersion -> fileVersion.getVersionTag().equals(file.getActiveVersionTag()))
                .findFirst();
        Long fileSize = ecmFileVersion.map(EcmFileVersion::getFileSizeBytes).orElse(0L);

        return Optional.of(new WopiFileInfo(file.getFileId(), file.getFileName(),
                file.getFileExtension(), file.getCreator(), file.getActiveVersionTag(), fileSize));
    }

    public InputStreamResource getFileContents(Long id) throws AcmUserActionFailedException
    {
        InputStream fileContent = ecmFileService.downloadAsInputStream(id);
        return new InputStreamResource(fileContent);
    }

    public void putFile(Long id, InputStreamResource resource, Authentication authentication)
            throws AcmObjectNotFoundException, IOException, ArkCaseFileRepositoryException
    {
        EcmFile fileToBeReplaced = ecmFileService.findById(id);
        if (fileToBeReplaced == null)
        {
            throw new AcmObjectNotFoundException("FILE", id, "File not found");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("arkcase-update-file-transaction", null);
            FileUtils.copyInputStreamToFile(resource.getInputStream(), tempFile);
            fileTransaction.updateFileTransactionEventAware(authentication, fileToBeReplaced, tempFile);
        }
        finally
        {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    public WopiLockInfo getSharedLock(Long fileId)
    {
        AcmObjectLock lock = objectLockService.findLock(fileId, "FILE");
        return lock != null && lock.getLockType().equals(FileLockType.SHARED_WRITE.name())
                ? new WopiLockInfo(fileId, lock.getExpiry().toInstant().getEpochSecond())
                : null;
    }

    public WopiLockInfo lock(Long fileId, Authentication authentication)
    {
        AcmObjectLock lock = objectLockingManager.acquireObjectLock(fileId, "FILE",
                FileLockType.SHARED_WRITE.name(), wopiConfig.getWopiLockDuration(), false, authentication.getName());
        Date expirationDate = lock.getExpiry();
        long expirationEpochSeconds = expirationDate.toInstant().getEpochSecond();
        return new WopiLockInfo(lock.getId(), expirationEpochSeconds);
    }

    public WopiLockInfo unlock(Long fileId, Long lockKey, Authentication authentication)
    {
        // should be able to remove lock even if the request did not come from the user who originally created the lock
        objectLockingManager.releaseObjectLock(fileId, "FILE", FileLockType.SHARED_WRITE.name(),
                false, authentication.getName(), lockKey);
        return new WopiLockInfo(0L, 0L);
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

    public WopiConfig getWopiConfig()
    {
        return wopiConfig;
    }

    public void setWopiConfig(WopiConfig wopiConfig)
    {
        this.wopiConfig = wopiConfig;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AcmObjectLockingManager getObjectLockingManager()
    {
        return objectLockingManager;
    }

    public void setObjectLockingManager(AcmObjectLockingManager objectLockingManager)
    {
        this.objectLockingManager = objectLockingManager;
    }

    public AcmObjectLockService getObjectLockService()
    {
        return objectLockService;
    }

    public void setObjectLockService(AcmObjectLockService objectLockService)
    {
        this.objectLockService = objectLockService;
    }

    public EcmFileTransaction getFileTransaction()
    {
        return fileTransaction;
    }

    public void setFileTransaction(EcmFileTransaction fileTransaction)
    {
        this.fileTransaction = fileTransaction;
    }

    public ArkPermissionEvaluator getPermissionEvaluator()
    {
        return permissionEvaluator;
    }

    public void setPermissionEvaluator(ArkPermissionEvaluator permissionEvaluator)
    {
        this.permissionEvaluator = permissionEvaluator;
    }

    public AuthenticationTokenService getTokenService()
    {
        return tokenService;
    }

    public void setTokenService(AuthenticationTokenService tokenService)
    {
        this.tokenService = tokenService;
    }

    @Override
    public boolean isEnabled()
    {
        return wopiConfig.getWopiPluginEnabled();
    }

    @Override
    public String getName()
    {
        return WOPI_PLUGIN;
    }
}
