package com.armedia.acm.service.outlook.dao;

import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookDao {
    @Cacheable(value = "outlook-connection-cache", key = "#user.emailAddress")
    ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException;

    @CacheEvict(value = "outlook-connection-cache", key = "#user.emailAddress")
    void disconnect(AcmOutlookUser user);

    FindItemsResults<Item> findItems(
            ExchangeService service,
            WellKnownFolderName wellKnownFolderName,
            PropertySet extraFieldsToRetrieve,
            int start,
            int maxItems,
            String sortProperty,
            boolean sortAscending)
            throws AcmOutlookFindItemsFailedException;

    FindItemsResults<Item> findItems(
            ExchangeService service,
            String folderId,
            PropertySet extraFieldsToRetrieve,
            int start,
            int maxItems,
            String sortProperty,
            boolean sortAscending)
            throws AcmOutlookFindItemsFailedException;

    OutlookCalendarItem createCalendarAppointment(
            ExchangeService service,
            Folder folder,
            OutlookCalendarItem calendarItem)
            throws AcmOutlookCreateItemFailedException;

    OutlookTaskItem createTaskItem(
            ExchangeService service,
            Folder folder,
            OutlookTaskItem taskItem)
            throws AcmOutlookCreateItemFailedException;

    OutlookContactItem createContactItem(
            ExchangeService service,
            Folder folder,
            OutlookContactItem contactItem)
            throws AcmOutlookCreateItemFailedException;

    void deleteItem(ExchangeService service,
                    String itemId,
                    DeleteMode deleteMode);

    void deleteAppointmentItem(ExchangeService service,
                               String itemId,
                               boolean recurring,
                               DeleteMode deleteMode);

    OutlookFolder createFolder(ExchangeService service,
                               String owner,
                               WellKnownFolderName parentFolderName,
                               OutlookFolder newFolder) throws AcmOutlookItemNotFoundException, AcmOutlookCreateItemFailedException;

    OutlookFolder createFolder(ExchangeService service,
                               String owner,
                               String parentFolderId,
                               OutlookFolder newFolder)
            throws AcmOutlookCreateItemFailedException, AcmOutlookItemNotFoundException;

    void deleteFolder(ExchangeService service,
                      String folderId,
                      DeleteMode deleteMode)throws AcmOutlookItemNotFoundException;


    public FindFoldersResults findFolders(ExchangeService service,
                                          String parentFolderId,
                                          int start,
                                          int maxItems,
                                          String sortProperty,
                                          boolean sortAscending) throws AcmOutlookFindItemsFailedException;
    public FindFoldersResults findFolders(ExchangeService service,
                                          WellKnownFolderName wellKnownFolderName,
                                          int start,
                                          int maxItems,
                                          String sortProperty,
                                          boolean sortAscending) throws AcmOutlookFindItemsFailedException;

    void addFolderPermissions(ExchangeService service,
                              String folderId,
                              List<OutlookFolderPermission> permissions)
            throws AcmOutlookItemNotFoundException;

    void addFolderPermission(ExchangeService service,
                              String folderId,
                              OutlookFolderPermission permission)
            throws AcmOutlookItemNotFoundException;

    void removeFolderPermissions(ExchangeService service,
                                 String folderId,
                                 List<OutlookFolderPermission> permissions)
            throws AcmOutlookItemNotFoundException;

    void removeFolderPermission(ExchangeService service,
                                String folderId,
                                OutlookFolderPermission permission) throws AcmOutlookItemNotFoundException;

    Folder getFolder(ExchangeService service,
                     WellKnownFolderName wellKnownFolderName) throws AcmOutlookItemNotFoundException;

    Folder getFolder(ExchangeService service,
                     String folderId) throws AcmOutlookItemNotFoundException;
}
