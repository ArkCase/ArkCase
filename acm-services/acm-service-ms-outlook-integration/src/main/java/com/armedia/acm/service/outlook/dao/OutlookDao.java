package com.armedia.acm.service.outlook.dao;

import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by armdev on 4/20/15.
 */
public interface OutlookDao
{
//    @Cacheable(value="outlook-connection-cache", key="#user.emailAddress")
    ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException;
}
