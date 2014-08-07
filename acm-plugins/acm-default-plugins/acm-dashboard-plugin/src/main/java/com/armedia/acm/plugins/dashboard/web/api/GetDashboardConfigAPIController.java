package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
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

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DashboardDto getDashboardConfig(
            Authentication authentication,
            HttpSession session
    ) throws AcmDashboardException, AcmObjectNotFoundException {
        String userId = (String) authentication.getName();
        if (log.isInfoEnabled()) {
            log.info("Finding dashboard configuration for user '" + userId + "'");
        }
        AcmUser owner = userDao.findByUserId(userId);
        if (owner == null) {
            throw new AcmObjectNotFoundException("user",null, "Object not found", null);
        }
        Dashboard retval = null;
        DashboardDto dashboardDto;
        boolean inserted = false;
        try {
            retval = getDashboardDao().getDashboardConfigForUser(owner);
            raiseGetEvent(authentication, session, retval, true);
            dashboardDto = prepareDashboardDto(retval,inserted);
            return dashboardDto;
        } catch (AcmObjectNotFoundException e) {
            // If there is no record into the DB ( when user logs in for the first time) we will read defaultDashboard
            // config String from .acm/dashboardPlugin.properties and that value will be stored into
            // DB table acm-dashboard.
            if (retval == null) {
                retval = createDashbaord(owner);
                inserted = true;
                getEventPublisher().publishDashboardEvent(retval,authentication,true,true);
            }
            if (log.isInfoEnabled()) {
                log.info("User '" + userId + "'is logged in for the first time and default dashboard is inserted into the DB");
            }
            raiseGetEvent(authentication, session, retval, true);
            dashboardDto = prepareDashboardDto(retval,inserted);
            return dashboardDto;
        } catch (Exception e1) {
            if(log.isErrorEnabled()) {
                log.error("Exception occurred while raising an event or while reading values from fetched DB dashboard ");
            }
           throw new AcmDashboardException("Get dashboard exception",e1);
        }
    }

    private DashboardDto prepareDashboardDto(Dashboard dashboard, boolean inserted){
        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setUserId(dashboard.getDashboardOwner().getUserId());
        dashboardDto.setDashboardConfig(dashboard.getDashobardConfig());
        dashboardDto.setInserted(inserted);
        return dashboardDto;
    }

    private Dashboard createDashbaord(AcmUser owner) {
        Dashboard d = new Dashboard();
        d.setDashboardOwner(owner);
        if(!dashboardPlugin.getPluginProperties().isEmpty()) {
            d.setDashobardConfig((String) dashboardPlugin.getPluginProperties().get("acm.defaultDashboard"));
        } else {
              // to add <prop key="acm.deafultDashbolard">"some default long dashboard string"</prop> under
              // dashboardPluginProperties bean in spring-library-dashboard.xml and never get here?
            if(log.isInfoEnabled()) {
                log.info("dashboardPlugin.properties is missing, users will not have dashboard");
            }
        }
        return dashboardDao.save(d);
    }

    protected void raiseGetEvent(Authentication authentication, HttpSession session, Dashboard foundDashboard, boolean succeeded) {
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
