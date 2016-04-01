package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;

import org.json.JSONArray;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 9/19/2014.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class GetWidgetsByUserRolesAPIController
{

    private UserDao userDao;
    private AcmPlugin dashboardPlugin;
    private WidgetDao widgetDao;
    private DashboardPropertyReader dashboardPropertyReader;
    private WidgetEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Widget> getDashboardConfig(Authentication authentication, HttpSession session)
            throws AcmWidgetException, AcmObjectNotFoundException
    {

        String userId = authentication.getName();
        if (log.isInfoEnabled())
        {
            log.info("Finding widgets for user  based on the user roles'" + userId + "'");
        }

        List<AcmRole> roles = getUserDao().findAllRolesByUser(userId);
        if (roles == null)
        {
            throw new AcmObjectNotFoundException("user", null, "Object not found", null);
        }
        List<Widget> retval = null;
        boolean inserted = false;
        try
        {
            retval = onlyUniqueValues(getWidgetDao().getAllWidgetsByRoles(roles));
            raiseGetEvent(authentication, session, retval, true);
            List<Widget> dashboardWidgetsOnly = dashboardPropertyReader.getDashboardWidgetsOnly();
            return retval.stream().filter(w -> dashboardWidgetsOnly.contains(w)).collect(Collectors.toList());
        }
        catch (AcmObjectNotFoundException e)
        {
            // If there are no records for widgets into the DB ( when user logs in for the first time) we will read all widgets by user
            // roles from
            // config file .acm/dashboardPlugin.properties and that we will store them into the DB.
            if (retval == null)
            {
                retval = addAvailableWidgets(userId, authentication);
            }
            if (log.isInfoEnabled())
            {
                log.info("Roles added for User '" + userId + "' in DASHBOARD context to restrict available widgets.");
            }
            raiseGetEvent(authentication, session, retval, true);
            return retval;
        }
        catch (Exception e1)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while raising an event or while reading widgets values");
            }
            throw new AcmWidgetException("Get widgets by user roles exception", e1);
        }
    }

    private List<Widget> onlyUniqueValues(List<Widget> widgets)
    {
        Set<Widget> widgetSet = new HashSet<>();
        List<Widget> result = new ArrayList<>();
        for (Widget widget : widgets)
        {
            widgetSet.add(widget);
        }
        for (Widget widget : widgetSet)
        {
            result.add(widget);
        }
        return result;
    }

    private List<Widget> addAvailableWidgets(String userId, Authentication authentication)
    {
        List<Widget> retval = new ArrayList<>();
        Set<Widget> retvalSet = new HashSet<>();
        List<AcmRole> userRoles = userDao.findAllRolesByUser(userId);
        Set<String> widgetSet = new HashSet<>();
        String retVal = null;
        String[] widgetArray;

        if (!dashboardPlugin.getPluginProperties().isEmpty())
        {

            // get all widgets by roles from the property file and put them as strings in a Set Collection to avoid widget duplicates
            for (AcmRole role : userRoles)
            {
                Map<String, Object> dashboardPluginPluginProperties = dashboardPlugin.getPluginProperties();
                String jsonRoleWidgetsString = (String) dashboardPluginPluginProperties.get(DashboardConstants.ROLE_WIDGET_LIST);
                JSONArray jsonArray = new JSONArray(jsonRoleWidgetsString);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    if (role.getRoleName().equals(jsonArray.getJSONObject(i).getString(DashboardConstants.ROLE)))
                    {
                        retVal = jsonArray.getJSONObject(i).getString(DashboardConstants.WIDGET_LIST);
                        break;
                    }
                }

                if (retVal == null)
                {
                    continue;
                }

                widgetArray = retVal.split(DashboardConstants.COMMA_SPLITTER);

                for (String widget : widgetArray)
                {
                    widgetSet.add(widget.trim());
                }

                for (String widgetName : widgetSet)
                {

                    Widget widget = new Widget();
                    widget.setWidgetName(widgetName.trim());
                    widget = getWidgetDao().saveWidget(widget);
                    retvalSet.add(widget);
                    if (widget.getWidgetId() != null)
                    {
                        getEventPublisher().publishWidgetEvent(widget, authentication, true, true);
                        WidgetRole widgetRole;
                        widgetRole = addWidgetRoleIntoDB(widget, role);
                        getEventPublisher().publishWidgetRoleEvent(widgetRole, authentication, true, true);
                    }
                    else
                    {
                        WidgetRole widgetRole;
                        widgetRole = addWidgetRoleIntoDB(widget, role);
                        getEventPublisher().publishWidgetRoleEvent(widgetRole, authentication, true, true);
                    }
                }
                widgetSet.clear();
            }
        }
        retval.addAll(retvalSet);
        return retval;
    }

    private WidgetRole addWidgetRoleIntoDB(Widget widget, AcmRole role)
    {
        WidgetRole widgetRole = new WidgetRole();
        widgetRole.setWidgetId(widget.getWidgetId());
        widgetRole.setRoleName(role.getRoleName());
        return getWidgetDao().saveWidgetRole(widgetRole);
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
}
