package com.armedia.acm.pluginmanager;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * Created by dmiller on 3/18/14.
 */
public class AcmPluginManagerTest extends EasyMockSupport
{
    private AcmPluginManager unit;

    private AcmPlugin enabledNavigatorPlugin;
    private AcmPlugin disabledNavigatorPlugin;
    private AcmPlugin nonNavigatorPlugin;

    private ApplicationContext mockContext;

    @Before
    public void setUp() throws Exception
    {
        mockContext = createMock(ApplicationContext.class);

        unit = new AcmPluginManager();

        enabledNavigatorPlugin = createPlugin(true, true, "enabledNavigatorPlugin");
        disabledNavigatorPlugin = createPlugin(false, true, "disabledNavigatorPlugin");
        nonNavigatorPlugin = createPlugin(true, false, "nonNavigatorPlugin");
    }

    @Test
    public void getNavigatorPlugins_returnOnlyEnabledNavigatorPlugins()
    {
        List<AcmPlugin> plugins = Arrays.asList(enabledNavigatorPlugin, disabledNavigatorPlugin, nonNavigatorPlugin);
        Map<String, AcmPlugin> beans = asMap(plugins);

        expect(mockContext.getBeansOfType(AcmPlugin.class)).andReturn(beans);

        replayAll();

        unit.setApplicationContext(mockContext);

        verifyAll();

        assertEquals(1, unit.getEnabledNavigatorPlugins().size());
    }

    @Test
    public void setApplicationContext_registerAllPlugins()
    {
        List<AcmPlugin> plugins = Arrays.asList(enabledNavigatorPlugin, disabledNavigatorPlugin, nonNavigatorPlugin);
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
        for ( AcmPlugin plugin : pluginList )
        {
            beans.put(plugin.getPluginName(), plugin);
        }

        return beans;
    }

    private AcmPlugin createPlugin(boolean enabled, boolean navigatorTab, String tabName)
    {
        AcmPlugin retval = new AcmPlugin();
        retval.setEnabled(enabled);
        retval.setNavigatorTab(navigatorTab);
        retval.setNavigatorTabName(tabName);
        retval.setHomeUrl(tabName);
        retval.setPluginName(tabName);

        return retval;
    }
}
