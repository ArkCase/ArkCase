package com.armedia.acm.auth;


import com.armedia.acm.data.AuditPropertyEntityAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by armdev on 11/5/14.
 */
public class AcmAuditPropertyInterceptor extends HandlerInterceptorAdapter
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private AuditPropertyEntityAdapter entityAdapter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (log.isTraceEnabled())
        {
            log.trace("Setting user id for MVC request");
        }

        HttpSession session = request.getSession(false);

        if (session == null)
        {
            // no session yet - login must not have completed?  Anyway, proceed with the next handler.
            return true;
        }

        String userid = (String) session.getAttribute("acm_username");
        log.debug("audit interceptor was called: user id is: '" + userid + "'");

        getEntityAdapter().setUserId(userid);

        return true;

    }

    public AuditPropertyEntityAdapter getEntityAdapter()
    {
        return entityAdapter;
    }

    public void setEntityAdapter(AuditPropertyEntityAdapter entityAdapter)
    {
        this.entityAdapter = entityAdapter;
    }
}
