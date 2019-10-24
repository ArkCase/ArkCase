package com.armedia.acm.service.outlook.service.impl;

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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.fail;

import com.armedia.acm.core.exceptions.AcmOutlookException;
import com.armedia.acm.core.exceptions.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.filter.SearchFilter;

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
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-convert-folder-service.xml",
        "/spring/spring-library-folder-watcher.xml"
})
public class OutlookServiceRetryLogicIT extends EasyMockSupport
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    private transient final Logger log = LogManager.getLogger(getClass());
    @Autowired
    private OutlookService outlookService;
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

        expectLastCall().times(expectedRetries);

        replayAll();

        try
        {
            outlookService.findTaskItems(user, 0, 5, "subject", true, null);
            fail("should have caught an exception");
        }
        catch (AcmOutlookException e)
        {
            log.info("Got expected exception, test passes.");
        }

        verifyAll();
    }
}
