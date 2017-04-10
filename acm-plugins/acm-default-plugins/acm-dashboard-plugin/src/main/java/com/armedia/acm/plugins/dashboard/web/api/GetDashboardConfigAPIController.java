package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by marst on 7/29/14.
 */

@Controller
@RequestMapping({"/api/v1/plugin/dashboard", "/api/latest/plugin/dashboard"})
public class GetDashboardConfigAPIController
{

    private AcmPlugin dashboardPlugin;
    private DashboardPropertyReader dashboardPropertyReader;
    private DashboardEventPublisher eventPublisher;
    private DashboardService dashboardService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DashboardDto getDashboardConfig(
            @RequestParam(value = "moduleName", required = false, defaultValue = "DASHBOARD") String moduleName,
            Authentication authentication, HttpSession session) throws AcmDashboardException, AcmObjectNotFoundException
    {
        String userId = authentication.getName();
        log.info("Finding dashboard configuration for user: [{}]", userId);
        AcmUser owner = dashboardService.getUserByUserId(userId);
        if (owner == null)
        {
            throw new AcmObjectNotFoundException("user", null, "Object not found", null);
        }
        Dashboard retval = null;
        DashboardDto dashboardDto;
        boolean inserted = false;
        try
        {
            List<String> modules = dashboardPropertyReader.getModuleNameList();
            if (!modules.contains(moduleName.trim()))
            {
                throw new AcmDashboardException("Module name:" + moduleName + " does  not exist");
            }

            retval = dashboardService.getDashboardConfigForUserAndModuleName(owner, moduleName);
            dashboardDto = dashboardService.prepareDashboardDto(retval, inserted, moduleName);
            return dashboardDto;
        } catch (AcmObjectNotFoundException e)
        {
            if (dashboardPropertyReader.getModuleNameList().contains(moduleName))
            {

                // If there is no record into the DB ( when user logs in for the first time) we will read defaultDashboard
                // config String from .acm/dashboardPlugin.properties and that value will be stored into
                // DB table acm-dashboard.
                if (retval == null)
                {
                    retval = dashboardService.createDefaultModuleDashboard(owner, moduleName);
                    inserted = true;
                    getEventPublisher().publishDashboardEvent(retval, authentication, true, true);
                }
                log.info("Module dashboard config for user [{}] and module [{}] is inserted into the DB", userId, moduleName);
                dashboardDto = dashboardService.prepareDashboardDto(retval, inserted, moduleName);
                return dashboardDto;
            } else
            {
                log.warn("Module dashboard config for user: [{}] and module: [{}] NOT inserted into the DB, the moduleName does NOT exists", userId, moduleName, moduleName, e);
                throw e;
            }
        } catch (Exception e1)
        {
            log.error("Exception occurred while raising an event or while reading values from fetched DB dashboard ", e1);
            throw new AcmDashboardException("Get dashboard exception", e1);
        }
    }

    public DashboardEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(DashboardEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public AcmPlugin getDashboardPlugin()
    {
        return dashboardPlugin;
    }

    public void setDashboardPlugin(AcmPlugin dashboardPlugin)
    {
        this.dashboardPlugin = dashboardPlugin;
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
