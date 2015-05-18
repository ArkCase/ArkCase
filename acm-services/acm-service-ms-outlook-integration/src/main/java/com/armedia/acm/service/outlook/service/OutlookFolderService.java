package com.armedia.acm.service.outlook.service;

import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;

/**
 * Created by nebojsha on 13.05.2015.
 */
public interface OutlookFolderService {

    OutlookFolder createFolder(AcmOutlookUser user,
                               WellKnownFolderName parentFolderName,
                               OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    OutlookFolder createFolder(WellKnownFolderName parentFolderName,
                               OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    OutlookFolder getFolder(AcmOutlookUser user,
                            String folderId) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    void deleteFolder(AcmOutlookUser user,
                      String folderId,
                      DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

    void deleteFolder(String folderId,
                      DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

    void addFolderPermission(AcmOutlookUser user,
                             String folderId,
                             OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException;

    void addFolderPermission(String folderId,
                             OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException;

    void removeFolderPermission(AcmOutlookUser user,
                                String folderId,
                                OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException;

    void removeFolderPermission(String folderId,
                                OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException;

}
