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
import com.armedia.acm.services.authenticationtoken.model.AuthenticationToken;
import com.armedia.acm.services.authenticationtoken.service.AuthenticationTokenService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;
import com.armedia.acm.services.wopi.model.WopiConfig;
import com.armedia.acm.services.wopi.model.WopiFileInfo;
import com.armedia.acm.services.wopi.model.WopiLockInfo;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.core.Authentication;

import javax.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WopiAcmService
{
    private WopiConfig wopiConfig;
    private EcmFileService ecmFileService;
    private AcmObjectLockingManager objectLockingManager;
    private AcmObjectLockService objectLockService;
    private EcmFileTransaction fileTransaction;
    private ArkPermissionEvaluator permissionEvaluator;
    private AuthenticationTokenService tokenService;
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
                fileSize, userCanWrite, !userCanWrite));
    }

    public InputStreamResource getFileContents(Long id) throws AcmUserActionFailedException
    {
        InputStream fileContent = ecmFileService.downloadAsInputStream(id);
        return new InputStreamResource(fileContent);
    }

    public void putFile(Long id, @Nullable InputStreamResource resource, Authentication authentication)
            throws AcmObjectNotFoundException, IOException, MuleException
    {
        EcmFile fileToBeReplaced = ecmFileService.findById(id);
        if (fileToBeReplaced == null)
        {
            throw new AcmObjectNotFoundException("FILE", id, "File not found");
        }
        if (resource == null)
        {
            fileTransaction.updateFileTransactionEventAware(authentication, fileToBeReplaced, new ByteArrayInputStream(new byte[0]));
        }
        else
        {
            fileTransaction.updateFileTransactionEventAware(authentication, fileToBeReplaced, resource.getInputStream());
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

    public String getWopiAccessToken(Long fileId, String userMail, Authentication authentication)
    {
        List<AuthenticationToken> tokens = tokenService.findByTokenEmailAndFileId(userMail, fileId);
        List<AuthenticationToken> activeTokens = tokens.stream()
                .filter(token -> token.getStatus().equals("ACTIVE"))
                .collect(Collectors.toList());
        String authenticationTokenKey;
        if (activeTokens.isEmpty())
        {
            authenticationTokenKey = tokenService.generateAndSaveAuthenticationToken(fileId, userMail, authentication);
        }
        else
        {
            authenticationTokenKey = activeTokens.stream()
                    .filter(authenticationToken -> {
                        Instant tokenCreated = authenticationToken.getCreated().toInstant();
                        Instant tokenExpiration = tokenCreated.plus(Period.ofDays(AuthenticationTokenService.EMAIL_TICKET_EXPIRATION_DAYS));
                        // 10min before expiration, Office Online client will expect renewed token
                        return ChronoUnit.MINUTES.between(Instant.now(), tokenExpiration) > 11;
                    })
                    .map(AuthenticationToken::getKey)
                    .findFirst()
                    .orElseGet(() -> tokenService.generateAndSaveAuthenticationToken(fileId, userMail, authentication));
        }
        return authenticationTokenKey;
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
}
