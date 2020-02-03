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
    private ApplicationContext mockContext;

    @Before
    public void setUp() throws Exception
    {
        mockContext = createMock(ApplicationContext.class);
        unit = new AcmPluginManager();
        pluginOne = createPlugin("pluginOne");
        pluginTwo = createPlugin("pluginTwo");
        pluginThree = createPlugin("pluginThree");

    }

    @Test
    public void registerAllPlugins()
    {
        List<AcmPlugin> plugins = Arrays.asList(pluginOne, pluginTwo, pluginThree);
        Map<String, AcmPlugin> beans = asMap(plugins);
        expect(mockContext.getBeansOfType(AcmPlugin.class)).andReturn(beans);

        replayAll();

        unit.setApplicationContext(mockContext);

        verifyAll();

        assertEquals(plugins.size(), unit.getAcmPlugins().size());
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

}
