package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/19/2014.
 */

@Controller
@RequestMapping({"/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets"})
public class GetWidgetsByUserRoles {

    private UserDao userDao;
    private AcmPlugin dashboardPlugin;
    private WidgetDao widgetDao;
    private WidgetEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Widget> getDashboardConfig(Authentication authentication, HttpSession session) throws AcmWidgetException, AcmObjectNotFoundException {

        String userId = (String) authentication.getName().toLowerCase();
        if (log.isInfoEnabled()) {
            log.info("Finding widgets for user  based on the user roles'" + userId + "'");
        }

        List<AcmRole> roles = getUserDao().findAllRolesByUser(userId);
        if (roles == null) {
            throw new AcmObjectNotFoundException("user",null, "Object not found", null);
        }
        List<Widget> retval = null;
        DashboardDto dashboardDto;
        boolean inserted = false;
        try {
            retval = getWidgetDao().getAllWidgetsByRoles(roles);
            raiseGetEvent(authentication, session, retval, true);
            return retval;
        } catch (AcmObjectNotFoundException e) {
            // If there are no records for widgets into the DB ( when user logs in for the first time) we will read all widgets by user roles from
            // config file .acm/dashboardPlugin.properties and that values will store them into DB.
            if (retval == null) {
                retval = createWidgets(userId, authentication);
            }
            if (log.isInfoEnabled()) {
                log.info("Roles added for User '" + userId + "' in DASHBOARD context to restrict available widgets.");
            }
            raiseGetEvent(authentication, session, retval, true);
            return retval;
        } catch (Exception e1) {
            if(log.isErrorEnabled()) {
                log.error("Exception occurred while raising an event or while reading widgets values ");
            }
            throw new AcmWidgetException("Get widgets by user roles exception",e1);
        }

    }

    private List<Widget> createWidgets(String userId, Authentication authentication) {
        List<Widget> retval = null;

        //TODO add all readed widgets from the properties file into DB
        //TODO create WidgetRole EVENTS and raise them here also
        for (Widget widget : retval) {
            getEventPublisher().publishWidgetdEvent(widget, authentication, true, true);
        }
        return retval;
    }

    protected void raiseGetEvent(Authentication authentication, HttpSession session, List<Widget> foundWidgets, boolean succeeded) {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishGetWidgetsByUserRoles(foundWidgets, authentication, ipAddress, succeeded);
    }
    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public AcmPlugin getDashboardPlugin() {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin) {
        this.dashboardPlugin = dashboardPlugin;
    }

    public WidgetDao getWidgetDao() {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao) {
        this.widgetDao = widgetDao;
    }

    public WidgetEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
