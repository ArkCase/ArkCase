package com.armedia.acm.web.api;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * Request filter that sets the RequestId, Remote address and User Id in {@link MDC}. Created by Bojan Milenkoski on 28.1.2016.
 */
public class RequestMDCFilter implements Filter
{
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, getClientIpAddress((HttpServletRequest) request));
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY,
                (String) ((HttpServletRequest) request).getSession().getAttribute("acm_username"));
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY,
                (String) ((HttpServletRequest) request).getSession().getAttribute("acm_alfresco_username"));

        // Pass request back down the filter chain
        chain.doFilter(request, response);
    }

    private String getClientIpAddress(HttpServletRequest request)
    {
        for (String header : HEADERS_TO_TRY)
        {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip))
            {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    @Override
    public void destroy()
    {
    }
}
