package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.enumeration.DateTimePrecision;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by armdev on 4/20/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ms-outlook-integration.xml"
})
public class ExchangeWebServicesOutlookDaoIT
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String validUser = "dmiller@armedia.com";
    private String validPassword = "!MattHelm531";

    private AcmOutlookUser user = new AcmOutlookUser("dmiller", validUser, validPassword);

    @Autowired
    @Qualifier("exchangeWebServicesOutlookDao")
    private OutlookDao dao;

    @Test
    public void connect()
    {
        ExchangeService service = null;

        service = dao.connect(user);

        DateTimePrecision precision = service.getDateTimePrecision();

        assertNotNull(precision);

        log.info("Date time precision: " + precision);

        log.debug("---------------- starting again");

        service = dao.connect(user);

        log.info("Exchange 2007 compatibility mode? " + service.getExchange2007CompatibilityMode());

        log.debug("---------------- another user");

        AcmOutlookUser invalidUser = new AcmOutlookUser("invalidUser", "invalidUser@armedia.com", "AcMd3v$");

        try
        {
            dao.connect(invalidUser);
            fail("should have failed to authenticate");
        }
        catch (Exception e)
        {
            // expected
            log.info("Exception: " + e.getMessage(), e);
        }



    }


}
