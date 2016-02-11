package com.armedia.acm.web.api;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Servlet Filter where we wrap the HTTP request with custom wrapper {@link MultiReadHttpServletRequest} and pass it back to filter chain.
 * This way when ever request.getInputStream() gets called it will call the overrided method {@link MultiReadHttpServletRequest#getInputStream()},
 * which returns the same input stream which is already read by the wrapper
 * <p>
 * Created by Bojan Milenkoski on 15.1.2016.
 */
public class RequestWrapperFilter implements Filter
{
    @Override
    public void init(FilterConfig config) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest((HttpServletRequest) request);
        // Pass request back down the filter chain
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy()
    {
    }
}

