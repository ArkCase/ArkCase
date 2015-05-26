package com.armedia.acm.service.outlook.service;

import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookCreateItemFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookException;
import com.armedia.acm.service.outlook.exception.AcmOutlookItemNotFoundException;
import com.armedia.acm.service.outlook.exception.AcmOutlookListItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import microsoft.exchange.webservices.data.enumeration.DeleteMode;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookService {
    OutlookResults<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                  boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookResults<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                  boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookResults<OutlookCalendarItem> findCalendarItems(String folderId, AcmOutlookUser user, int start, int maxItems, String sortField,
                                                          boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookResults<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                        boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookCalendarItem createOutlookAppointment(AcmOutlookUser user, OutlookCalendarItem calendarItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    OutlookTaskItem createOutlookTaskItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookTaskItem taskItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    OutlookContactItem createOutlookContactItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookContactItem contactItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    void deleteItem(AcmOutlookUser user, String itemId, DeleteMode deleteMode) throws AcmOutlookException, AcmOutlookItemNotFoundException;

    void deleteAppointmentItem(AcmOutlookUser user, String itemId, Boolean recurring, DeleteMode deleteMode);
}
