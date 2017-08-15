package com.armedia.acm.plugins.outlook.service;

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
