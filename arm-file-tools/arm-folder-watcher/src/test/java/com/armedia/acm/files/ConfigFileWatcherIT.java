package com.armedia.acm.files;

import org.apache.commons.vfs2.FileObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by dmiller on 2/20/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-folder-watcher.xml", "/spring-test-config-file-watcher.xml"})
public class ConfigFileWatcherIT
{

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier(value = "configFolder")
    private FileObject configFolder;

    @Autowired
    @Qualifier(value = "userHomeFolder")
    private File homeFolder;

    @Autowired
    @Qualifier(value = "configFileEventListener")
    private ConfigurationFileEventListener listener;


    @Test
    public void folderMonitor() throws Exception
    {
        log.debug("Home folder path: " + homeFolder.getCanonicalPath());
        if ( !homeFolder.exists() )
        {
            homeFolder.mkdirs();
        }

        if ( !configFolder.exists() )
        {
            configFolder.createFolder();
        }

        assertNotNull(configFolder);

        assertTrue(homeFolder.exists());
        assertTrue(configFolder.exists());

        assertEquals(0, listener.getAddedCount());
        assertEquals(0, listener.getRemovedCount());

        Resource testFile = new ClassPathResource("/log4j.properties");
        assertTrue(testFile.exists());


        File configFolderFile = new File(configFolder.getURL().toURI());
        File target = new File(configFolderFile, testFile.getFilename());

        FileCopyUtils.copy(testFile.getFile(), target);

        Thread.sleep(5000);

        target.delete();

        Thread.sleep(5000);

        assertEquals(1, listener.getAddedCount());
        assertEquals(1, listener.getRemovedCount());
    }
}
