package com.armedia.acm.files.capture;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.files.FileAddedEvent;
import com.armedia.acm.files.FileEvent;
import com.armedia.acm.files.FileWatcher;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileTypeSelector;
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
import java.util.ArrayList;
import java.util.List;

@RunWith(EasyMockRunner.class)
public class CaptureFileWatcherTest extends EasyMockSupport
{
    // do not put a period before the extension
    private final String allowedFileExtensions = "pdf,xml";
    @Mock
    private FileObject mockFileObject;
    @Mock
    private ApplicationEventPublisher mockPublisher;
    @Mock
    private FileChangeEvent mockFileChangeEvent;
    @Mock
    private FileName mockFileName;
    private FileWatcher unit;
    private String fileSeparator = File.separator;
    private final String baseFolderPath = fileUrlPrefix + fileSeparator + "temp";
    private Logger log = LoggerFactory.getLogger(getClass());
    private boolean runningOnWindows = System.getProperty("os.name").startsWith("Windows");
    // for this test to pass, Windows and Linux require different file URL prefixes
    private final String fileUrlPrefix = runningOnWindows ? "file:///C:" : "file:///";

    @Before
    public void setUp() throws Exception
    {
        unit = new FileWatcher();
        unit.setWatchFolderPath(baseFolderPath);
        unit.setAllowedFileExtensions(allowedFileExtensions);
    }

    @Test
    public void setApplicationContext_noFilesOrFoldersFound() throws Exception
    {
        expect(mockFileObject.getName()).andReturn(mockFileName);
        expect(mockFileObject.getURL()).andReturn(new URL(baseFolderPath));
        expect(mockFileObject.findFiles(anyObject(FileTypeSelector.class))).andReturn(null).times(2);

        replayAll();

        unit.setWatchFolder(mockFileObject);
        unit.setApplicationContext(null);

        verifyAll();
    }

    @Test
    public void baseFolderPath_shouldBeSet_afterSourceFolderIsSet() throws Exception
    {
        expect(mockFileObject.getURL()).andReturn(new URL(baseFolderPath));

        replayAll();

        unit.setWatchFolder(mockFileObject);

        verifyAll();

        String expected = (runningOnWindows ? "C:" : "") + fileSeparator + "temp";

        assertEquals(expected, unit.getWatchFolderPath());
    }

    @Test
    public void fileExtensionsList_shouldBeSet_afterFileExtensionsIsSet() throws Exception
    {
        String fileExtensions = "pdf,txt,html";

        replayAll();

        unit.setAllowedFileExtensions(fileExtensions);

        verifyAll();

        List<String> expected = new ArrayList<>();
        expected.add("pdf");
        expected.add("txt");
        expected.add("html");

        assertEquals(expected, unit.getAllowedFileExtensionsList());
    }

    @Test
    public void raiseEvent_whenFileIsAdded_allowed() throws Exception
    {

        Capture<FileEvent> capturedEvent = setupEventTest(unit.getWatchFolderPath() + fileSeparator + "file.xml", "xml");

        unit.fileCreated(mockFileChangeEvent);

        verifyEventTestResults(capturedEvent);
        assertEquals(FileAddedEvent.class, capturedEvent.getValue().getClass());
    }

    @Test
    public void raiseEvent_whenFileIsAdded_notallowed() throws Exception
    {
        // we don't watch for png files so we shouldn't get an event

        String extension = "png";
        String fileUrl = unit.getWatchFolderPath() + fileSeparator + "file.png";

        unit.setApplicationEventPublisher(mockPublisher);

        expect(mockFileObject.getName()).andReturn(mockFileName);
        expect(mockFileChangeEvent.getFile()).andReturn(mockFileObject).atLeastOnce();
        expect(mockFileObject.getName()).andReturn(mockFileName).anyTimes();
        expect(mockFileName.getExtension()).andReturn(extension);

        log.debug("File URL: " + fileUrl);

        replayAll();

        unit.fileCreated(mockFileChangeEvent);

        // no event should be captured
        verifyAll();
    }

    private void verifyEventTestResults(Capture<FileEvent> capturedEvent)
    {
        verifyAll();

        assertEquals("file.xml", capturedEvent.getValue().getFile().getName());
        assertNotNull(capturedEvent.getValue().getFile());
    }

    private Capture<FileEvent> setupEventTest(String fileUrl, String extension) throws FileSystemException, MalformedURLException
    {
        unit.setApplicationEventPublisher(mockPublisher);

        Capture<FileEvent> capturedEvent = new Capture<>();

        expect(mockFileChangeEvent.getFile()).andReturn(mockFileObject).atLeastOnce();
        expect(mockFileObject.getName()).andReturn(mockFileName).anyTimes();
        expect(mockFileName.getExtension()).andReturn(extension);

        URL fileUrlObj = new URL(fileUrl);
        expect(mockFileObject.getURL()).andReturn(fileUrlObj).times(1, 2);

        log.debug("File URL: " + fileUrl);

        mockPublisher.publishEvent(capture(capturedEvent));

        replayAll();
        return capturedEvent;
    }
}
