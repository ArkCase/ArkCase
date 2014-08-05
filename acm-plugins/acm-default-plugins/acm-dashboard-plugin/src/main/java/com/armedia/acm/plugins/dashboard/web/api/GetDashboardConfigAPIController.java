package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marst on 7/29/14.
 */

@Controller
@RequestMapping({"/api/v1/plugin/dashboard", "/api/latest/plugin/dashboard"})
public class GetDashboardConfigAPIController {

    private DashboardDao dashboardDao;
    private DashboardEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get/{user}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Dashboard getDashboardConfig(
            @PathVariable("user") String user,
            Authentication authentication,
            HttpSession session
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Finding dashboard configuration for user '" + user + "'");
        }
        try {
            Dashboard retval = getDashboardDao().getDashboardConfigForUser(user);
            if (retval == null) {
                throw new AcmObjectNotFoundException("dashboard", null, "Object not found", null);
            }
            raiseEvent(authentication, session, retval, true);
            return retval;
        } catch (Exception e) {
            // make a fake dashboard so the event will have the desired userId (dashboard owner ID) and object type
            Dashboard fakeDashboard = new Dashboard();
            AcmUser fakeAcmUser = new AcmUser();
            fakeAcmUser.setUserId(user);
            fakeDashboard.setDashboardOwner(fakeAcmUser);
            raiseEvent(authentication, session, fakeDashboard, false);
            throw new AcmUserActionFailedException("get", "dashboard", null, e.getMessage(), e);
        }
    }
    protected void raiseEvent(Authentication authentication, HttpSession session, Dashboard foundDashboard, boolean succeeded) {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishGetDashboardByUserIdEvent(foundDashboard, authentication, ipAddress, succeeded);
    }
    public DashboardDao getDashboardDao() {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    public DashboardEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(DashboardEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
