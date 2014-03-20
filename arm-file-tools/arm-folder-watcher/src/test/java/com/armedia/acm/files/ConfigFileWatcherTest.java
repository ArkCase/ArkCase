package com.armedia.acm.files;

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
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by dmiller on 2/20/14.
 */
@RunWith(EasyMockRunner.class)
public class ConfigFileWatcherTest extends EasyMockSupport
{
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

    @Before
    public void setUp() throws Exception
    {
        unit = new ConfigFileWatcher();
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
        if ( "/".equals(fileSeparator) )
        {
            expected = "/" + expected;
        }

        assertEquals(expected, unit.getBaseFolderPath());
    }

    @Test
    public void raiseEvent_whenFileIsAdded() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest();

        unit.fileCreated(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(ConfigurationFileAddedEvent.class, capturedEvent.getValue().getClass());
    }

    @Test
    public void raiseEvent_whenFileIsRemoved() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest();

        unit.fileDeleted(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(ConfigurationFileDeletedEvent.class, capturedEvent.getValue().getClass());
    }

    @Test
    public void raiseEvent_whenFileIsChanged() throws Exception
    {
        Capture<AbstractConfigurationFileEvent> capturedEvent = setupEventTest();

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

    private Capture<AbstractConfigurationFileEvent> setupEventTest() throws FileSystemException, MalformedURLException
    {
        unit.setApplicationEventPublisher(mockPublisher);
        unit.setBaseFolderPath("home" + fileSeparator + "acm");

        Capture<AbstractConfigurationFileEvent> capturedEvent = new Capture<>();

        expect(mockFileChangeEvent.getFile()).andReturn(mockFileObject).atLeastOnce();
        expect(mockFileObject.getName()).andReturn(mockFileName).anyTimes();
        expect(mockFileObject.getURL()).andReturn(new URL("file:/" + unit.getBaseFolderPath() + fileSeparator + "file.txt"));

        mockPublisher.publishEvent(capture(capturedEvent));

        replayAll();
        return capturedEvent;
    }
}
