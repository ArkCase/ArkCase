package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.AcmUserRoleService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class DashboardService
{
    private transient static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private AcmPlugin dashboardPlugin;
    private DashboardDao dashboardDao;
    private UserDao userDao;
    private WidgetDao widgetDao;
    private DashboardPropertyReader dashboardPropertyReader;
    private AcmUserRoleService userRoleService;
    private WidgetEventPublisher eventPublisher;

    public static String removeWidgetsFromJson(JSONObject dashboardJson, Set<String> widgetNames)
    {
        try
        {
            JSONArray rows = dashboardJson.getJSONArray(DashboardConstants.DASHBOARD_ROWS);

            for (int i = 0; i < rows.length(); i++)
            {
                JSONArray columns = rows.getJSONObject(i).getJSONArray(DashboardConstants.DASHBOARD_COLUMNS);
                for (int j = 0; j < columns.length(); j++)
                {
                    JSONArray widgets = columns.getJSONObject(j).getJSONArray(DashboardConstants.DASHBOARD_WIDGETS);

                    for (int k = widgets.length() - 1; k >= 0; k--)
                    {
                        String widgetName = widgets.getJSONObject(k).getString(DashboardConstants.DASHBOARD_WIDGET_TYPE);
                        if (!widgetNames.contains(widgetName))
                        {
                            widgets.remove(k);
                        }
                    }
                }
            }
        }
        catch (JSONException e)
        {
            log.warn("JSON configuration can not be parsed. {}", e.getMessage());
        }

        return dashboardJson.toString();
    }

    public Dashboard getDashboardConfigForUserAndModuleName(AcmUser owner, String moduleName) throws AcmObjectNotFoundException
    {
        if (DashboardConstants.DASHBOARD_MODULE_NAME.equals(moduleName))
        {
            return prepareDashboardStringBasedOnUserRoles(owner.getUserId(), moduleName);
        }
        else
        {
            return getDashboardDao().getDashboardConfigForUserAndModuleName(owner, moduleName);
        }
    }

    public int setDashboardConfigForUserAndModule(AcmUser user, DashboardDto updateDashboardDto, String moduleName)
    {
        return dashboardDao.setDashboardConfigForUserAndModule(user, updateDashboardDto, moduleName);
    }

    public DashboardDto prepareDashboardDto(Dashboard dashboard, boolean inserted, String module)
    {
        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setUserId(dashboard.getDashboardOwner().getUserId());
        dashboardDto.setDashboardConfig(removeHashKeyValues(dashboard.getDashboardConfig()));
        dashboardDto.setInserted(inserted);
        dashboardDto.setModule(module);
        dashboardDto.setCollapsed(dashboard.getCollapsed());
        return dashboardDto;
    }

    public AcmUser getUserByUserId(String userId)
    {
        return userDao.findByUserId(userId);
    }

    private String removeHashKeyValues(String dashboardConfigWithHashValues)
    {
        // the regex ",\"\\$\\$hashKey\":\"\\w+\"" is used in replaceAll(...) method to remove
        // all ,"$$hashKey":"00A" like strings added by angularjs into dashboard config json string.
        return dashboardConfigWithHashValues.replaceAll(",\"\\$\\$hashKey\":\"\\w+\"", "");
    }

    public Dashboard createDefaultModuleDashboard(AcmUser owner, String moduleName)
    {
        Dashboard d = new Dashboard();
        d.setDashboardOwner(owner);
        d.setModuleName(moduleName);
        d.setCollapsed(Boolean.FALSE);
        if (!dashboardPlugin.getPluginProperties().isEmpty())
        {
            if (moduleName.equals(DashboardConstants.DEFAULT_DASHBOARD_NAME))
            {
                d.setDashboardConfig((String) dashboardPlugin.getPluginProperties().get(DashboardConstants.DEFAULT_DASHBOARD));
            }
            else
            {
                String defaultModuleDashboardConfig = (String) dashboardPlugin.getPluginProperties()
                        .get(DashboardConstants.DEFAULT_MODULE_DASHBOARD);
                if (defaultModuleDashboardConfig != null)
                {
                    d.setDashboardConfig(defaultModuleDashboardConfig);
                }
            }
        }
        else
        {
            // to add <prop key="acm.deafultDashbolard">"some default long dashboard string"</prop> under
            // dashboardPluginProperties bean in spring-library-dashboard.xml and never get here?
            log.info("dashboardPlugin.properties is missing, users will not have dashboard");
        }
        return dashboardDao.save(d);
    }

    public Dashboard prepareDashboardStringBasedOnUserRoles(String userId, String moduleName) throws AcmObjectNotFoundException
    {
        AcmUser user = userDao.findByUserId(userId);
        Dashboard dashboard;
        try
        {
            dashboard = dashboardDao.getDashboardConfigForUserAndModuleName(user, moduleName);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw e;
        }
        String dashboardModifiedString = dashboard.getDashboardConfig();
        Set<String> roles = userRoleService.getUserRoles(userId);
        try
        {
            List<Widget> result = onlyUniqueValues(widgetDao.getAllWidgetsByRoles(roles));
            List<Widget> listOfDashboardWidgetsOnly = dashboardPropertyReader.getDashboardWidgetsOnly();
            List<Widget> dashboardWidgetsOnly = result.stream().filter(w -> listOfDashboardWidgetsOnly.contains(w))
                    .collect(Collectors.toList());

            JSONObject dashboardJSONObject = new JSONObject(dashboard.getDashboardConfig());

            dashboardModifiedString = removeNotAuthorizedWidgets(dashboardJSONObject, dashboardWidgetsOnly);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.info("There are no widgets associated with roles of the user: {}", userId);
        }
        dashboard.setDashboardConfig(dashboardModifiedString);
        DashboardDto dashboardDto = prepareDashboardDto(dashboard, false, moduleName);
        int retval = setDashboardConfigForUserAndModule(user, dashboardDto, moduleName);
        if (retval != 1)
        {
            log.error("Unable to update dashboard config because dashboard for user: [{}] is not found", userId);
            throw new AcmObjectNotFoundException("dashboard", null, "Object not found", null);
        }
        return dashboardDao.getDashboardConfigForUserAndModuleName(user, moduleName);
    }

    private String removeNotAuthorizedWidgets(JSONObject dashboardJSONObject, List<Widget> dashboardWidgetsOnly)
    {
        // these 3 loops iterate over dashboard config JSON string/object, removing widgets that are not allowed
        // to be rendered on the UI side due to changes in widget roles relations.

        Set<String> dashboardWidgetNames = dashboardWidgetsOnly.stream()
                .map(Widget::getWidgetName)
                .collect(Collectors.toSet());
        return removeWidgetsFromJson(dashboardJSONObject, dashboardWidgetNames);
    }

    public List<Widget> onlyUniqueValues(List<Widget> widgets)
    {
        return widgets.stream().distinct().collect(Collectors.toList());
    }

    public void raiseGetEvent(Authentication authentication, HttpSession session, List<RolesGroupByWidgetDto> rolesPerWidgets,
            boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishGeRolesByWidgets(rolesPerWidgets, authentication, ipAddress, succeeded);
    }

    public List<RolesGroupByWidgetDto> addNotAuthorizedRolesPerWidget(List<RolesGroupByWidgetDto> rolesPerWidget)
    {
        List<AcmRole> allRoles = getUserDao().findAllRoles();
        List<Widget> allWidgets = getWidgetDao().findAll();
        List<WidgetRoleName> notAuthorizedWidgetRoleNames = new ArrayList<>();
        List<RolesGroupByWidgetDto> tmpRolesPerWidget = new ArrayList<>();
        boolean isAddedToRolesGroupByWidgetList = false;
        for (RolesGroupByWidgetDto rolePerW : rolesPerWidget)
        {
            rolePerW.setName(widgetName(rolePerW.getWidgetName()));
            for (AcmRole role : allRoles)
            {
                if (rolePerW.getWidgetAuthorizedRoles().stream().noneMatch(roleName -> roleName.getName().equals(role.getRoleName())))
                {
                    notAuthorizedWidgetRoleNames.add(new WidgetRoleName(role.getRoleName()));
                }
            }
            rolePerW.setWidgetNotAuthorizedRoles(notAuthorizedWidgetRoleNames);
            notAuthorizedWidgetRoleNames = new ArrayList<>();
        }
        for (Widget widget : allWidgets)
        {
            for (RolesGroupByWidgetDto roleW : rolesPerWidget)
            {
                if (roleW.getWidgetName().equals(widget.getWidgetName()))
                {
                    tmpRolesPerWidget.add(roleW);
                    isAddedToRolesGroupByWidgetList = true;
                    break;
                }
            }
            if (!isAddedToRolesGroupByWidgetList)
            {
                RolesGroupByWidgetDto rolesGBW = new RolesGroupByWidgetDto();
                rolesGBW.setWidgetName(widget.getWidgetName());
                rolesGBW.setName(widgetName(widget.getWidgetName()));
                List<WidgetRoleName> notAuth = new ArrayList<>();
                for (AcmRole role : allRoles)
                {
                    notAuth.add(new WidgetRoleName(role.getRoleName()));
                }
                rolesGBW.setWidgetNotAuthorizedRoles(notAuth);
                rolesGBW.setWidgetAuthorizedRoles(new ArrayList<>());
                tmpRolesPerWidget.add(rolesGBW);
            }
            isAddedToRolesGroupByWidgetList = false;
        }
        return tmpRolesPerWidget;
    }

    private String widgetName(String camelName)
    {
        StringBuffer stringBuffer = new StringBuffer();
        // create sentence from camelString
        for (String w : camelName.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
        {
            stringBuffer.append(w.substring(0, 1).toUpperCase() + w.substring(1));
            stringBuffer.append(" ");
        }
        return stringBuffer.toString();
    }

    public AcmPlugin getDashboardPlugin()
    {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin)
    {
        this.dashboardPlugin = dashboardPlugin;
    }

    public DashboardDao getDashboardDao()
    {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao)
    {
        this.dashboardDao = dashboardDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public DashboardPropertyReader getDashboardPropertyReader()
    {
        return dashboardPropertyReader;
    }

    public void setDashboardPropertyReader(DashboardPropertyReader dashboardPropertyReader)
    {
        this.dashboardPropertyReader = dashboardPropertyReader;
    }

    public void setUserRoleService(AcmUserRoleService userRoleService)
    {
        this.userRoleService = userRoleService;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public WidgetEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }
}
