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

import com.armedia.acm.audit.model.AuditConfig;
import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.MDC;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Intercepts requests and logs the request details in the audit log.
 * <p>
 * Created by Bojan Milenkoski on 12.1.2016.
 */
public class AcmAuditRequestInterceptor extends HandlerInterceptorAdapter
{
    public static final String EVENT_TYPE = "com.armedia.acm.audit.request";
    private Logger log = LogManager.getLogger(getClass());
    private AuditService auditService;
    private AuditConfig auditConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        log.trace("Request audit interceptor called");

        if (auditConfig.getRequestsLoggingEnabled())
        {
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setEventDate(new Date());
            auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            auditEvent.setRequestId(UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
            auditEvent.setFullEventType(EVENT_TYPE);
            auditEvent.setEventResult(AuditConstants.EVENT_RESULT_SUCCESS);
            auditEvent.setObjectType(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST);
            auditEvent.setStatus(AuditConstants.EVENT_STATUS_COMPLETE);
            auditEvent.setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY) != null
                    ? MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY)
                    : AuditConstants.USER_ID_ANONYMOUS);
            auditEvent.setEventProperties(getEventProperties(request));

            log.trace("Request created AuditEvent: {}", auditEvent.toString());

            getAuditService().audit(auditEvent);
        }

        return true;
    }

    private Map<String, String> getEventProperties(HttpServletRequest request) throws IOException
    {
        Map<String, String> eventProperties = new HashMap<>();

        eventProperties.put("Method", request.getMethod());
        eventProperties.put("Protocol", request.getProtocol());
        eventProperties.put("URI", request.getRequestURI());
        if (request.getQueryString() != null)
        {
            eventProperties.put("QueryString", request.getQueryString());
        }

        // Due to security concerns about session hijacking the sessionId should not be logged
        // if (request.getRequestedSessionId() != null)
        // {
        // eventProperties.put("SessionId", request.getRequestedSessionId());
        // }

        // headers
        if (auditConfig.getRequestsLoggingHeadersEnabled())
        {
            String headers = getRequestHeadersAsString(request);
            if (headers.length() > 0)
            {
                eventProperties.put("Headers", headers);
            }
        }

        // cookies
        if (auditConfig.getRequestsLoggingCookiesEnabled() && (request.getCookies() != null))
        {
            String cookies = getCookiesAsString(request);
            if (cookies.length() > 0)
            {
                eventProperties.put("Cookies", cookies);
            }
        }

        // body
        if (auditConfig.getRequestsLoggingBodyEnabled() && "POST".equalsIgnoreCase(request.getMethod()))
        {
            Map<String, String[]> parameterMap = request.getParameterMap();
            StringBuilder parameters = new StringBuilder();
            String separator = "";
            for (String parameterKey : parameterMap.keySet())
            {
                parameters.append(separator);
                separator = "|";
                parameters.append(parameterKey).append(": [");
                parameters.append(String.join(", ", parameterMap.get(parameterKey)));
                parameters.append("]");
            }

            if (request instanceof MultipartHttpServletRequest)
            {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

                for (Map.Entry<String, List<MultipartFile>> entry : multipartRequest.getMultiFileMap().entrySet())
                {
                    String paramName = entry.getKey();

                    List<MultipartFile> multipartFiles = entry.getValue();
                    for (int i = 0; i < multipartFiles.size(); i++)
                    {
                        MultipartFile multipartFile = multipartFiles.get(i);
                        parameters.append(separator);
                        separator = "|";
                        parameters.append(paramName).append("[").append(multipartFile.getOriginalFilename()).append("]").append(": [");
                        if (getAuditConfig().getContentTypesToLog().contains(multipartFile.getContentType()))
                        {
                            parameters.append(new String(multipartFile.getBytes(), Charset.forName("UTF-8")));
                        }
                        else
                        {
                            parameters.append("Content not logged for contenttype=").append(multipartFile.getContentType());
                        }
                        parameters.append("]");
                    }
                }
            }

            eventProperties.put("Body", parameters.toString());
        }

        return eventProperties;

    }

    private String getCookiesAsString(HttpServletRequest request)
    {
        StringBuilder cookies = new StringBuilder();
        String separator = "";
        for (Cookie cookie : request.getCookies())
        {
            cookies.append(separator);
            String cookieName = cookie.getName();
            String cookieValue = cookie.getValue();
            cookies.append(cookieName).append("=").append(cookieValue);
            separator = ";";
        }
        return cookies.toString();
    }

    private String getRequestHeadersAsString(HttpServletRequest request)
    {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        String separator = "";
        while (headerNames.hasMoreElements())
        {
            headers.append(separator);
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append("=").append(headerValue);
            separator = ";";
        }
        return headers.toString();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        MDC.clear();
    }

    public AuditService getAuditService()
    {
        return auditService;
    }

    public void setAuditService(AuditService auditService)
    {
        this.auditService = auditService;
    }

    public AuditConfig getAuditConfig()
    {
        return auditConfig;
    }

    public void setAuditConfig(AuditConfig auditConfig)
    {
        this.auditConfig = auditConfig;
    }
}
