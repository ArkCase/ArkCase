package com.armedia.acm.files.capture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import com.armedia.acm.files.capture.CaptureFileEventListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-folder-watcher.xml",
        "/spring-test-config-file-watcher.xml"
        // this is needed because all beans are loaded in spring-library-folder-watcher.xml and it depends on this, also need access to
        // capture.properties properties
        ,"/spring/spring-library-property-file-manager.xml"       
        })
public class CaptureFileWatcherIT
{
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier(value = "captureFolder")
    private FileObject captureFolder;

    @Autowired
    @Qualifier(value = "captureFileEventListener")
    private CaptureFileEventListener listener;


    @Test
    public void captureFolderMonitor_allowed() throws Exception
    {
        if ( !captureFolder.exists() )
        {
            captureFolder.createFolder();
        }

        assertNotNull(captureFolder);

        assertTrue(captureFolder.exists());

        int originalAdded = listener.getAddedCount();

        Resource testFile = new ClassPathResource("/spring-test-config-file-watcher.xml");
        assertTrue(testFile.exists());

        File captureFolderFile = new File(captureFolder.getURL().toURI());
        File target = new File(captureFolderFile, testFile.getFilename());

        FileCopyUtils.copy(testFile.getFile(), target);

        Thread.sleep(5000);

        target.delete();

        Thread.sleep(5000);

        // expect listener to get an event here
        assertEquals(originalAdded + 1, listener.getAddedCount());
    }
    
    @Test
    public void captureFolderMonitor_notallowed() throws Exception
    {
        if ( !captureFolder.exists() )
        {
            captureFolder.createFolder();
        }

        assertNotNull(captureFolder);

        assertTrue(captureFolder.exists());

        int originalAdded = listener.getAddedCount();

        Resource testFile = new ClassPathResource("/test.pdf");
        assertTrue(testFile.exists());

        File captureFolderFile = new File(captureFolder.getURL().toURI());
        File target = new File(captureFolderFile, testFile.getFilename());

        FileCopyUtils.copy(testFile.getFile(), target);

        Thread.sleep(5000);

        target.delete();

        Thread.sleep(5000);

        // do not expect listener to get an event here
        assertEquals(originalAdded, listener.getAddedCount());
    }
}
