package com.armedia.acm.plugins.dashboard.service;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.dashboard.dao.ModuleDao;
import com.armedia.acm.plugins.dashboard.dao.UserPreferenceDao;
import com.armedia.acm.plugins.dashboard.dao.WidgetDao;
import com.armedia.acm.plugins.dashboard.model.module.Module;
import com.armedia.acm.plugins.dashboard.model.userPreference.PreferredWidgetsDto;
import com.armedia.acm.plugins.dashboard.model.userPreference.UserPreference;
import com.armedia.acm.plugins.dashboard.model.widget.Widget;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by marjan.stefanoski on 18.01.2016.
 */
public class UserPreferenceService
{
    private UserPreferenceDao userPreferenceDao;
    private UserDao userDao;
    private WidgetDao widgetDao;
    private ModuleDao moduleDao;
    private UserPreferenceEventPublisher userPreferenceEventPublisher;
    private DashboardPropertyReader dashboardPropertyReader;
    private Logger log = LoggerFactory.getLogger(getClass());


    public PreferredWidgetsDto updateUserPreferenceWidgets(String userId, PreferredWidgetsDto preferredWidgets, String ipAddress) throws AcmObjectNotFoundException
    {
        List<Widget> widgetList = createWidgetList(preferredWidgets.getPreferredWidgets());
        List<UserPreference> upList = null;
        Module module;
        AcmUser user;
        if (widgetList.isEmpty())
        {
            if (log.isWarnEnabled())
            {
                log.warn("No widgets are found!");
            }
            throw new AcmObjectNotFoundException("User Preference", null, "Widgets not found", null);
        }
        try
        {
            module = getModule(preferredWidgets.getModuleName());
        } catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Module with module name " + preferredWidgets.getModuleName() + " is not found" + e.getMessage(), e);
            }
            throw e;
        }
        user = userDao.findByUserId(userId);
        try
        {
            upList = getUserPreferenceListByUserAndModule(user, module);
        } catch (AcmObjectNotFoundException e)
        {
            if (log.isInfoEnabled())
            {
                log.info("No User Preference found for user: " + userId + " and module name: " + module.getModuleName());
            }
        }

        try
        {
            int i = deleteOldUserPreferenceByUserAndModule(user, module);

            if (log.isInfoEnabled())
            {
                log.info("Deleted " + i + " UserPreference records");
            }
            if (upList != null)
            {
                upList.stream().forEach(userPreference -> {
                    userPreferenceEventPublisher.publishUserPreferenceDeleted(userPreference, ipAddress, true);
                });
            }
        } catch (Exception e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Error occurred while deleting UserPreference records  " + e.getMessage(), e);
            }
            if (upList != null)
            {
                upList.stream().forEach(userPreference -> {
                    userPreferenceEventPublisher.publishUserPreferenceDeleted(userPreference, ipAddress, false);
                });
            }
        }

        saveNewUserPreference(user, widgetList, module, ipAddress);

        return preferredWidgets;
    }


    private int deleteOldUserPreferenceByUserAndModule(AcmUser user, Module module)
    {
        return userPreferenceDao.deleteAllUserPreferenceByUserIdAndModuleName(user.getUserId(), module.getModuleName());
    }

    public PreferredWidgetsDto getPreferredWidgetsByUserAndModule(String userId, String moduleName) throws AcmObjectNotFoundException
    {
        if (!dashboardPropertyReader.getModuleNameList().contains(moduleName))
        {
            if (log.isErrorEnabled())
            {
                log.error("Module with name: " + moduleName + " is not found!");
            }
            throw new AcmObjectNotFoundException("User Preference", null, "Module not found!", null);
        }
        PreferredWidgetsDto preferredWidgetsDto = new PreferredWidgetsDto();
        List<Widget> widgetList;
        List<String> widgetNamesList = new ArrayList<>();

        try
        {
            widgetList = userPreferenceDao.getUserPreferredListOfWidgetsByUserAndModuleName(userId, moduleName);
        } catch (AcmObjectNotFoundException e)
        {
            if (log.isInfoEnabled())
            {
                log.info("No preferred widgets for user: " + userId + " and module: " + moduleName);
            }
            widgetList = getAllAllowedWidgetsForUser(userId);
        }
        widgetList.stream().forEach(widget -> widgetNamesList.add(widget.getWidgetName()));
        preferredWidgetsDto.setModuleName(moduleName);
        preferredWidgetsDto.setPreferredWidgets(widgetNamesList);

        return preferredWidgetsDto;
    }

    private List<Widget> getAllAllowedWidgetsForUser(String userId) throws AcmObjectNotFoundException
    {
        List<Widget> result;
        List<AcmRole> roles = getUserDao().findAllRolesByUser(userId);
        try
        {
            result = onlyUniqueValues(widgetDao.getAllWidgetsByRoles(roles));
            if (log.isInfoEnabled() && !result.isEmpty())
            {
                log.info("All allowed widgets for the user: " + userId + " will be returned!");
            }
        } catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("No widgets are allowed for the user: " + userId + "! " + e.getMessage(), e);
            }
            throw e;
        }
        return result;
    }

    private List<Widget> onlyUniqueValues(List<Widget> widgets)
    {
        Set<Widget> widgetSet = new HashSet<>();
        List<Widget> result = new ArrayList<>();

        widgets.stream().forEach(widget -> widgetSet.add(widget));
        widgetSet.stream().forEach(widget -> result.add(widget));

        return result;
    }

    private List<UserPreference> getUserPreferenceListByUserAndModule(AcmUser user, Module module) throws AcmObjectNotFoundException
    {
        return userPreferenceDao.getUserPreferenceListByUserModuleName(user.getUserId(), module.getModuleName());
    }

    private void saveNewUserPreference(AcmUser user, List<Widget> widgetList, Module module, String ipAddress)
    {
        widgetList.stream().forEach(widget -> {
            UserPreference userPreference = new UserPreference();
            userPreference.setModule(module);
            userPreference.setUser(user);
            userPreference.setWidget(widget);
            try
            {
                userPreference = userPreferenceDao.save(userPreference);
                userPreferenceEventPublisher.publishUserPreferenceCreated(userPreference, ipAddress, true);
            } catch (Exception e)
            {
                userPreferenceEventPublisher.publishUserPreferenceCreated(userPreference, ipAddress, false);
            }
        });
    }

    private List<Widget> createWidgetList(List<String> widgetNamesList)
    {
        List<Widget> widgets = new ArrayList<>();
        widgetNamesList.stream().forEach(widgetName -> {
            try
            {
                Widget w = widgetDao.getWidgetByWidgetName(widgetName);
                widgets.add(w);
            } catch (AcmObjectNotFoundException e)
            {
                if (log.isWarnEnabled())
                {
                    log.warn("Widget with widget name " + widgetName + " is not found!");
                }
            }
        });
        return widgets;
    }

    private Module getModule(String moduleName) throws AcmObjectNotFoundException
    {
        return moduleDao.getModuleByName(moduleName);
    }

    public UserPreferenceDao getUserPreferenceDao()
    {
        return userPreferenceDao;
    }

    public void setUserPreferenceDao(UserPreferenceDao userPreferenceDao)
    {
        this.userPreferenceDao = userPreferenceDao;
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

    public ModuleDao getModuleDao()
    {
        return moduleDao;
    }

    public void setModuleDao(ModuleDao moduleDao)
    {
        this.moduleDao = moduleDao;
    }

    public UserPreferenceEventPublisher getUserPreferenceEventPublisher()
    {
        return userPreferenceEventPublisher;
    }

    public void setUserPreferenceEventPublisher(UserPreferenceEventPublisher userPreferenceEventPublisher)
    {
        this.userPreferenceEventPublisher = userPreferenceEventPublisher;
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
