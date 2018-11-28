package com.armedia.acm.audit.listeners;

/*-
 * #%L
 * ACM Service: Audit Library
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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by Bojan Milenkoski on 29.1.2016.
 */
public class AcmAuditRequestInterceptorTest extends EasyMockSupport
{
    final UUID requestID = UUID.randomUUID();
    final String remoteAddress = "remote.address";
    final String userId = "userId";
    final String postMethod = "POST";
    final String getMethod = "GET";
    final String protocol = "http";
    final String uri = "test.com";
    final String queryString = "query=String";
    final String sessionId = "1234";
    final String headerName1 = "Cache-control";
    final String headerValue1 = "no-cache";
    final String headerName2 = "Connection";
    final String headerValue2 = "keep-alive";
    final String headerName3 = "Accept";
    final String headerValue3 = "text/plain";
    final Enumeration<String> headerNames = new Vector<>(Arrays.asList(headerName1, headerName2, headerName3)).elements();
    final String cookieName1 = "userId";
    final String cookieValue1 = "ann-acm";
    final String cookieName2 = "JSESSIONID";
    final String cookieValue2 = "123456";
    final Cookie[] cookies = { new Cookie(cookieName1, cookieValue1), new Cookie(cookieName2, cookieValue2) };
    final String body = "param: [value]";
    private AcmAuditRequestInterceptor interceptor;
    private AuditService mockAuditService;
    private HttpServletRequest mockRequest;

