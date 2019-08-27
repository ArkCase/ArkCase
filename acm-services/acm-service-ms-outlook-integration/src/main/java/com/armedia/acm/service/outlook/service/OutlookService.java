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

import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.core.exceptions.AcmOutlookConnectionFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.core.exceptions.AcmOutlookListItemsFailedException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookPassword;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import com.armedia.acm.services.email.service.AcmEmailSenderService;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;

import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;
import microsoft.exchange.webservices.data.property.definition.ExtendedPropertyDefinition;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookService extends AcmEmailSenderService
{
    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField, boolean sortAscending,
            SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField, boolean sortAscending,
            SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookCalendarItem> findCalendarItems(String folderId, AcmOutlookUser user, int start, int maxItems, String sortField,
            boolean sortAscending, SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookResults<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
            boolean sortAscending, SearchFilter filter) throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookCalendarItem createOutlookAppointment(AcmOutlookUser user, OutlookCalendarItem calendarItem)
            throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookTaskItem createOutlookTaskItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookTaskItem taskItem)
            throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    OutlookContactItem createOutlookContactItem(AcmOutlookUser user, WellKnownFolderName folderName, OutlookContactItem contactItem)
            throws AcmOutlookConnectionFailedException, AcmOutlookCreateItemFailedException;

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void deleteItem(AcmOutlookUser user, String itemId, DeleteMode deleteMode) throws AcmOutlookException, AcmOutlookItemNotFoundException;

    void deleteAllItemsFoundByExtendedProperty(String folderId, AcmOutlookUser user, ExtendedPropertyDefinition extendedPropertyDefinition,
            Object extendedPropertyValue);

    @Retryable(maxAttempts = 3, value = AcmOutlookException.class, backoff = @Backoff(delay = 500))
    void deleteAppointmentItem(AcmOutlookUser user, String itemId, Boolean recurring, DeleteMode deleteMode);

    OutlookDTO retrieveOutlookPassword(Authentication authentication) throws AcmEncryptionException;

    void saveOutlookPassword(Authentication authentication, OutlookDTO in) throws AcmEncryptionException;

    OutlookPassword getOutlookPasswordForUser(String userId);

    void setDao(OutlookDao dao);

}
