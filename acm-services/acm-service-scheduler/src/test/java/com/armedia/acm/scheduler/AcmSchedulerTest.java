package com.armedia.acm.scheduler;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.vfs2.FileChangeEvent;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 29, 2016
 *
 */
@RunWith(EasyMockRunner.class)
public class AcmSchedulerTest extends EasyMockSupport
{

    private File noSchedulerConfiguration;

    private File schedulerConfiguration;

    // @TestSubject
    private AcmScheduler acmScheduler;

    @Mock
    private TaskScheduler mockedTaskScheduler;

    @Mock
    private TaskExecutor mockedTaskExecutor;

    @Mock
    private SpringContextHolder mockedSpringContextHolder;

    @Mock
    private FileChangeEvent mockedSource;

    @Mock
    private AcmSchedulableBean mockedSchedulableBean;

    @Before
    public void setUp() throws IOException
    {
        acmScheduler = new AcmScheduler(mockedTaskScheduler, mockedTaskExecutor, mockedSpringContextHolder);
        schedulerConfiguration = new ClassPathResource("scheduledTasks.json").getFile();
        noSchedulerConfiguration = new ClassPathResource("someConfiguration.json").getFile();
        assertNotNull(schedulerConfiguration);
        assertNotNull(noSchedulerConfiguration);
    }

    @Test
    public void testOnApplicationEventNotMonitoredFile()
    {
        AbstractConfigurationFileEvent event = new ConfigurationFileChangedEvent(mockedSource);
        event.setConfigFile(noSchedulerConfiguration);
        acmScheduler.onApplicationEvent(event);

        long lastModifiedTime = Whitebox.getInternalState(acmScheduler, "lastModifiedTime");

        assertEquals(0, lastModifiedTime);
    }

    @Test
    public void testOnApplicationEventMonitoredFileNotModified() throws IOException
    {
        AbstractConfigurationFileEvent event = new ConfigurationFileChangedEvent(mockedSource);
        event.setConfigFile(schedulerConfiguration);

        FileTime schedulerModifiedTime = getConfigLastModifiedTime(schedulerConfiguration);
        Whitebox.setInternalState(acmScheduler, "lastModifiedTime", schedulerModifiedTime.toMillis());

        acmScheduler.onApplicationEvent(event);

        String configurationPath = Whitebox.getInternalState(acmScheduler, "configurationPath");

        assertNull(configurationPath);

    }

    @Test
    public void testOnApplicationEventMonitoredFileModified() throws IOException
    {
        expect(mockedSpringContextHolder.getBeanByName("scheduledBillingQueuePurger", AcmSchedulableBean.class))
                .andReturn(mockedSchedulableBean);

        replay(mockedSpringContextHolder);

        AbstractConfigurationFileEvent event = new ConfigurationFileChangedEvent(mockedSource);
        event.setConfigFile(schedulerConfiguration);

        acmScheduler.onApplicationEvent(event);

        String configurationPath = Whitebox.getInternalState(acmScheduler, "configurationPath");
        Map<String, AcmSchedulerTask> tasks = Whitebox.getInternalState(acmScheduler, "tasks");

        assertTrue(configurationPath.endsWith("scheduledTasks.json"));
        assertTrue(tasks.containsKey("test"));

        AcmSchedulerTask task = tasks.get("test");
        AcmSchedulableBean scheduledBean = Whitebox.getInternalState(task, "schedulableBean");

        assertEquals(mockedSchedulableBean, scheduledBean);

    }

    private FileTime getConfigLastModifiedTime(File configFile) throws IOException
    {
        Path configFilePath = configFile.toPath();
        BasicFileAttributes attributes = Files.readAttributes(configFilePath, BasicFileAttributes.class);
        FileTime lastModified = attributes.lastModifiedTime();
        return lastModified;
    }

}
