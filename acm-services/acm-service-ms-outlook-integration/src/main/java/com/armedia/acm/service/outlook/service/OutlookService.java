package com.armedia.acm.service.outlook.service;

import com.armedia.acm.service.outlook.dao.OutlookDao;
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
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookService {

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                  boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                  boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookCalendarItem> findCalendarItems(String folderId, AcmOutlookUser user, int start, int maxItems, String sortField,
                                                          boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                        boolean sortAscending, SearchFilter filter)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookCalendarItem createOutlookAppointment(AcmOutlookUser user, OutlookCalendarItem calendarItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookTaskItem createOutlookTaskItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookTaskItem taskItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookContactItem createOutlookContactItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookContactItem contactItem) throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void deleteItem(AcmOutlookUser user, String itemId, DeleteMode deleteMode) throws AcmOutlookException, AcmOutlookItemNotFoundException;

    void deleteAllItemsFoundByExtendedProperty(String folderId, AcmOutlookUser user, ExtendedPropertyDefinition extendedPropertyDefinition, Object extendedPropertyValue);

    @Retryable(maxAttempts = 3, value=AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void deleteAppointmentItem(AcmOutlookUser user, String itemId, Boolean recurring, DeleteMode deleteMode);

    void setDao(OutlookDao dao);
}
