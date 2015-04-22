package com.armedia.acm.service.outlook.service;

import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookListItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookResults;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookService
{
    OutlookResults<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                  boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookResults<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                  boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookResults<OutlookCalendarItem> findCalendarItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                          boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;

    OutlookResults<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                        boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException;
}
