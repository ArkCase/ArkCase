package com.armedia.acm.plugins.outlook.service;

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.services.participants.model.AcmParticipant;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;

import java.util.List;

/**
 * Created by nebojsha on 25.05.2015.
 */
public interface OutlookContainerCalendarService {

    OutlookFolder createFolder(String folderName,
                               AcmContainer container, List<AcmParticipant> participants) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    void deleteFolder(Long containerId,
                      String folderId,
                      DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

    void updateFolderParticipants(String folderId,
                                  List<AcmParticipant> participants)
            throws AcmOutlookItemNotFoundException;
}
