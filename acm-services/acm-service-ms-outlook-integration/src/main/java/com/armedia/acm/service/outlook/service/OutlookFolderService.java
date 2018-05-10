package com.armedia.acm.service.outlook.service;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

/**
 * Created by nebojsha on 13.05.2015.
 */
public interface OutlookFolderService
{
    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookFolder createFolder(AcmOutlookUser user, Long objectId, String objectType, WellKnownFolderName parentFolderName,
            OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookFolder getFolder(AcmOutlookUser user, String folderId)
            throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void deleteFolder(AcmOutlookUser user, String folderId, DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void addFolderPermission(AcmOutlookUser user, String folderId, OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void removeFolderPermission(AcmOutlookUser user, String folderId, OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException;

    void updateFolderPermissions(AcmOutlookUser user, String calendarFolderId, List<OutlookFolderPermission> folderPermissionsToBeAdded);
}
