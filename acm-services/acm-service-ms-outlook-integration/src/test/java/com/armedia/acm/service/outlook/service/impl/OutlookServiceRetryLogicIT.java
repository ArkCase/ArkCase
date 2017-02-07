package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.core.exceptions.AcmOutlookException;
import com.armedia.acm.core.exceptions.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookService;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-test-ms-outlook-integration.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-activiti-configuration.xml"
})
public class OutlookServiceRetryLogicIT extends EasyMockSupport
{
    @Autowired
    private OutlookService outlookService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String validUser = "***REMOVED***";
    private String validPassword = "AcMd3v$";

    private AcmOutlookUser user = new AcmOutlookUser("ann-acm", validUser, validPassword);

    private OutlookDao mockDao;
    private ExchangeService mockExchangeService;

    @Before
    public void setUp() throws Exception
    {
        mockDao = createMock(OutlookDao.class);
        mockExchangeService = createMock(ExchangeService.class);

        outlookService.setDao(mockDao);
    }

    @Test
    public void tasks_firstCallFails_SecondCallWorks()
    {
        expect(mockDao.connect(user)).andReturn(mockExchangeService);

        int start = 0;
        int maxItems = 5;
        String sortProperty = "subject";
        boolean sortAscending = true;
        SearchFilter filter = null;

        // first call - throw an exception
        expect(mockDao.findItems(
                eq(mockExchangeService),
                eq(WellKnownFolderName.Tasks),
                anyObject(PropertySet.class),
                eq(start),
                eq(maxItems),
                eq(sortProperty),
                eq(sortAscending),
                eq(filter))).andThrow(new AcmOutlookFindItemsFailedException(new NullPointerException("test exception")));

        // since we threw an exception, now it should disconnect, and then retry
        mockDao.disconnect(user);

        // second connect, after the exception
        expect(mockDao.connect(user)).andReturn(mockExchangeService);

        // second call - return something
        expect(mockDao.findItems(
                eq(mockExchangeService),
                eq(WellKnownFolderName.Tasks),
                anyObject(PropertySet.class),
                eq(start),
                eq(maxItems),
                eq(sortProperty),
                eq(sortAscending),
                eq(filter))).andReturn(new FindItemsResults<>());


        replayAll();

        outlookService.findTaskItems(user, 0, 5, "subject", true, null);

        verifyAll();
    }

    @Test
    public void tasks_threeCallsFail_shouldGetException()
    {
        int expectedRetries = 3;
        expect(mockDao.connect(user)).andReturn(mockExchangeService).times(expectedRetries);

        int start = 0;
        int maxItems = 5;
        String sortProperty = "subject";
        boolean sortAscending = true;
        SearchFilter filter = null;

        // first call - throw an exception
        expect(mockDao.findItems(
                eq(mockExchangeService),
                eq(WellKnownFolderName.Tasks),
                anyObject(PropertySet.class),
                eq(start),
                eq(maxItems),
                eq(sortProperty),
                eq(sortAscending),
                eq(filter))).andThrow(new AcmOutlookFindItemsFailedException(new NullPointerException("test exception")))
                .times(expectedRetries);

        // since we threw an exception, now it should disconnect, and then retry
        mockDao.disconnect(user);
        expectLastCall().times(expectedRetries);

        replayAll();

        try
        {
            outlookService.findTaskItems(user, 0, 5, "subject", true, null);
            fail("should have caught an exception");
        } catch (AcmOutlookException e)
        {
            log.info("Got expected exception, test passes.");
        }

        verifyAll();
    }
}
