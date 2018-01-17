package com.armedia.acm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * By default, form login will answer a successful authentication request with a 301 MOVED PERMANENTLY status code; this
 * makes sense in the
 * context of an actual login form which needs to redirect after login. For a RESTful web service however, the desired
 * response for a
 * successful authentication should be 200 OK.
 * <p>
 * Created by Bojan Milenkoski on 06.4.2016
 */
public class AcmRestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException
    {
        log.debug("REST authentication successful. Authentication details is of type: {}",
                (authentication.getDetails() == null ? null : authentication.getDetails().getClass().getName()));

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null)
        {
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrlParam = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam))))
        {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);
    }

    /**
     * @param requestCache
     *            the requestCache to set
     */
    public void setRequestCache(RequestCache requestCache)
    {
        this.requestCache = requestCache;
    }
}
