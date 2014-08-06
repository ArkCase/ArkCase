package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
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



    private UserDao userDao;
    private AcmPlugin dashboardPlugin;
    private DashboardDao dashboardDao;



    private DashboardEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get/{user}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Dashboard getDashboardConfig(
            @PathVariable("user") String user,
            Authentication authentication,
            HttpSession session
    ) throws AcmDashboardException {
        if (log.isInfoEnabled()) {
            log.info("Finding dashboard configuration for user '" + user + "'");
        }
        AcmUser owner = userDao.findByUserId(user);
        Dashboard retval = null;
        try {
            //If there is no record into the DB we will read defaultDashboard config String from .acm/dashboardPlugin.properties
            retval = getDashboardDao().getDashboardConfigForUser(owner);
            if (retval == null) {
                retval = new Dashboard();
                retval.setDashboardOwner(userDao.findByUserId(user));
                retval.setDashobardConfig((String)dashboardPlugin.getPluginProperties().get("acm.defaultDashboard"));
                retval = dashboardDao.save(retval);
            }
            raiseEvent(authentication, session, retval, true);
            return retval;
        } catch (AcmDashboardException e) {

            if (retval == null) {

                retval = new Dashboard();
                retval.setDashboardOwner(owner);
                retval.setDashobardConfig((String)dashboardPlugin.getPluginProperties().get("acm.defaultDashboard"));
                retval = dashboardDao.save(retval);
            }
            raiseEvent(authentication, session, retval, true);
            return retval;
        } catch (Exception e1) {
           throw new AcmDashboardException("Event exception",e1);
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

    public AcmPlugin getDashboardPlugin() {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin) {
        this.dashboardPlugin = dashboardPlugin;
    }
    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
