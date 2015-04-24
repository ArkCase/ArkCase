package com.armedia.acm.service.outlook.dao;

import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookDao
{
    @Cacheable(value="outlook-connection-cache", key="#user.emailAddress")
    ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException;

    @CacheEvict(value="outlook-connection-cache", key="#user.emailAddress")
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
}
