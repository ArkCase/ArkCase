package com.armedia.acm.pluginmanager.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivileges;
import com.armedia.acm.pluginmanager.model.AcmPluginUrlPrivilege;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.spring.events.AbstractContextHolderEvent;
import com.armedia.acm.spring.events.ContextAddedEvent;
import com.armedia.acm.spring.events.ContextRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AcmPluginManager implements ApplicationContextAware, ApplicationListener<AbstractContextHolderEvent>
{

    public static final String PLUGINS_PRIVILEGES_FOLDER_NAME = "spring-config-acm-plugins";
    private Logger log = LoggerFactory.getLogger(getClass());
    private SpringContextHolder springContextHolder;
    private Collection<AcmPlugin> acmPlugins = new ArrayList<>();
    private Map<String, List<String>> privilegesByRole = new HashMap<>();
    private List<AcmPluginUrlPrivilege> urlPrivileges = new ArrayList<>();
    private Map<String, List<String>> configurablePrivilegesByRole = new HashMap<>();
    private List<AcmPluginUrlPrivilege> configurableUrlPrivileges = new ArrayList<>();

    public synchronized Collection<AcmPlugin> getAcmPlugins()
    {
        return Collections.unmodifiableCollection(acmPlugins);
    }

    public synchronized void registerPlugin(AcmPlugin plugin)
    {
        acmPlugins.add(plugin);

        addPluginPrivileges(plugin);

        addPluginUrlPrivileges(plugin);
    }


    private void addPluginUrlPrivileges(AcmPlugin plugin)
    {
        if ( plugin.getUrlPrivileges() != null )
        {
            urlPrivileges.addAll(plugin.getUrlPrivileges());
            configurableUrlPrivileges.addAll(plugin.getUrlPrivileges());
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
                    mapRolesToConfigurablePrivileges(privilege);
                }
            }
        }
    }
    private void mapRolesToConfigurablePrivileges(AcmPluginPrivilege privilege)
    {
        for ( String role : privilege.getApplicationRolesWithPrivilege() )
        {
            List<String> rolePrivileges = configurablePrivilegesByRole.get(role);
            rolePrivileges = rolePrivileges == null ? new ArrayList<String>() : rolePrivileges;
            String privilegeName = privilege.getPrivilegeName();
            addConfigurablePrivilegeIfNecessary(role, rolePrivileges, privilegeName);
        }
    }

    private void addConfigurablePrivilegeIfNecessary(String role, List<String> rolePrivileges, String privilegeName)
    {
        if ( ! rolePrivileges.contains(privilegeName) )
        {
            rolePrivileges.add(privilegeName);
            configurablePrivilegesByRole.put(role, rolePrivileges);
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

    public List<String> getPrivilegesForRole(String role)
    {
        if ( configurablePrivilegesByRole.containsKey(role) )
        {
            return Collections.unmodifiableList(configurablePrivilegesByRole.get(role));
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public List<AcmPluginUrlPrivilege> getUrlPrivileges()
    {
        return Collections.unmodifiableList(configurableUrlPrivileges);
    }

    public List<String> getRolesForPrivilege(String privilege)
    {
        // iterate over the role-to-privileges map; if the value list includes this privilege, the role (i.e. the
        // map key) is included in the return list.
        List<String> retval = new LinkedList<>();

        for ( Map.Entry<String, List<String>> roleToPrivileges : configurablePrivilegesByRole.entrySet() )
        {
            if ( roleToPrivileges.getValue() != null
                    && roleToPrivileges.getValue().contains(privilege)
                    && !retval.contains(roleToPrivileges.getKey()))
            {
                retval.add(roleToPrivileges.getKey());
            }
        }

        return retval;
    }

    public synchronized void registerPluginPrivileges(AcmPluginPrivileges pluginPrivileges)
    {

        addPluginConfigurablePrivileges(pluginPrivileges);

        addPluginUrlConfigurablePrivileges(pluginPrivileges);
    }

    private void addPluginUrlConfigurablePrivileges(AcmPluginPrivileges pluginPrivileges)
    {
        if ( pluginPrivileges.getUrlPrivileges() != null )
        {
            configurableUrlPrivileges.addAll(pluginPrivileges.getUrlPrivileges());
        }
    }
    private void addPluginConfigurablePrivileges(AcmPluginPrivileges pluginPrivileges)
    {
        if ( pluginPrivileges.getPrivileges() != null )
        {
            for ( AcmPluginPrivilege privilege : pluginPrivileges.getPrivileges() )
            {
                if ( privilege.getApplicationRolesWithPrivilege() != null )
                {
                    mapRolesToConfigurablePrivileges(privilege);
                }
            }
        }
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder) {
        this.springContextHolder = springContextHolder;
    }

    @Override
    public void onApplicationEvent(AbstractContextHolderEvent event) {

        String eventFile = event.getContextName();
        if ((event instanceof ContextRemovedEvent) && eventFile.equals(PLUGINS_PRIVILEGES_FOLDER_NAME)) {
            //clear all privileges
            configurablePrivilegesByRole.clear();
            configurableUrlPrivileges.clear();
            //add non configurable privileges
            configurablePrivilegesByRole.putAll(privilegesByRole);
            configurableUrlPrivileges.addAll(urlPrivileges);
        } else if ((event instanceof ContextAddedEvent) && eventFile.equals(PLUGINS_PRIVILEGES_FOLDER_NAME)) {
            //read all privileges
            Map<String, AcmPluginPrivileges> pluginsPrivileges = springContextHolder.getAllBeansOfType(AcmPluginPrivileges.class);

            if (log.isInfoEnabled()) {
                log.info(pluginsPrivileges.size() + " plugins privileges(s) found.");
            }

            //get privileges from configuration files located in ${user.home}/.acm
            for (Map.Entry<String, AcmPluginPrivileges> privilegesForPlugin : pluginsPrivileges.entrySet()) {
                if (log.isDebugEnabled()) {
                    log.debug("Registering plugins privileges '" + privilegesForPlugin.getKey() + "'.");
                }
                registerPluginPrivileges(privilegesForPlugin.getValue());
            }
        }

    }
}
