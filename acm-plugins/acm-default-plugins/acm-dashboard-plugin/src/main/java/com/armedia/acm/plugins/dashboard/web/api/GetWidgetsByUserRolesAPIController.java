package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 9/19/2014.
 */

@Controller
@RequestMapping({"/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets"})
public class GetWidgetsByUserRolesAPIController
{

    private UserDao userDao;
    private AcmPlugin dashboardPlugin;
    private WidgetDao widgetDao;
    private DashboardPropertyReader dashboardPropertyReader;
    private WidgetEventPublisher eventPublisher;
    private DashboardService dashboardService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Widget> getWidgetsByUserAndRoles(Authentication authentication, HttpSession session)
            throws AcmWidgetException, AcmObjectNotFoundException
    {

        String userId = authentication.getName();

        log.info("Finding widgets for user: [{}]  based on the user roles'", userId);

        List<AcmRole> roles = getUserDao().findAllRolesByUser(userId);
        if (roles == null)
        {
            throw new AcmObjectNotFoundException("user", null, "Object not found", null);
        }
        List<Widget> retval = null;
        try
        {
            retval = dashboardService.onlyUniqueValues(getWidgetDao().getAllWidgetsByRoles(roles));
            raiseGetEvent(authentication, session, retval, true);
            List<Widget> dashboardWidgetsOnly = dashboardPropertyReader.getDashboardWidgetsOnly();
            List<Widget> result = retval.stream().filter(w -> dashboardWidgetsOnly.contains(w)).collect(Collectors.toList());
            return result;
        } catch (AcmObjectNotFoundException e)
        {
            log.error("Widgets by roles associated to user: [{}] not found! ", userId, e, e.getMessage());
            raiseGetEvent(authentication, session, retval, true);
            return new ArrayList<>();
        } catch (Exception e1)
        {
            log.error("Exception occurred while raising an event or while reading widgets values");
            throw new AcmWidgetException("Get widgets by user roles exception", e1);
        }
    }

    protected void raiseGetEvent(Authentication authentication, HttpSession session, List<Widget> foundWidgets, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishGetWidgetsByUserRoles(foundWidgets, authentication, ipAddress, succeeded);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AcmPlugin getDashboardPlugin()
    {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin)
    {
        this.dashboardPlugin = dashboardPlugin;
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public WidgetEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public DashboardPropertyReader getDashboardPropertyReader()
    {
        return dashboardPropertyReader;
    }

    public void setDashboardPropertyReader(DashboardPropertyReader dashboardPropertyReader)
    {
        this.dashboardPropertyReader = dashboardPropertyReader;
    }

    public DashboardService getDashboardService()
    {
        return dashboardService;
    }

    public void setDashboardService(DashboardService dashboardService)
    {
        this.dashboardService = dashboardService;
    }
}
