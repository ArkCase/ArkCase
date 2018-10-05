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
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileModifiedEvent;
import com.armedia.acm.plugins.ecm.model.AcmContainer;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.Date;
import java.util.Properties;

public class AcmCaseFileStatusChangedListenerTest extends EasyMockSupport
{
    private AcmCaseFileStatusChangedListener unit;
    private AlfrescoRecordsService mockService;
    private AcmAuthenticationManager mockAuthenticationManager;

    @Before
    public void setUp()
    {
        unit = new AcmCaseFileStatusChangedListener();
        mockService = createMock(AlfrescoRecordsService.class);
        mockAuthenticationManager = createMock(AcmAuthenticationManager.class);

        unit.setAlfrescoRecordsService(mockService);
        unit.setAuthenticationManager(mockAuthenticationManager);
    }

    @Test
    public void doNotProceed_shouldNotDeclareRecords()
    {
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.FALSE);

        CaseFileModifiedEvent caseEvent = new CaseFileModifiedEvent(new CaseFile());
        caseEvent.setEventStatus("status.changed");

        replayAll();

        unit.onApplicationEvent(caseEvent);

        verifyAll();
    }

    @Test
    public void statusChangeEvent_notClosed_shouldNotDeclareRecords()
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setContainer(new AcmContainer());
        caseFile.setCaseNumber("caseNumber");
        caseFile.setStatus("open");

        Properties p = new Properties();
        p.setProperty("alfresco_rma_case_closed_statuses", "closed");
        unit.setAlfrescoRecordsService(mockService);

        expect(mockService.getAlfrescoRmaProperties()).andReturn(p);
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);

        CaseFileModifiedEvent caseEvent = new CaseFileModifiedEvent(caseFile);
        caseEvent.setEventStatus("status.changed");

        replayAll();

        unit.afterPropertiesSet();
        unit.onApplicationEvent(caseEvent);

        verifyAll();

    }

    @Test
    public void statusChangeEvent_closed_shouldDeclareRecords()
    {
        CaseFile caseFile = new CaseFile();
        caseFile.setContainer(new AcmContainer());
        caseFile.setCaseNumber("caseNumber");
        caseFile.setStatus("closed");
        String user = "user";

        Capture<Authentication> captureAuth = Capture.newInstance();

        Properties p = new Properties();
        p.setProperty("alfresco_rma_case_closed_statuses", "closed");
        unit.setAlfrescoRecordsService(mockService);

        expect(mockService.getAlfrescoRmaProperties()).andReturn(p);
        expect(mockService.checkIntegrationEnabled(AlfrescoRmaPluginConstants.CASE_CLOSE_INTEGRATION_KEY)).andReturn(Boolean.TRUE);
        expect(mockAuthenticationManager.getAcmAuthentication(new UsernamePasswordAuthenticationToken(user, user)))
                .andReturn(new AcmAuthentication(Collections.emptySet(), user, "", true, user, 0L));

        mockService.declareAllContainerFilesAsRecords(
                capture(captureAuth),
                eq(caseFile.getContainer()),
                anyObject(Date.class),
                eq(caseFile.getCaseNumber()));

        CaseFileModifiedEvent caseEvent = new CaseFileModifiedEvent(caseFile);
        caseEvent.setEventStatus("status.changed");
        caseEvent.setUserId(user);

        replayAll();

        unit.afterPropertiesSet();
        unit.onApplicationEvent(caseEvent);

        verifyAll();

        Authentication actual = captureAuth.getValue();

        assertEquals(user, actual.getName());

    }

}
