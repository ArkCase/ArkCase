package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.dashboard.dao.DashboardDao;
import com.armedia.acm.plugins.dashboard.model.Dashboard;
import com.armedia.acm.plugins.dashboard.model.DashboardConstants;
import com.armedia.acm.plugins.dashboard.model.DashboardDto;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by marjan.stefanoski on 19.01.2016.
 */
public class DashboardService
{
    private AcmPlugin dashboardPlugin;
    private DashboardDao dashboardDao;
    private UserDao userDao;
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public Dashboard getDashboardConfigForUserAndModuleName(AcmUser owner, String moduleName) throws AcmObjectNotFoundException
    {
        return getDashboardDao().getDashboardConfigForUserAndModuleName(owner, moduleName);
    }

    public int setDashboardConfigForUserAndModule(AcmUser user, DashboardDto updateDashboardDto, String moduleName)
    {
        return getDashboardDao().setDashboardConfigForUserAndModule(user, updateDashboardDto, moduleName);
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
        //the regex ",\"\\$\\$hashKey\":\"\\w+\"" is used in replaceAll(...) method to remove
        //all ,"$$hashKey":"00A" like strings added by  angularjs into dashboard config json string.
        return dashboardConfigWithHashValues.replaceAll(",\"\\$\\$hashKey\":\"\\w+\"", "");
    }

    public Dashboard createDefaultModuleDashboard(AcmUser owner, String moduleName)
    {
        Dashboard d = new Dashboard();
        d.setDashboardOwner(owner);
        d.setModuleName(moduleName);
        d.setCollapsed(new Boolean(false));
        if (!dashboardPlugin.getPluginProperties().isEmpty())
        {
            if(moduleName.equals(DashboardConstants.DEFAULT_DASHBOARD_NAME)) {
                d.setDashboardConfig((String) dashboardPlugin.getPluginProperties().get(DashboardConstants.DEFAULT_DASHBOARD));
            } else {
                String defaultModuleDashboardConfig = (String) dashboardPlugin.getPluginProperties().get(DashboardConstants.DEFAULT_MODULE_DASHBOARD);
                if(defaultModuleDashboardConfig != null) {
                    d.setDashboardConfig(defaultModuleDashboardConfig);
                }
            }
        } else
        {
            // to add <prop key="acm.deafultDashbolard">"some default long dashboard string"</prop> under
            // dashboardPluginProperties bean in spring-library-dashboard.xml and never get here?
            if (log.isInfoEnabled())
            {
                log.info("dashboardPlugin.properties is missing, users will not have dashboard");
            }
        }
        return dashboardDao.save(d);
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
}
