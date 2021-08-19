package com.armedia.acm.plugins.dashboard.service;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConfig;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.service.AcmUserRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.armedia.acm.services.users.model.AcmRoleType.APPLICATION_ROLE;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class DashboardService
{
    private transient static final Logger log = LogManager.getLogger(DashboardService.class);
    private DashboardConfig dashboardConfig;
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

        String defaultDashboard = dashboardConfig.getDefaultDashboard();
        if (StringUtils.isNotBlank(defaultDashboard))
        {
            if (moduleName.equals(DashboardConstants.DASHBOARD_MODULE_NAME))
            {
                d.setDashboardConfig(defaultDashboard);
            }
            else
            {
                String defaultModuleDashboardConfig = dashboardConfig.getModuleDefaultDashboard();
                if (StringUtils.isNotBlank(defaultModuleDashboardConfig))
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

        Dashboard dashboard = dashboardDao.getDashboardConfigForUserAndModuleName(user, moduleName);

        String dashboardModifiedString = dashboard.getDashboardConfig();
        Set<String> roles = userRoleService.getUserRoles(userId);

        List<Widget> result = onlyUniqueValues(widgetDao.getAllWidgetsByRoles(roles));
        List<Widget> listOfDashboardWidgetsOnly = dashboardPropertyReader.getDashboardWidgetsOnly();
        List<Widget> dashboardWidgetsOnly = result.stream()
                .filter(listOfDashboardWidgetsOnly::contains)
                .collect(Collectors.toList());

        JSONObject dashboardJSONObject = new JSONObject(dashboard.getDashboardConfig());

        dashboardModifiedString = removeNotAuthorizedWidgets(dashboardJSONObject, dashboardWidgetsOnly);

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

        List<AcmRole> allRoles = getUserDao().findAllRolesByRoleType(APPLICATION_ROLE);
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

    public List<WidgetRoleName> getRolesByWidgetPaged(String widgetName, String sortBy, String sortDirection, Integer startRow,
            Integer maxRows,
            Boolean authorized, List<RolesGroupByWidgetDto> rolesGroupsPerWidget) throws AcmWidgetException
    {
        return getRolesGroupsPaged(rolesGroupsPerWidget, widgetName, sortBy, sortDirection, startRow, maxRows, authorized, "");
    }

    public List<WidgetRoleName> getRolesByWidget(String widgetName, String sortBy, String sortDirection, Integer startRow,
            Integer maxRows, String filterName, Boolean authorized, List<RolesGroupByWidgetDto> rolesGroupsPerWidget)
            throws AcmWidgetException
    {
        return getRolesGroupsPaged(rolesGroupsPerWidget, widgetName, sortBy, sortDirection, startRow, maxRows, authorized, filterName);
    }

    public List<WidgetRoleName> getRolesGroupsPaged(List<RolesGroupByWidgetDto> rolesGroupsPerWidget, String widgetName,
            String sortBy,
            String sortDirection, Integer startRow, Integer maxRows, Boolean authorized, String filterName) throws AcmWidgetException
    {
        List<RolesGroupByWidgetDto> rolesGroupsByWidgetDto = addNotAuthorizedRolesPerWidget(rolesGroupsPerWidget);
        RolesGroupByWidgetDto roleGroupByWidgetDto = rolesGroupsByWidgetDto.stream()
                .filter(roleGroup -> roleGroup.getWidgetName().equalsIgnoreCase(widgetName)).findFirst()
                .orElseThrow(() -> new AcmWidgetException("There are no roles/groups for the widget " + widgetName));

        List<WidgetRoleName> result = null;

        if (authorized)
        {
            result = new ArrayList<>(roleGroupByWidgetDto.getWidgetAuthorizedRoles());
        }
        else
        {
            result = new ArrayList<>(roleGroupByWidgetDto.getWidgetNotAuthorizedRoles());
        }

        if (sortDirection.contains("DESC"))
        {
            Collections.sort(result, Collections.reverseOrder());
        }
        else
        {
            Collections.sort(result);
        }

        if (startRow > result.size())
        {
            return result;
        }
        maxRows = maxRows > result.size() ? result.size() : maxRows;

        if (!filterName.isEmpty())
        {
            result.removeIf(widgetRoleName -> !(widgetRoleName.getName().toLowerCase().contains(filterName.toLowerCase())));
        }

        return result.stream().skip(startRow).limit(maxRows).collect(Collectors.toList());
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

    public WidgetEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public DashboardConfig getDashboardConfig()
    {
        return dashboardConfig;
    }

    public void setDashboardConfig(DashboardConfig dashboardConfig)
    {
        this.dashboardConfig = dashboardConfig;
    }
}
