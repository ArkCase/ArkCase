package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.auth.AcmAuthentication;
import com.armedia.acm.auth.AcmAuthenticationManager;
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaPluginConstants;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.model.AcmContainer;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Date;

public class AcmCaseFileClosedListenerTest extends EasyMockSupport
{
    private AcmCaseFileClosedListener unit;
    private AlfrescoRecordsService mockService;
    private Authentication mockAuthentication;
    private AcmAuthenticationManager mockAuthenticationManager;

    @Before
    public void setUp()
    {
        unit = new AcmCaseFileClosedListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthentication = createMock(Authentication.class);
        mockAuthenticationManager = createMock(AcmAuthenticationManager.class);

        unit.setAlfrescoRecordsService(mockService);
        unit.setAuthenticationManager(mockAuthenticationManager);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.FALSE);

        CaseEvent caseEvent = new CaseEvent(new CaseFile(), "ipAddress", "user", "eventType", new Date(), true,
                mockAuthentication);

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();
    }

    @Test
    public void notACaseClosedEvent_shouldNotDeclareRecords()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        CaseEvent caseEvent = new CaseEvent(new CaseFile(), "ipAddress", "user", "caseOpenEvent", new Date(), true,
                mockAuthentication);

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();
    }

    @Test
    public void caseClosedEvent_shouldDeclareRecords()
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setContainer(new AcmContainer());
        caseFile.setCaseNumber("caseNumber");
        String user = "user";

        Capture<Authentication> captureAuth = Capture.newInstance();

        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        expect(mockAuthenticationManager.getAcmAuthentication(new UsernamePasswordAuthenticationToken(user, user)))
                .andReturn(new AcmAuthentication(Collections.emptySet(), user, "", true, user));

        CaseEvent caseEvent = new CaseEvent(caseFile, "ipAddress", user,
                AlfrescoRmaPluginConstants.CASE_CLOSED_EVENT, new Date(), true,
                new AcmAuthentication(Collections.emptySet(), user, "", true, user));

        mockService.declareAllContainerFilesAsRecords(
                capture(captureAuth),
                eq(caseFile.getContainer()),
                anyObject(Date.class),
                eq(caseFile.getCaseNumber()));

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();

        Authentication actual = captureAuth.getValue();
        assertEquals(user, actual.getName());
    }

}
