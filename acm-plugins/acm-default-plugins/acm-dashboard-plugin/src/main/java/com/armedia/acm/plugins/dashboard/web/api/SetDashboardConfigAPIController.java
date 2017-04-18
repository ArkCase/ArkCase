package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by marst on 7/30/14.
 */

@Controller
@RequestMapping({"/api/v1/plugin/dashboard", "/api/latest/plugin/dashboard"})
public class SetDashboardConfigAPIController
{
    private DashboardService dashboardService;
    private DashboardPropertyReader dashboardPropertyReader;
    private DashboardEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public DashboardDto setDashboardConfig(
            @RequestBody DashboardDto updateDashboardDto,
            Authentication authentication,
            HttpSession session
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmDashboardException
    {
        String userId = authentication.getName();
        AcmUser user = dashboardService.getUserByUserId(userId);
        List<String> modules = dashboardPropertyReader.getModuleNameList();
        String moduleName;
        if (updateDashboardDto.getModule() != null)
        {
            moduleName = updateDashboardDto.getModule().trim();
        } else
        {
            moduleName = DashboardConstants.DASHBOARD_MODULE_NAME;
        }
        if (!modules.contains(moduleName))
        {
            throw new AcmDashboardException("Module name:" + moduleName + "does  not exist");
        }

        log.info("Updating dashboard configuration for user: [{}] '", userId);

        Dashboard d = null;
        try
        {
            //retval is the number of entities (dashboards) updated or deleted
            d = dashboardService.getDashboardConfigForUserAndModuleName(user, moduleName);
            int retval = dashboardService.setDashboardConfigForUserAndModule(user, updateDashboardDto, moduleName);
            if (retval != 1)
            {
                log.error("Unable to update dashboard config because dashboard for user: [{}] is not found", userId);
                throw new AcmObjectNotFoundException("dashboard", null, "Object not found", null);
            } else
            {
                getEventPublisher().publishDashboardEvent(d, authentication, false, true);
                updateDashboardDto.setUpdated(true);
                return updateDashboardDto;
            }
        } catch (Exception e)
        {
            getEventPublisher().publishDashboardEvent(d, authentication, false, false);
            throw new AcmUserActionFailedException("update", "dashboard", null, e.getMessage(), e);
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


