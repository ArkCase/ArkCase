package com.armedia.acm.plugins.dashboard.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.exception.AcmWidgetException;
import com.armedia.acm.plugins.dashboard.model.widget.RolesGroupByWidgetDto;
import com.armedia.acm.plugins.dashboard.service.DashboardService;
import com.armedia.acm.plugins.dashboard.service.WidgetEventPublisher;
import com.armedia.acm.services.users.dao.UserDao;

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
 * Created by marjan.stefanoski on 9/30/2014.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/dashboard/widgets", "/api/latest/plugin/dashboard/widgets" })
public class GetRolesByWidgetsAPIController
{

    private UserDao userDao;
    private WidgetDao widgetDao;
    private WidgetEventPublisher eventPublisher;
    private DashboardService dashboardService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/rolesByWidget/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<RolesGroupByWidgetDto> getRolesGroupedByWidget(Authentication authentication, HttpSession session)
            throws AcmWidgetException, AcmObjectNotFoundException
    {
        log.info("List of all, authorized and not authorized roles grouped by widget'");

        List<RolesGroupByWidgetDto> result = null;
        try
        {
            result = dashboardService.addNotAuthorizedRolesPerWidget(getWidgetDao().getRolesGroupByWidget());
            dashboardService.raiseGetEvent(authentication, session, result, true);
            return result;
        }
        catch (AcmObjectNotFoundException e)
        {
            throw new AcmWidgetException("No Roles per Widgets found", e);
        }
    }

    public WidgetDao getWidgetDao()
    {
        return widgetDao;
    }

    public void setWidgetDao(WidgetDao widgetDao)
    {
        this.widgetDao = widgetDao;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setDashboardService(DashboardService dashboardService)
    {
        this.dashboardService = dashboardService;
    }

    public DashboardService getDashboardService()
    {
        return dashboardService;
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