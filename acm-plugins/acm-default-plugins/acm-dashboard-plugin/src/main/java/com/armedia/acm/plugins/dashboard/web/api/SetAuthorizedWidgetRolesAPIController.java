package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRole;
import com.armedia.acm.plugins.dashboard.model.widget.WidgetRoleName;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/2/2014.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class SetAuthorizedWidgetRolesAPIController
{

    private UserDao userDao;
    private WidgetDao widgetDao;
    private WidgetEventPublisher eventPublisher;

    private DashboardService dashboardService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/set", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public RolesGroupByWidgetDto setAuthorizedWidgetRoles(
            @RequestBody RolesGroupByWidgetDto updateAuthorizedWidgetRoles,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        String userId = (String) authentication.getName();

        log.info("Updating authorized roles for dashboard widget: [{}]", updateAuthorizedWidgetRoles.getWidgetName());

        RolesGroupByWidgetDto result = null;
        try
        {
            result = updateWidgetRolesAuthorization(updateAuthorizedWidgetRoles);
            raiseSetEvent(authentication, session, result, true);
            return result;
        }
        catch (AcmUserActionFailedException e)
        {
            throw e;
        }
    }

    @RequestMapping(value = "/roleGroupToWidget", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public RolesGroupByWidgetDto setAuthorizedWidgetRoles(
            @RequestBody List<String> rolesGroups,
            @RequestParam(value = "widgetName") String widgetName,
            @RequestParam(value = "authorized") Boolean authorized,
            @RequestParam(value = "sortBy", required = false, defaultValue = "widgetName") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            Authentication authentication,
            HttpSession session) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        RolesGroupByWidgetDto result;
        List<RolesGroupByWidgetDto> rolesGroupsByWidgetDto;
        RolesGroupByWidgetDto roleGroupByWidgetDtoUpdated;
        try
        {
            rolesGroupsByWidgetDto = dashboardService
                    .addNotAuthorizedRolesPerWidget(getWidgetDao().getRolesGroupByWidget());
            roleGroupByWidgetDtoUpdated = rolesGroupsByWidgetDto.stream()
                    .filter(roleGroup -> roleGroup.getWidgetName().equalsIgnoreCase(widgetName)).findFirst()
                    .orElse(null);

            if (authorized)
            {
                rolesGroups.forEach(roleGroup -> {
                    roleGroupByWidgetDtoUpdated.getWidgetAuthorizedRoles().add(new WidgetRoleName(roleGroup));
                    roleGroupByWidgetDtoUpdated.getWidgetNotAuthorizedRoles().removeIf(rg -> rg.getName().equalsIgnoreCase(roleGroup));
                });
            }
            else
            {
                rolesGroups.forEach(roleGroup -> {
                    roleGroupByWidgetDtoUpdated.getWidgetNotAuthorizedRoles().add(new WidgetRoleName(roleGroup));
                    roleGroupByWidgetDtoUpdated.getWidgetAuthorizedRoles().removeIf(rg -> rg.getName().equalsIgnoreCase(roleGroup));
                });
            }
            log.info("Updating authorized roles for dashboard widget: [{}]", roleGroupByWidgetDtoUpdated.getWidgetName());

            result = updateWidgetRolesAuthorization(roleGroupByWidgetDtoUpdated);
            raiseSetEvent(authentication, session, result, true);
            return result;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    protected void raiseSetEvent(Authentication authentication, HttpSession session, RolesGroupByWidgetDto rolesPerWidget,
            boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishSetAuthorizedWidgetRolesEvent(rolesPerWidget, authentication, ipAddress, succeeded);
    }

    protected RolesGroupByWidgetDto updateWidgetRolesAuthorization(RolesGroupByWidgetDto rolesGroupByWidgetDto)
            throws AcmUserActionFailedException
    {
        int i = getWidgetDao().deleteAllWidgetRolesByWidgetName(rolesGroupByWidgetDto.getWidgetName());

        log.info("Deleted " + i + " WidgetRoles");

        List<AcmRole> allRoles = getUserDao().findAllRoles();
        List<AcmRole> rolesForUpdate = new ArrayList<>();

        for (WidgetRoleName roleName : rolesGroupByWidgetDto.getWidgetAuthorizedRoles())
        {
            for (AcmRole role : allRoles)
            {
                if (role.getRoleName().equals(roleName.getName()))
                {
                    rolesForUpdate.add(role);
                    break;
                }
            }
        }
        try
        {
            addRolesToAWidgetByWidgetNameAndRoles(rolesGroupByWidgetDto.getWidgetName(), rolesForUpdate);
            return rolesGroupByWidgetDto;
        }
        catch (AcmWidgetException e)
        {
            log.error("Updating Authorized Roles for widget: [{}] ", rolesGroupByWidgetDto.getWidgetName(), e);
            throw new AcmUserActionFailedException("Update Authorized Roles for a Widget", "Dashboard", null, e.getMessage(), e);
        }
    }

    protected void addRolesToAWidgetByWidgetNameAndRoles(String widgetName, List<AcmRole> roles) throws AcmWidgetException
    {
        WidgetRole widgetRole = new WidgetRole();
        Widget widget = null;
        try
        {
            widget = getWidgetDao().getWidgetByWidgetName(widgetName);
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmWidgetException("Widget " + widgetName + " is not found", e);
        }
        for (AcmRole role : roles)
        {
            widgetRole.setRoleName(role.getRoleName());
            widgetRole.setWidgetId(widget.getWidgetId());
            getWidgetDao().saveWidgetRole(widgetRole);
            widgetRole = new WidgetRole();
        }
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

    public WidgetEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(WidgetEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
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
