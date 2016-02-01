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
 * Request filter that sets the RequestId, Remote address and User Id in {@link MDC}.
 * Created by Bojan Milenkoski on 28.1.2016.
 */
public class RequestMDCFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, request.getRemoteAddr());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, (String) ((HttpServletRequest) request).getSession().getAttribute("acm_username"));

        // Pass request back down the filter chain
        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
    }
}
