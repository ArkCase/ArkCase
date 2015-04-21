package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookListItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.model.OutlookCalendarItem;
import com.armedia.acm.service.outlook.model.OutlookContactItem;
import com.armedia.acm.service.outlook.model.OutlookMailItem;
import com.armedia.acm.service.outlook.model.OutlookTaskItem;
import com.armedia.acm.service.outlook.service.OutlookService;

import microsoft.exchange.webservices.data.core.ExchangeService;

import java.util.List;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookServiceImpl implements OutlookService
{
    public List<OutlookMailItem> findMailItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                               boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        return null;
    }

    public List<OutlookTaskItem> findTaskItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                               boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        return null;
    }

    public List<OutlookCalendarItem> findCalendarItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                       boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        return null;
    }

    public List<OutlookContactItem> findContactItems(AcmOutlookUser user, int start, int maxItems, String sortField,
                                                     boolean sortAscending)
            throws AcmOutlookConnectionFailedException, AcmOutlookListItemsFailedException
    {
        return null;
    }

    protected ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException
    {
        return null;
    }
}
