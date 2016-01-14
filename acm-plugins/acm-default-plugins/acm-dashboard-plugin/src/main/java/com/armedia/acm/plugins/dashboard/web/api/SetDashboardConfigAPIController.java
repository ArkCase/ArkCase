package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.exception.AcmDashboardException;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.plugins.dashboard.service.DashboardEventPublisher;
import com.armedia.acm.plugins.dashboard.service.DashboardPropertyReader;
import com.armedia.acm.services.users.dao.ldap.UserDao;
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

    private UserDao userDao;
    private DashboardDao dashboardDao;
    private DashboardPropertyReader dashboardPropertyReader;
    private DashboardEventPublisher eventPublisher;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/set", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DashboardDto setDashboardConfig(
            @RequestBody DashboardDto updateDashboardDto,
            Authentication authentication,
            HttpSession session
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmDashboardException
    {
        String userId = (String) authentication.getName();
        AcmUser user = userDao.findByUserId(userId);
        List<String> modules = dashboardPropertyReader.getModuleNameList();
        String moduleName = null;
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

        if (log.isInfoEnabled())
        {
            log.info("Updating dashboard configuration for user '" + userId + "'");
        }
        Dashboard d = null;
        try
        {
            //retval is the number of entities (dashboards) updated or deleted
            d = dashboardDao.getDashboardConfigForUserAndModuleName(user, moduleName);
            int retval = getDashboardDao().setDasboardConfigForUserAndModule(user, updateDashboardDto, moduleName);
            if (retval != 1)
            {
                if (log.isErrorEnabled())
                {
                    log.error("Unable to update dashboard config because dashboard for user: " + userId + "is not found");
                }
                throw new AcmObjectNotFoundException("dashboard", null, "Object not found", null);
            } else
            {
                getEventPublisher().publishDashboardEvent(d, authentication, false, true);
                updateDashboardDto.setUpdated(true);
                return updateDashboardDto;
            }
        } catch (AcmDashboardException de)
        {
            //This should never happen because if this code is executed that means that  user is already
            // authenticated and present into system,  so this situation is anomaly
            if (log.isErrorEnabled())
            {
                log.error("Unable to update dashboard config because user: " + userId + "is not found");
            }
            throw new AcmObjectNotFoundException("dashboard", null, "Object not found", de);
        } catch (Exception e)
        {
            getEventPublisher().publishDashboardEvent(d, authentication, false, false);
            throw new AcmUserActionFailedException("update", "dashboard", null, e.getMessage(), e);
        }
    }

    public DashboardDao getDashboardDao()
    {
        return dashboardDao;
    }

    public void setDashboardDao(DashboardDao dashboardDao)
    {
        this.dashboardDao = dashboardDao;
    }

    public DashboardEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(DashboardEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
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


