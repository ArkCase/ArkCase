package com.armedia.acm.service.outlook.dao;

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

import com.armedia.acm.core.exceptions.AcmOutlookConnectionFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookFindItemsFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookFolder;
import com.armedia.acm.service.outlook.model.OutlookFolderPermission;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;

import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookDao
{

    ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException;

    FindItemsResults<Item> findItems(
            ExchangeService service,
            WellKnownFolderName wellKnownFolderName,
            PropertySet extraFieldsToRetrieve,
            int start,
            int maxItems,
            String sortProperty,
            boolean sortAscending,
            SearchFilter filter)
            throws AcmOutlookFindItemsFailedException;

    FindItemsResults<Item> findItems(
            ExchangeService service,
            String folderId,
            PropertySet extraFieldsToRetrieve,
            int start,
            int maxItems,
            String sortProperty,
            boolean sortAscending,
            SearchFilter filter)
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
            DeleteMode deleteMode) throws AcmOutlookItemNotFoundException;

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
