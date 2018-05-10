package com.armedia.acm.pluginmanager.service;

/*-
 * #%L
 * ACM Service: Plugin Manager
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivilege;
import com.armedia.acm.pluginmanager.model.AcmPluginPrivileges;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.spring.events.ContextAddedEvent;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmiller on 3/18/14.
 */
public class AcmPluginManagerTest extends EasyMockSupport
{

    private final String roleAdd = "role_add";
    private final String roleAdmin = "role_admin";
    private final String wildCardRole = "ADMIN@*";
    private final String privilegeAdd = "add";
    private AcmPluginManager unit;
    private AcmPlugin pluginOne;
    private AcmPlugin pluginTwo;
    private AcmPlugin pluginThree;
    private AcmPluginPrivileges pluginPrivilegesOne;
    private AcmPluginPrivileges pluginPrivilegesTwo;
    private AcmPluginPrivileges pluginPrivilegesThree;
    private ApplicationContext mockContext;
    private SpringContextHolder mockSpringContextHolder;

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
        List<AcmPluginPrivileges> pluginsPrivileges = Arrays.asList(pluginPrivilegesOne, pluginPrivilegesTwo, pluginPrivilegesThree);
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
    public void getPrivilegesForRole()
    {
        AcmPlugin plugin = createPlugin("test plugin");
        unit.registerPlugin(plugin);

        List<String> privileges = unit.getPrivilegesForRole(roleAdd);

        assertEquals(1, privileges.size());

        assertEquals(privilegeAdd, privileges.get(0));

    }

    @Test
    public void getPrivilegesForWildCardRole()
    {
        AcmPlugin plugin = createPlugin("test plugin");
        unit.registerPlugin(plugin);

        List<String> privileges = unit.getPrivilegesForRole("ADMIN@ARMEDIA.COM");

        assertEquals(1, privileges.size());

        assertEquals(privilegeAdd, privileges.get(0));

    }

    @Test
    public void getRolesForPrivilege()
    {
        AcmPlugin plugin = createPlugin("test plugin");
        unit.registerPlugin(plugin);

        List<String> roles = unit.getRolesForPrivilege(privilegeAdd);

        assertEquals(3, roles.size());

    }

    @Test
    public void getPrivilegesForRole_noPrivileges()
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
        for (AcmPlugin plugin : pluginList)
        {
            beans.put(plugin.getPluginName(), plugin);
        }

        return beans;
    }

    private Map<String, AcmPluginPrivileges> asMapPrivileges(List<AcmPluginPrivileges> pluginList)
    {
        Map<String, AcmPluginPrivileges> beans = new HashMap<>();
        for (AcmPluginPrivileges plugin : pluginList)
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

        add.setApplicationRolesWithPrivilege(Arrays.asList(roleAdd, roleAdmin, wildCardRole));

        retval.setPrivileges(Arrays.asList(add));

        return retval;
    }

    private AcmPluginPrivileges createPluginPrivileges(String pluginName)
    {
        AcmPluginPrivileges retval = new AcmPluginPrivileges();
        retval.setPluginName(pluginName);

        AcmPluginPrivilege add = new AcmPluginPrivilege();
        add.setPrivilegeName(privilegeAdd);

        add.setApplicationRolesWithPrivilege(Arrays.asList(roleAdd, roleAdmin, wildCardRole));

        retval.setPrivileges(Arrays.asList(add));

        return retval;
    }
}
