package com.armedia.acm.pluginmanager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/spring/spring-library-plugin-manager.xml",
        "classpath:/spring/spring-library-folder-watcher.xml"})
public class PluginDynamicLoaderIT
{
    @Autowired
    private PluginDynamicLoader pluginDynamicLoader;
    private static final String pluginName = "/armedia-blog-plugin-1.2-SNAPSHOT.jar";


    @BeforeClass
    public static void beforeClass()
    {
        String testUserHomePath = "/testUserHome";
        File testUserHome = new File(testUserHomePath);
        if ( !testUserHome.exists() )
        {
            testUserHome.mkdir();
        }
        File acm = new File(testUserHomePath + "/.acm");
        if ( !acm.exists() )
        {
            acm.mkdir();
        }

        System.setProperty("user.home", testUserHomePath);

        File targetJar = new File(System.getProperty("user.home") + "/.acm" + pluginName);
        if ( targetJar.exists() )
        {
            boolean deleted = targetJar.delete();
            assertTrue(deleted);
        }
    }

    @Test
    public void loadJar() throws Exception
    {
        Resource testPlugin = new ClassPathResource(pluginName);
        assertTrue(testPlugin.exists());

        File targetJar = null;

        targetJar = new File(System.getProperty("user.home") + "/.acm" + pluginName);
        if ( targetJar.exists() )
        {
            boolean deleted = targetJar.delete();
            assertTrue(deleted);
        }


        FileCopyUtils.copy(testPlugin.getFile(), targetJar);

        Thread.sleep(15000);




    }
}
