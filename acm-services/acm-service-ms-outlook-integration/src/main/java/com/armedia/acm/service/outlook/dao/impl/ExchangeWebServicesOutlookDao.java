package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.enumeration.ExchangeVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;

/**
 * Created by armdev on 4/20/15.
 */
public class ExchangeWebServicesOutlookDao implements OutlookDao
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Cacheable(value="outlook-connection-cache", key="#user.emailAddress")
    public ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException
    {
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(user.getOutlookPassword(), "Password cannot be null");
        Objects.requireNonNull(user.getEmailAddress(), "E-mail address cannot be null");

        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        ExchangeCredentials credentials = new WebCredentials(user.getEmailAddress(), user.getOutlookPassword());
        service.setCredentials(credentials);

        try
        {
            service.autodiscoverUrl(user.getEmailAddress(), redirectionUrl -> true);
            return service;
        }
        catch (Exception e)
        {
            log.error("Could not connect to Exchange: " + e.getMessage(), e);
            throw new AcmOutlookConnectionFailedException(e.getMessage(), e);
        }
    }
}
