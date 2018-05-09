package com.armedia.acm.files;

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


import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by dmiller on 2/20/14.
 */
@RunWith(EasyMockRunner.class)
public class ConfigFileWatcherTest extends EasyMockSupport
{
    // for this test to pass, Windows and Linux require different file URL prefixes
    private final String fileUrlPrefix = "file:" + (File.separator.equals("/") ? "" : "/");
    @Mock
    private FileObject mockFileObject;
    @Mock
    private ApplicationEventPublisher mockPublisher;
    @Mock
    private FileChangeEvent mockFileChangeEvent;
    @Mock
    private FileName mockFileName;
    private ConfigFileWatcher unit;
    private String fileSeparator = File.separator;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        unit = new ConfigFileWatcher();
        unit.setIgnoreFolders(Arrays.asList("/ignoreFolder"));
        unit.setBaseFolderPath(System.getProperty("user.home") + fileSeparator + "acm");
    }

    @Test
    public void baseFolderPath_shouldBeSet_afterSourceFolderIsSet() throws Exception
    {
        expect(mockFileObject.getURL()).andReturn(new URL("file:///C:" + fileSeparator + "home" + fileSeparator + "acm"));

        replayAll();

        unit.setBaseFolder(mockFileObject);

        verifyAll();

        String expected = "C:" + fileSeparator + "home" + fileSeparator + "acm";
        // cross platform canonical path names...
        if ("/".equals(fileSeparator))
        {
            expected = "/" + expected;
        }

        assertEquals(expected, unit.getBaseFolderPath());
    }

    @Test
    public void raiseEvent_ignoreFilesFromIgnoreFolders() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest(fileUrlPrefix + unit.getBaseFolderPath() + fileSeparator +
                "ignoreFolder" + fileSeparator + "file.txt");

        unit.fileCreated(mockFileChangeEvent);

        // since no event should have been raised, nothing should have been captured.
        assertFalse(capturedEvent.hasCaptured());
    }

    @Test
    public void raiseEvent_whenFileIsAdded() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest(
                fileUrlPrefix + unit.getBaseFolderPath() + fileSeparator + "file.txt");

        unit.fileCreated(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(ConfigurationFileAddedEvent.class, capturedEvent.getValue().getClass());
    }

    @Test
    public void raiseEvent_whenFileIsRemoved() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest(
                fileUrlPrefix + unit.getBaseFolderPath() + fileSeparator + "file.txt");

        unit.fileDeleted(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(ConfigurationFileDeletedEvent.class, capturedEvent.getValue().getClass());
    }

    @Test
    public void raiseEvent_whenFileIsChanged() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest(
                fileUrlPrefix + unit.getBaseFolderPath() + fileSeparator + "file.txt");

        unit.fileChanged(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(ConfigurationFileChangedEvent.class, capturedEvent.getValue().getClass());
    }

    private void verifyEventTestResults(Capture<AbstractConfigurationFileEvent> capturedEvent)
    {
        verifyAll();

        assertEquals("file.txt", capturedEvent.getValue().getConfigFile().getName());
        assertNotNull(capturedEvent.getValue().getConfigFile());
    }

    private Capture<AbstractConfigurationFileEvent> setupEventTest(String fileUrl) throws FileSystemException, MalformedURLException
    {
        unit.setApplicationEventPublisher(mockPublisher);

        Capture<AbstractConfigurationFileEvent> capturedEvent = new Capture<>();

        expect(mockFileChangeEvent.getFile()).andReturn(mockFileObject).atLeastOnce();
        expect(mockFileObject.getName()).andReturn(mockFileName).anyTimes();

        URL fileUrlObj = new URL(fileUrl);
        expect(mockFileObject.getURL()).andReturn(fileUrlObj).times(1, 2);

        log.debug("File URL: " + fileUrl);

        // if file is in one of the folders to be ignored we shouldn't raise an event
        boolean ignored = unit.ignoreThisFile(fileUrlObj);

        if (!ignored)
        {
            mockPublisher.publishEvent(capture(capturedEvent));
        }

        replayAll();
        return capturedEvent;
    }
}
