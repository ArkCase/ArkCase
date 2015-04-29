package com.armedia.acm.pluginmanager.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivileges;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.spring.events.ContextAddedEvent;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by dmiller on 3/18/14.
 */
public class AcmPluginManagerTest extends EasyMockSupport
{


    private AcmPluginManager unit;

    private AcmPlugin pluginOne;
    private AcmPlugin pluginTwo;
    private AcmPlugin pluginThree;
    private AcmPluginPrivileges pluginPrivilegesOne;
    private AcmPluginPrivileges pluginPrivilegesTwo;
    private AcmPluginPrivileges pluginPrivilegesThree;

    private ApplicationContext mockContext;
    private SpringContextHolder mockSpringContextHolder;
    private final String roleAdd = "role_add";
    private final String roleAdmin = "role_admin";
    private final String privilegeAdd = "add";

    @Before
    public void setUp() throws Exception
    {
        mockContext = createMock(ApplicationContext.class);
        mockSpringContextHolder = createMock(SpringContextHolder.class);
        unit = new AcmPluginManager();
        unit.setSpringContextHolder(mockSpringContextHolder);
        pluginOne = createPlugin("pluginOne");
        pluginTwo = createPlugin("pluginTwo");
        pluginThree = createPlugin("pluginThree");

        pluginPrivilegesOne = createPluginPrivileges("pluginOne");
        pluginPrivilegesTwo = createPluginPrivileges("pluginTwo");
        pluginPrivilegesThree = createPluginPrivileges("pluginThree");
    }

    @Test
    public void setApplicationContext_registerAllPlugins()
    {
        List<AcmPlugin> plugins = Arrays.asList(pluginOne, pluginTwo, pluginThree);
        List<AcmPluginPrivileges> pluginsPrivileges = Arrays.asList(pluginPrivilegesOne,pluginPrivilegesTwo,pluginPrivilegesThree);
        Map<String, AcmPlugin> beans = asMap(plugins);
        Map<String, AcmPluginPrivileges> acmPluginsPrivileges = asMapPrivileges(pluginsPrivileges);
        expect(mockSpringContextHolder.getAllBeansOfType(AcmPluginPrivileges.class)).andReturn(acmPluginsPrivileges);
        expect(mockContext.getBeansOfType(AcmPlugin.class)).andReturn(beans);

        replayAll();

        unit.onApplicationEvent(new ContextAddedEvent(new Object(), AcmPluginManager.PLUGINS_PRIVILEGES_FOLDER_NAME));
        unit.setApplicationContext(mockContext);

        verifyAll();

        assertEquals(plugins.size(), unit.getAcmPlugins().size());

    }

    @Test
    public void getPrivilegesForRole() throws Exception
    {
        AcmPlugin plugin = createPlugin("test plugin");
        unit.registerPlugin(plugin);

        List<String> privileges = unit.getPrivilegesForRole(roleAdd);

        assertEquals(1, privileges.size());

        assertEquals(privilegeAdd, privileges.get(0));


    }

    @Test
    public void getRolesForPrivilege() throws Exception {
        AcmPlugin plugin = createPlugin("test plugin");
        unit.registerPlugin(plugin);

        List<String> roles = unit.getRolesForPrivilege(privilegeAdd);

        assertEquals(2, roles.size());

    }

    @Test
    public void getPrivilegesForRole_noPrivileges() throws Exception
    {
        AcmPlugin plugin = createPlugin("test plugin");
        plugin.setPrivileges(null);
        unit.registerPlugin(plugin);

        List<String> privileges = unit.getPrivilegesForRole(roleAdd);

        assertEquals(0, privileges.size());


    }


    private Map<String, AcmPlugin> asMap(List<AcmPlugin> pluginList)
    {
        Map<String, AcmPlugin> beans = new HashMap<>();
        for ( AcmPlugin plugin : pluginList )
        {
            beans.put(plugin.getPluginName(), plugin);
        }

        return beans;
    }

    private Map<String, AcmPluginPrivileges> asMapPrivileges(List<AcmPluginPrivileges> pluginList)
    {
        Map<String, AcmPluginPrivileges> beans = new HashMap<>();
        for ( AcmPluginPrivileges plugin : pluginList )
        {
            beans.put(plugin.getPluginName(), plugin);
        }

        return beans;
    }

    private AcmPlugin createPlugin(String pluginName)
    {
        AcmPlugin retval = new AcmPlugin();
        retval.setPluginName(pluginName);

        AcmPluginPrivilege add = new AcmPluginPrivilege();
        add.setPrivilegeName(privilegeAdd);

        add.setApplicationRolesWithPrivilege(Arrays.asList(roleAdd, roleAdmin));

        retval.setPrivileges(Arrays.asList(add));

        return retval;
    }

    private AcmPluginPrivileges createPluginPrivileges(String pluginName)
    {
        AcmPluginPrivileges retval = new AcmPluginPrivileges();
        retval.setPluginName(pluginName);

        AcmPluginPrivilege add = new AcmPluginPrivilege();
        add.setPrivilegeName(privilegeAdd);

        add.setApplicationRolesWithPrivilege(Arrays.asList(roleAdd, roleAdmin));

        retval.setPrivileges(Arrays.asList(add));

        return retval;
    }
}