    @Before
    public void setUp() throws Exception
    {
        interceptor = new AcmAuditRequestInterceptor();
        mockRequest = createMock(HttpServletRequest.class);
        mockAuditService = createMock(AuditService.class);
        interceptor.setAuditService(mockAuditService);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, requestID.toString());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, remoteAddress);
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, userId);
    }

    @Test
    public void preHandleDoesNotAuditRequestWhenRequestLoggingDisabled() throws Exception
    {
        // given
        interceptor.setRequestsLoggingEnabled(false);
        interceptor.setRequestsLoggingHeadersEnabled(true);
        interceptor.setRequestsLoggingCookiesEnabled(true);
        interceptor.setRequestsLoggingBodyEnabled(true);

        // when
        replayAll();
        interceptor.preHandle(mockRequest, null, null);

        // then
        verifyAll();
    }

    @Test
    public void preHandleDoesNotAuditRequestHeadersWhenRequestHeadersLoggingDisabled() throws Exception
    {
        // given
        interceptor.setRequestsLoggingEnabled(true);
        interceptor.setRequestsLoggingHeadersEnabled(false);
        interceptor.setRequestsLoggingCookiesEnabled(false);
        interceptor.setRequestsLoggingBodyEnabled(false);

        expect(mockRequest.getMethod()).andReturn(postMethod);
        expect(mockRequest.getProtocol()).andReturn(protocol);
        expect(mockRequest.getRequestURI()).andReturn(uri);
        expect(mockRequest.getQueryString()).andReturn(queryString).anyTimes();
        // expect(mockRequest.getRequestedSessionId()).andReturn(sessionId).anyTimes();
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        interceptor.preHandle(mockRequest, null, null);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertEquals(remoteAddress, auditEvent.getIpAddress());
        assertEquals(requestID, auditEvent.getRequestId());
        assertEquals(userId, auditEvent.getUserId());
        assertEquals(AcmAuditRequestInterceptor.EVENT_TYPE, auditEvent.getFullEventType());
        assertEquals(AuditConstants.EVENT_RESULT_SUCCESS, auditEvent.getEventResult());
        assertEquals(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST, auditEvent.getObjectType());
        assertEquals(AuditConstants.EVENT_STATUS_COMPLETE, auditEvent.getStatus());
        assertEquals(postMethod, auditEvent.getEventProperties().get("Method"));
        assertEquals(protocol, auditEvent.getEventProperties().get("Protocol"));
        assertEquals(uri, auditEvent.getEventProperties().get("URI"));
        assertEquals(queryString, auditEvent.getEventProperties().get("QueryString"));
        assertNull(auditEvent.getEventProperties().get("Headers"));

        verifyAll();
    }

    @Test
    public void preHandleDoesNotAuditRequestCookiesWhenRequestCookiesLoggingDisabled() throws Exception
    {
        // given
        interceptor.setRequestsLoggingEnabled(true);
        interceptor.setRequestsLoggingHeadersEnabled(true);
        interceptor.setRequestsLoggingCookiesEnabled(false);
        interceptor.setRequestsLoggingBodyEnabled(false);

        expect(mockRequest.getMethod()).andReturn(postMethod);
        expect(mockRequest.getProtocol()).andReturn(protocol);
        expect(mockRequest.getRequestURI()).andReturn(uri);
        expect(mockRequest.getQueryString()).andReturn(queryString).anyTimes();
        expect(mockRequest.getHeaderNames()).andReturn(headerNames);
        expect(mockRequest.getHeader(headerName1)).andReturn(headerValue1);
        expect(mockRequest.getHeader(headerName2)).andReturn(headerValue2);
        expect(mockRequest.getHeader(headerName3)).andReturn(headerValue3);
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        interceptor.preHandle(mockRequest, null, null);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertEquals(remoteAddress, auditEvent.getIpAddress());
        assertEquals(requestID, auditEvent.getRequestId());
        assertEquals(userId, auditEvent.getUserId());
        assertEquals(AcmAuditRequestInterceptor.EVENT_TYPE, auditEvent.getFullEventType());
        assertEquals(AuditConstants.EVENT_RESULT_SUCCESS, auditEvent.getEventResult());
        assertEquals(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST, auditEvent.getObjectType());
        assertEquals(AuditConstants.EVENT_STATUS_COMPLETE, auditEvent.getStatus());
        assertEquals(postMethod, auditEvent.getEventProperties().get("Method"));
        assertEquals(protocol, auditEvent.getEventProperties().get("Protocol"));
        assertEquals(uri, auditEvent.getEventProperties().get("URI"));
        assertEquals(queryString, auditEvent.getEventProperties().get("QueryString"));
        assertEquals(headerName1 + "=" + headerValue1 + ";" + headerName2 + "=" + headerValue2 + ";" + headerName3 + "=" + headerValue3,
                auditEvent.getEventProperties().get("Headers"));
        assertNull(auditEvent.getEventProperties().get("Cookies"));

        verifyAll();
    }

    @Test
    public void preHandleDoesNotAuditRequestBodyWhenRequestBodyLoggingDisabled() throws Exception
    {
        // given
        interceptor.setRequestsLoggingEnabled(true);
        interceptor.setRequestsLoggingHeadersEnabled(true);
        interceptor.setRequestsLoggingCookiesEnabled(true);
        interceptor.setRequestsLoggingBodyEnabled(false);

        expect(mockRequest.getMethod()).andReturn(postMethod);
        expect(mockRequest.getProtocol()).andReturn(protocol);
        expect(mockRequest.getRequestURI()).andReturn(uri);
        expect(mockRequest.getQueryString()).andReturn(queryString).anyTimes();
        expect(mockRequest.getHeaderNames()).andReturn(headerNames);
        expect(mockRequest.getHeader(headerName1)).andReturn(headerValue1);
        expect(mockRequest.getHeader(headerName2)).andReturn(headerValue2);
        expect(mockRequest.getHeader(headerName3)).andReturn(headerValue3);
        expect(mockRequest.getCookies()).andReturn(cookies).anyTimes();
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        interceptor.preHandle(mockRequest, null, null);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertEquals(remoteAddress, auditEvent.getIpAddress());
        assertEquals(requestID, auditEvent.getRequestId());
        assertEquals(userId, auditEvent.getUserId());
        assertEquals(AcmAuditRequestInterceptor.EVENT_TYPE, auditEvent.getFullEventType());
        assertEquals(AuditConstants.EVENT_RESULT_SUCCESS, auditEvent.getEventResult());
        assertEquals(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST, auditEvent.getObjectType());
        assertEquals(AuditConstants.EVENT_STATUS_COMPLETE, auditEvent.getStatus());
        assertEquals(postMethod, auditEvent.getEventProperties().get("Method"));
        assertEquals(protocol, auditEvent.getEventProperties().get("Protocol"));
        assertEquals(uri, auditEvent.getEventProperties().get("URI"));
        assertEquals(queryString, auditEvent.getEventProperties().get("QueryString"));
        assertEquals(headerName1 + "=" + headerValue1 + ";" + headerName2 + "=" + headerValue2 + ";" + headerName3 + "=" + headerValue3,
                auditEvent.getEventProperties().get("Headers"));
        assertEquals(cookieName1 + "=" + cookieValue1 + ";" + cookieName2 + "=" + cookieValue2,
                auditEvent.getEventProperties().get("Cookies"));
        assertNull(auditEvent.getEventProperties().get("Body"));

        verifyAll();
    }

    @Test
    public void preHandleAuditsBodyInRequestWhenPOST() throws Exception
    {
        // given
        interceptor.setRequestsLoggingEnabled(true);
        interceptor.setRequestsLoggingHeadersEnabled(true);
        interceptor.setRequestsLoggingCookiesEnabled(true);
        interceptor.setRequestsLoggingBodyEnabled(true);

        expect(mockRequest.getMethod()).andReturn(postMethod).anyTimes();
        expect(mockRequest.getProtocol()).andReturn(protocol);
        expect(mockRequest.getRequestURI()).andReturn(uri);
        expect(mockRequest.getQueryString()).andReturn(queryString).anyTimes();
        expect(mockRequest.getHeaderNames()).andReturn(headerNames);
        expect(mockRequest.getHeader(headerName1)).andReturn(headerValue1);
        expect(mockRequest.getHeader(headerName2)).andReturn(headerValue2);
        expect(mockRequest.getHeader(headerName3)).andReturn(headerValue3);
        expect(mockRequest.getCookies()).andReturn(cookies).anyTimes();
        HashMap<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("param", new String[] { "value" });
        expect(mockRequest.getParameterMap()).andReturn(parameterMap);
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        interceptor.preHandle(mockRequest, null, null);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertEquals(remoteAddress, auditEvent.getIpAddress());
        assertEquals(requestID, auditEvent.getRequestId());
        assertEquals(userId, auditEvent.getUserId());
        assertEquals(AcmAuditRequestInterceptor.EVENT_TYPE, auditEvent.getFullEventType());
        assertEquals(AuditConstants.EVENT_RESULT_SUCCESS, auditEvent.getEventResult());
        assertEquals(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST, auditEvent.getObjectType());
        assertEquals(AuditConstants.EVENT_STATUS_COMPLETE, auditEvent.getStatus());
        assertEquals(postMethod, auditEvent.getEventProperties().get("Method"));
        assertEquals(protocol, auditEvent.getEventProperties().get("Protocol"));
        assertEquals(uri, auditEvent.getEventProperties().get("URI"));
        assertEquals(queryString, auditEvent.getEventProperties().get("QueryString"));
        assertEquals(headerName1 + "=" + headerValue1 + ";" + headerName2 + "=" + headerValue2 + ";" + headerName3 + "=" + headerValue3,
                auditEvent.getEventProperties().get("Headers"));
        assertEquals(cookieName1 + "=" + cookieValue1 + ";" + cookieName2 + "=" + cookieValue2,
                auditEvent.getEventProperties().get("Cookies"));
        assertEquals(body, auditEvent.getEventProperties().get("Body"));
        verifyAll();
    }

    @Test
    public void preHandleDoesNotAuditBodyInRequestWhenGET() throws Exception
    {
        // given
        interceptor.setRequestsLoggingEnabled(true);
        interceptor.setRequestsLoggingHeadersEnabled(true);
        interceptor.setRequestsLoggingCookiesEnabled(true);
        interceptor.setRequestsLoggingBodyEnabled(true);

        expect(mockRequest.getMethod()).andReturn(getMethod).anyTimes();
        expect(mockRequest.getProtocol()).andReturn(protocol);
        expect(mockRequest.getRequestURI()).andReturn(uri);
        expect(mockRequest.getQueryString()).andReturn(queryString).anyTimes();
        expect(mockRequest.getHeaderNames()).andReturn(headerNames);
        expect(mockRequest.getHeader(headerName1)).andReturn(headerValue1);
        expect(mockRequest.getHeader(headerName2)).andReturn(headerValue2);
        expect(mockRequest.getHeader(headerName3)).andReturn(headerValue3);
        expect(mockRequest.getCookies()).andReturn(cookies).anyTimes();
        Capture<AuditEvent> capturedAuditEvent = newCapture();
        mockAuditService.audit(capture(capturedAuditEvent));
        expectLastCall();

        // when
        replayAll();
        interceptor.preHandle(mockRequest, null, null);

        // then
        AuditEvent auditEvent = capturedAuditEvent.getValue();
        assertEquals(remoteAddress, auditEvent.getIpAddress());
        assertEquals(requestID, auditEvent.getRequestId());
        assertEquals(userId, auditEvent.getUserId());
        assertEquals(AcmAuditRequestInterceptor.EVENT_TYPE, auditEvent.getFullEventType());
        assertEquals(AuditConstants.EVENT_RESULT_SUCCESS, auditEvent.getEventResult());
        assertEquals(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST, auditEvent.getObjectType());
        assertEquals(AuditConstants.EVENT_STATUS_COMPLETE, auditEvent.getStatus());
        assertEquals(getMethod, auditEvent.getEventProperties().get("Method"));
        assertEquals(protocol, auditEvent.getEventProperties().get("Protocol"));
        assertEquals(uri, auditEvent.getEventProperties().get("URI"));
        assertEquals(queryString, auditEvent.getEventProperties().get("QueryString"));
        assertEquals(headerName1 + "=" + headerValue1 + ";" + headerName2 + "=" + headerValue2 + ";" + headerName3 + "=" + headerValue3,
                auditEvent.getEventProperties().get("Headers"));
        assertEquals(cookieName1 + "=" + cookieValue1 + ";" + cookieName2 + "=" + cookieValue2,
                auditEvent.getEventProperties().get("Cookies"));
        assertNull(auditEvent.getEventProperties().get("Body"));
        verifyAll();
    }
}
