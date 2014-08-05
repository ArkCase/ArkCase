package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by marst on 7/30/14.
 */

    @Controller
    @RequestMapping({ "/api/v1/plugin/dashboard", "/api/latest/plugin/dashboard" })
    public class SetDashboardConfigAPIController {

        private DashboardDao dashboardDao;
        private DashboardEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

        @RequestMapping(value = "/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public Dashboard setDashboardConfig(
                @RequestBody Dashboard newDashboard,
                Authentication authentication,
                HttpSession session
        ) throws AcmObjectNotFoundException, AcmUserActionFailedException {
            String userId = (String) authentication.getName();
            if ( log.isInfoEnabled()) {
                log.info("Updating dashboard configuration for user '" + userId + "'");
            }
            try {
                //retval is the number of entities (dashboards) updated or deleted
                int retval = getDashboardDao().setDasboardConfigForUser(userId, newDashboard);
                if (retval != 1) {
                    throw new AcmObjectNotFoundException("dashboard",null, "Object not found", null);
                } else {
                    getEventPublisher().publishDashboardEvent(newDashboard,authentication,false,true);
                    return newDashboard;
                }
            }
            catch (Exception e) {
                getEventPublisher().publishDashboardEvent(newDashboard, authentication, false, false);
                throw new AcmUserActionFailedException("update", "dashboard", newDashboard.getDashboardId(), e.getMessage(), e);
            }
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


