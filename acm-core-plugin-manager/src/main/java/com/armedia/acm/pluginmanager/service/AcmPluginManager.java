package com.armedia.acm.pluginmanager.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AcmPluginManager implements ApplicationContextAware
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Collection<AcmPlugin> acmPlugins = new ArrayList<>();
    private Collection<AcmPlugin> enabledNavigatorPlugins = new ArrayList<>();
    private Map<String, List<String>> privilegesByRole = new HashMap<>();
    private List<AcmPluginUrlPrivilege> urlPrivileges = new ArrayList<>();

    public synchronized Collection<AcmPlugin> getAcmPlugins()
    {
        return Collections.unmodifiableCollection(acmPlugins);
    }

    public synchronized void registerPlugin(AcmPlugin plugin)
    {
        acmPlugins.add(plugin);

        checkForNavigatorTab(plugin);

        addPluginPrivileges(plugin);

        addPluginUrlPrivileges(plugin);
    }

    private void addPluginUrlPrivileges(AcmPlugin plugin)
    {
        if ( plugin.getUrlPrivileges() != null )
        {
            urlPrivileges.addAll(plugin.getUrlPrivileges());
        }
    }

    private void addPluginPrivileges(AcmPlugin plugin)
    {
        if ( plugin.getPrivileges() != null )
        {
            for ( AcmPluginPrivilege privilege : plugin.getPrivileges() )
            {
                if ( privilege.getApplicationRolesWithPrivilege() != null )
                {
                    mapRolesToPrivileges(privilege);
                }
            }
        }
    }

    private void mapRolesToPrivileges(AcmPluginPrivilege privilege)
    {
        for ( String role : privilege.getApplicationRolesWithPrivilege() )
        {
            List<String> rolePrivileges = privilegesByRole.get(role);
            rolePrivileges = rolePrivileges == null ? new ArrayList<String>() : rolePrivileges;
            String privilegeName = privilege.getPrivilegeName();
            addPrivilegeIfNecessary(role, rolePrivileges, privilegeName);
        }
    }

    private void addPrivilegeIfNecessary(String role, List<String> rolePrivileges, String privilegeName)
    {
        if ( ! rolePrivileges.contains(privilegeName) )
        {
            rolePrivileges.add(privilegeName);
            privilegesByRole.put(role, rolePrivileges);
        }
    }

    private void checkForNavigatorTab(AcmPlugin plugin)
    {
        if ( plugin.isNavigatorTab() && plugin.isEnabled() )
        {
            if ( log.isDebugEnabled() )
            {
                log.debug("Adding navigator plugin " + plugin.getPluginName());
            }
            enabledNavigatorPlugins.add(plugin);
        }
    }

    /**
     * Scan for bundled plugins at application start time.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        Map<String, AcmPlugin> plugins = applicationContext.getBeansOfType(AcmPlugin.class);

        if ( log.isInfoEnabled() )
        {
            log.info(plugins.size() + " plugin(s) found.");
        }

        for ( Map.Entry<String, AcmPlugin> plugin : plugins.entrySet() )
        {
            if ( log.isDebugEnabled() )
            {
                log.debug("Registering plugin '" + plugin.getKey() + "' of type '" +
                        plugin.getValue().getClass().getName() + "'.");
            }
            registerPlugin(plugin.getValue());
        }
    }


    public synchronized Collection<AcmPlugin> getEnabledNavigatorPlugins()
    {
        return Collections.unmodifiableCollection(enabledNavigatorPlugins);
    }


    public List<String> getPrivilegesForRole(String role)
    {
        if ( privilegesByRole.containsKey(role) )
        {
            return Collections.unmodifiableList(privilegesByRole.get(role));
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public List<AcmPlugin> findAccessiblePlugins(Map<String, Boolean> userPrivileges)
    {
        List<AcmPlugin> retval = new ArrayList<>();

        for ( AcmPlugin plugin : getEnabledNavigatorPlugins() )
        {
            boolean hasPrivilege = checkUserPrivilege(userPrivileges, plugin);
            if ( hasPrivilege )
            {
                retval.add(plugin);
            }
        }

        return retval;
    }

    protected boolean checkUserPrivilege(Map<String, Boolean> userPrivileges, AcmPlugin plugin)
    {
        boolean hasPrivilege = false;
        AcmPluginPrivilege requiredPrivilege = plugin.getNavigatorTabPrivilegeRequired();
        if ( requiredPrivilege != null )
        {
            String privilegeName = requiredPrivilege.getPrivilegeName();
            hasPrivilege = userPrivileges.containsKey(privilegeName) ? userPrivileges.get(privilegeName) : false;

            if ( log.isDebugEnabled() )
            {
                log.debug("Checking access to navigator tab '" + plugin.getNavigatorTabName() + ". " +
                        "Required privilege: '" + privilegeName + "'. User has access: " + hasPrivilege + ".");
            }
        }
        return hasPrivilege;
    }

    public List<AcmPluginUrlPrivilege> getUrlPrivileges()
    {
        return Collections.unmodifiableList(urlPrivileges);
    }
}
