package com.armedia.acm.audit.listeners;

import com.armedia.acm.audit.model.AuditConstants;
import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.audit.service.AuditService;
import com.armedia.acm.web.api.MDCConstants;
import com.google.common.io.CharStreams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Intercepts requests and logs the request details in the audit log.
 * <p>
 * Created by Bojan Milenkoski on 12.1.2016.
 */
public class AcmAuditRequestInterceptor extends HandlerInterceptorAdapter
{
    private Logger log = LoggerFactory.getLogger(getClass());

    public static final String EVENT_TYPE = "com.armedia.acm.audit.request";

    private AuditService auditService;
    private boolean requestsLoggingEnabled;
    private boolean requestsLoggingHeadersEnabled;
    private boolean requestsLoggingCookiesEnabled;
    private boolean requestsLoggingBodyEnabled;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        log.debug("Request audit interceptor called");

        if (isRequestsLoggingEnabled())
        {
            AuditEvent auditEvent = new AuditEvent();
            auditEvent.setEventDate(new Date());
            auditEvent.setIpAddress(MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY));
            auditEvent.setRequestId(UUID.fromString(MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY)));
            auditEvent.setFullEventType(EVENT_TYPE);
            auditEvent.setEventResult(AuditConstants.EVENT_RESULT_SUCCESS);
            auditEvent.setObjectType(AuditConstants.EVENT_OBJECT_TYPE_WEB_REQUEST);
            auditEvent.setStatus(AuditConstants.EVENT_STATUS_COMPLETE);

            // user Id
            HttpSession session = request.getSession(false);
            if (session != null)
            {
                auditEvent.setUserId(MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY));
            }
            if (auditEvent.getUserId() == null || auditEvent.getUserId().trim().isEmpty())
            {
                auditEvent.setUserId("anonymous");
            }

            // event properties
            Map<String, String> eventProperties = new HashMap<>();

            eventProperties.put("Method", request.getMethod());
            eventProperties.put("Protocol", request.getProtocol());
            eventProperties.put("URI", request.getRequestURI());
            if (request.getQueryString() != null)
            {
                eventProperties.put("QueryString", request.getQueryString());
            }
            if (request.getRequestedSessionId() != null)
            {
                eventProperties.put("SessionId", request.getRequestedSessionId());
            }

            // headers
            if (isRequestsLoggingHeadersEnabled())
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
                if (headers != null && headers.length() > 0)
                {
                    eventProperties.put("Headers", headers.toString());
                }
            }

            // cookies
            if (isRequestsLoggingCookiesEnabled() && (request.getCookies() != null))
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
                if (cookies.length() > 0)
                {
                    eventProperties.put("Cookies", cookies.toString());
                }
            }

            // body
            if (isRequestsLoggingBodyEnabled() && "POST".equalsIgnoreCase(request.getMethod()) && (request.getHeader("content-type") == null
                    || (request.getHeader("content-type") != null && !request.getHeader("content-type").contains("multipart/form-data"))))
            {
                eventProperties.put("Body", CharStreams.toString(request.getReader()));
            }

            auditEvent.setEventProperties(eventProperties);

            if (log.isTraceEnabled())
            {
                log.trace("Request created AuditEvent: " + auditEvent.toString());
            }

            getAuditService().audit(auditEvent);
        }

        return true;
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

    public boolean isRequestsLoggingEnabled()
    {
        return requestsLoggingEnabled;
    }

    public void setRequestsLoggingEnabled(boolean requestsLoggingEnabled)
    {
        this.requestsLoggingEnabled = requestsLoggingEnabled;
    }

    public boolean isRequestsLoggingHeadersEnabled()
    {
        return requestsLoggingHeadersEnabled;
    }

    public void setRequestsLoggingHeadersEnabled(boolean requestsLoggingHeadersEnabled)
    {
        this.requestsLoggingHeadersEnabled = requestsLoggingHeadersEnabled;
    }

    public boolean isRequestsLoggingCookiesEnabled()
    {
        return requestsLoggingCookiesEnabled;
    }

    public void setRequestsLoggingCookiesEnabled(boolean requestsLoggingCookiesEnabled)
    {
        this.requestsLoggingCookiesEnabled = requestsLoggingCookiesEnabled;
    }

    public boolean isRequestsLoggingBodyEnabled()
    {
        return requestsLoggingBodyEnabled;
    }

    public void setRequestsLoggingBodyEnabled(boolean requestsLoggingBodyEnabled)
    {
        this.requestsLoggingBodyEnabled = requestsLoggingBodyEnabled;
    }
}
