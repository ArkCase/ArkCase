package com.armedia.acm.files.capture;

/*-
 * #%L
 * Tool Integrations: Folder Watcher
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.vfs2.FileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring-test-config-file-watcher.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class CaptureFileWatcherIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    private Logger log = LogManager.getLogger(getClass());

    @Autowired
    @Qualifier(value = "captureFolder")
    private FileObject captureFolder;

    @Autowired
    @Qualifier(value = "captureFileEventListener")
    private CaptureFileEventListener listener;

    @Test
    public void captureFolderMonitor_allowed() throws Exception
    {
        if (!captureFolder.exists())
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
        if (!captureFolder.exists())
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
