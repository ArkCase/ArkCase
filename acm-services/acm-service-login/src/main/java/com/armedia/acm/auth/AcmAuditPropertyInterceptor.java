package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by armdev on 11/5/14.
 */
public class AcmAuditPropertyInterceptor extends HandlerInterceptorAdapter
{
    private Logger log = LogManager.getLogger(getClass());
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
            // no session yet - login must not have completed? Anyway, proceed with the next handler.
            return true;
        }

        String userid = (String) session.getAttribute("acm_username");
        log.debug("audit interceptor was called: user id is: [{}]", userid);

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
