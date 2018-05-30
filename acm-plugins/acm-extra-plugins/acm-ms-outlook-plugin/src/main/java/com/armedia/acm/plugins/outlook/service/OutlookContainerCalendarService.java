package com.armedia.acm.plugins.outlook.service;

/*-
 * #%L
 * ACM Extra Plugin: MS Outlook Integration
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
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.List;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

/**
 * Created by nebojsha on 25.05.2015.
 */
public interface OutlookContainerCalendarService
{

    OutlookFolder createFolder(AcmOutlookUser outlookUser, Long objectId, String objectType, String folderName, AcmContainer container,
            List<AcmParticipant> participants) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    @Deprecated
    void deleteFolder(AcmOutlookUser outlookUser, Long containerId, String folderId, DeleteMode deleteMode)
            throws AcmOutlookItemNotFoundException;

    void deleteFolder(AcmOutlookUser outlookUser, AcmContainer container, DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

    void updateFolderParticipants(AcmOutlookUser outlookUser, String folderId, List<AcmParticipant> participants)
            throws AcmOutlookItemNotFoundException;
}
