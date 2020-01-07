package com.armedia.acm.plugins.audit.api;

/*-
 * #%L
 * ACM Default Plugin: Audit
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.model.AuditEventConfig;
import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.plugins.audit.model.AuditConstants;
import com.armedia.acm.plugins.audit.service.ReplaceEventTypeNames;
import com.armedia.acm.plugins.audit.web.api.GetAuditByObjectTypeAndObjectIdAPIController;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/spring-library-audit-plugin-test.xml" })
public class GetAuditByObjectTypeAndObjectIdAPIControllerTest
        extends EasyMockSupport
{
    String ipAddress = null;
    Long objectId = null;
    String objectType = null;
    String eventResult = null;
    String fullEventType = null;
    AuditEvent mockAuditEvent = null;
    QueryResultPageWithTotalCount<AuditEvent> mockExpected = null;
    String key = null;
    String sortBy = "";
    String sort = "";
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    private GetAuditByObjectTypeAndObjectIdAPIController unit;
    private AuditDao mockAuditDao;
    private Authentication mockAuthentication;
    private ReplaceEventTypeNames mockReplaceEventTypeNames;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;
    private AuditEventConfig auditEventConfigMock;
    private AuditConfig auditConfig;
    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        mockAuditDao = createMock(AuditDao.class);
        mockHttpSession = new MockHttpSession();
        mockAuthentication = createMock(Authentication.class);
        mockReplaceEventTypeNames = createMock(ReplaceEventTypeNames.class);
        auditEventConfigMock = createMock(AuditEventConfig.class);
        auditConfig = createMock(AuditConfig.class);
        unit = new GetAuditByObjectTypeAndObjectIdAPIController();
        unit.setAuditDao(mockAuditDao);
        unit.setReplaceEventTypeNames(mockReplaceEventTypeNames);
        unit.setAuditEventConfig(auditEventConfigMock);
        unit.setAuditConfig(auditConfig);

        mockMvc = MockMvcBuilders.standaloneSetup(unit).setHandlerExceptionResolvers(exceptionResolver).build();

        ipAddress = "ipAddress";
        objectId = 500L;
        objectType = "OBJECT_TYPE";
        eventResult = "eventResult";
        fullEventType = "fullEventType";
        sortBy = "eventDate";
        sort = "DESC";

        mockAuditEvent = setAuditEvent();

        mockExpected = new QueryResultPageWithTotalCount<>();
        mockExpected.setStartRow(0);
        mockExpected.setMaxRows(10);
        mockExpected.setTotalCount(1);
        mockExpected.setResultPage(Arrays.asList(mockAuditEvent));

        mockHttpSession.setAttribute("acm_ip_address", ipAddress);

        key = String.format("%s.%s", objectType, AuditConstants.HISTORY_TYPES);

    }

    public AuditEvent setAuditEvent()
    {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setObjectId(objectId);
        auditEvent.setObjectType(objectType);
        auditEvent.setIpAddress(ipAddress);
        auditEvent.setEventResult(eventResult);
        auditEvent.setFullEventType(fullEventType);
        auditEvent.setEventDate(new Date());
        return auditEvent;
    }

    @Test
    public void getEventsByObjectTypeAndObjectId() throws Exception
    {
        expect(auditConfig.getEventTypeByKey("OBJECT_TYPE"))
                .andReturn("com.armedia.acm.app.task.create, com.armedia.acm.casefile.created");
        executeTest(false);
    }

    @Test
    public void getEventsByObjectTypeAndObjectIdWhenEventTypesNull() throws Exception
    {
        expect(auditConfig.getEventTypeByKey("OBJECT_TYPE"))
                .andReturn(null);
        executeTest(true);
    }

    public void executeTest(boolean isEventTypesNull) throws Exception
    {
        Capture<List<String>> eventTypes = newCapture();

        expect(mockAuditDao.findPagedResults(eq(objectId), eq(objectType), eq(0), eq(10), capture(eventTypes), eq(sortBy), eq(sort)))
                .andReturn(Arrays.asList(mockAuditEvent));
        expect(mockAuditDao.countAll(eq(objectId), eq(objectType), capture(eventTypes))).andReturn(Arrays.asList(mockAuditEvent).size());
        expect(mockReplaceEventTypeNames.replaceNameInAcmEvent(mockAuditEvent)).andReturn(mockAuditEvent).anyTimes();
        // MVC test classes must call getName() somehow
        expect(mockAuthentication.getName()).andReturn("user");

        replayAll();

        MvcResult result = mockMvc.perform(
                get("/api/latest/plugin/audit/{objectType}/{objectId}", objectType, objectId)
                        .accept(MediaType.parseMediaType("application/json;charset=UTF-8")).session(mockHttpSession)
                        .principal(mockAuthentication))
                .andReturn();

        verifyAll();

        if (isEventTypesNull)
        {
            assertEquals(eventTypes.getValue(), null);
        }
        else
        {
            assertEquals(eventTypes.getValue().size(), 2);
        }
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));

        String jsonResult = result.getResponse().getContentAsString();

        log.info("results: " + jsonResult);

        String jsonExpected = new ObjectMapper().writeValueAsString(mockExpected);

        assertEquals(jsonExpected, jsonResult);
    }
}
