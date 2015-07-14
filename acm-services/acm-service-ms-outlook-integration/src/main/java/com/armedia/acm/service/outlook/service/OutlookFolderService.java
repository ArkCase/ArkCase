package com.armedia.acm.service.outlook.service;

import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

/**
 * Created by nebojsha on 13.05.2015.
 */
public interface OutlookFolderService {

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookFolder createFolder(AcmOutlookUser user,
                               WellKnownFolderName parentFolderName,
                               OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookFolder getFolder(AcmOutlookUser user,
                            String folderId) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void deleteFolder(AcmOutlookUser user,
                      String folderId,
                      DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void addFolderPermission(AcmOutlookUser user,
                             String folderId,
                             OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void removeFolderPermission(AcmOutlookUser user,
                                String folderId,
                                OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException;

    void updateFolderPermissions(AcmOutlookUser user, String calendarFolderId, List<OutlookFolderPermission> folderPermissionsToBeAdded);
}
