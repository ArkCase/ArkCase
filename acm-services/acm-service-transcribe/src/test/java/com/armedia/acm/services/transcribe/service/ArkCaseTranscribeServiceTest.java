package com.armedia.acm.services.transcribe.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.exception.CreateTranscribeException;
import com.armedia.acm.services.transcribe.exception.GetTranscribeConfigurationException;
import com.armedia.acm.services.transcribe.model.*;
import com.armedia.acm.services.transcribe.pipline.TranscribePipelineContext;
import com.armedia.acm.services.transcribe.rules.TranscribeBusinessProcessRulesExecutor;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class ArkCaseTranscribeServiceTest extends EasyMockSupport
{
    private ArkCaseTranscribeService arkCaseTranscribeService;
    private TranscribeConfigurationPropertiesService transcribeConfigurationPropertiesService;

    @Mock
    private PropertyFileManager propertyFileManager;

    @Mock
    private TranscribeDao transcribeDao;

    @Mock
    private EcmFileVersionDao ecmFileVersionDao;

    @Mock
    private PipelineManager<Transcribe, TranscribePipelineContext> pipelineManager;

    @Mock
    private TranscribeBusinessProcessRulesExecutor transcribeBusinessProcessRulesExecutor;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstance processInstance;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Before
    public void setUp()
    {
        transcribeConfigurationPropertiesService = new TranscribeConfigurationPropertiesService();
        transcribeConfigurationPropertiesService.setPropertyFileManager(propertyFileManager);

        arkCaseTranscribeService = new ArkCaseTranscribeService();
        arkCaseTranscribeService.setTranscribeConfigurationService(transcribeConfigurationPropertiesService);
        arkCaseTranscribeService.setTranscribeDao(transcribeDao);
        arkCaseTranscribeService.setPipelineManager(pipelineManager);
        arkCaseTranscribeService.setEcmFileVersionDao(ecmFileVersionDao);
        arkCaseTranscribeService.setTranscribeBusinessProcessRulesExecutor(transcribeBusinessProcessRulesExecutor);
        arkCaseTranscribeService.setActivitiRuntimeService(activitiRuntimeService);
    }

    @Test
    public void create_By_Version_Id() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(ecmFileVersionDao.find(version.getId())).thenReturn(version);
        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenReturn(transcribe);

        Transcribe created = arkCaseTranscribeService.create(version.getId(), TranscribeType.AUTOMATIC);

        verify(ecmFileVersionDao).find(version.getId());
        verify(propertyFileManager, times(3)).loadMultiple(any(), any());
        verify(transcribeDao).findByMediaVersionId(version.getId());
        verify(pipelineManager).executeOperation(any(), any(), any());

        assertNotNull(created);
        assertEquals(transcribe, created);
    }

    @Test
    public void create_By_Version() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenReturn(transcribe);

        Transcribe created = arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);

        verify(propertyFileManager, times(3)).loadMultiple(any(), any());
        verify(transcribeDao).findByMediaVersionId(version.getId());
        verify(pipelineManager).executeOperation(any(), any(), any());

        assertNotNull(created);
        assertEquals(transcribe, created);
    }

    @Test
    public void create_By_Version_Automatic_Not_Allowed() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        try
        {
            arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(propertyFileManager, times(2)).loadMultiple(any(), any());

            assertTrue(e instanceof CreateTranscribeException);
            assertEquals("Transcribe service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Transcribe_Not_Allowed() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        try
        {
            arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(propertyFileManager).loadMultiple(any(), any());

            assertTrue(e instanceof CreateTranscribeException);
            assertEquals("Transcribe service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_File_Not_Media() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setVersionMimeType("text/plain");

        try
        {
            arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CreateTranscribeException);
            assertEquals("Transcribe service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Media_More_Than_2_Hours() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(1000000d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        try
        {
            arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(propertyFileManager, times(2)).loadMultiple(any(), any());

            assertTrue(e instanceof CreateTranscribeException);
            assertEquals("Transcribe service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Already_Exist() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(transcribe);

        try
        {
            arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(propertyFileManager, times(3)).loadMultiple(any(), any());
            verify(transcribeDao).findByMediaVersionId(version.getId());

            assertTrue(e instanceof CreateTranscribeException);
            assertEquals("Creating Transcribe job is aborted. There is already Transcribe object for MEDIA_FILE_VERSION_ID=[101]", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Pipeline_Exception() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10.5");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);


        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenThrow(new PipelineProcessException("Pipeline Exception"));

        try
        {
            arkCaseTranscribeService.create(version, TranscribeType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(propertyFileManager, times(3)).loadMultiple(any(), any());
            verify(transcribeDao).findByMediaVersionId(version.getId());
            verify(pipelineManager).executeOperation(any(), any(), any());

            assertTrue(e instanceof CreateTranscribeException);
            assertEquals("Transcribe for MEDIA_VERSION_ID=[101] was not created successfully. REASON=[Pipeline Exception]", e.getMessage());
        }
    }

    @Test
    public void startBusinessProcess_Start_true() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(103l);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");
        version.setFile(file);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());
        transcribe.setMediaEcmFileVersion(version);

        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(transcribe.getType());
        model.setName("TranscribeWorkflow");
        model.setStart(true);

        when(transcribeBusinessProcessRulesExecutor.applyRules(any())).thenReturn(model);
        when(activitiRuntimeService.startProcessInstanceByKey(eq(model.getName()), (Map<String, Object>) any())).thenReturn(processInstance);
        when(processInstance.getId()).thenReturn("123");

        arkCaseTranscribeService.startBusinessProcess(transcribe);

        verify(transcribeBusinessProcessRulesExecutor).applyRules(any());
        verify(activitiRuntimeService).startProcessInstanceByKey(eq(model.getName()), (Map<String, Object>) any());
        verify(processInstance).getId();

        assertEquals("123", transcribe.getProcessId());
    }

    @Test
    public void startBusinessProcess_Start_true_Existing_Process() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(103l);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");
        version.setFile(file);

        List<Long> ids = new ArrayList<>();
        ids.add(999l);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());
        transcribe.setProcessId("123");
        transcribe.setRemoteId("remoteId");
        transcribe.setMediaEcmFileVersion(version);

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("IDS", ids);
        processVariables.put("REMOTE_ID", transcribe.getRemoteId());
        processVariables.put("STATUS", StatusType.QUEUED);
        processVariables.put("ACTION", ActionType.QUEUED);

        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(transcribe.getType());
        model.setName("TranscribeWorkflow");
        model.setStart(true);

        when(transcribeBusinessProcessRulesExecutor.applyRules(any())).thenReturn(model);
        when(activitiRuntimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceId(transcribe.getProcessId())).thenReturn(processInstanceQuery);
        when(processInstanceQuery.includeProcessVariables()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        when(processInstance.getProcessVariables()).thenReturn(processVariables);
        doNothing().when(activitiRuntimeService).setVariable(transcribe.getProcessId(), "IDS", ids);
        when(processInstance.getId()).thenReturn(transcribe.getProcessId());

        arkCaseTranscribeService.startBusinessProcess(transcribe);

        verify(transcribeBusinessProcessRulesExecutor).applyRules(any());
        verify(activitiRuntimeService).createProcessInstanceQuery();
        verify(processInstanceQuery).processInstanceId(transcribe.getProcessId());
        verify(processInstanceQuery).includeProcessVariables();
        verify(processInstanceQuery).singleResult();
        verify(processInstance).getProcessVariables();
        verify(activitiRuntimeService).setVariable(transcribe.getProcessId(), "IDS", ids);
        verify(processInstance).getId();

        assertEquals(2, ids.size());
        assertTrue(ids.contains(999l));
        assertTrue(ids.contains(transcribe.getId()));
    }

    @Test
    public void startBusinessProcess_Start_false() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(103l);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");
        version.setFile(file);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(102l);
        transcribe.setType(TranscribeType.AUTOMATIC.toString());
        transcribe.setMediaEcmFileVersion(version);

        TranscribeBusinessProcessModel model = new TranscribeBusinessProcessModel();
        model.setType(transcribe.getType());
        model.setName("TranscribeWorkflow");
        model.setStart(false);

        when(transcribeBusinessProcessRulesExecutor.applyRules(any())).thenReturn(model);

        arkCaseTranscribeService.startBusinessProcess(transcribe);

        verify(transcribeBusinessProcessRulesExecutor).applyRules(any());

        assertEquals(null, transcribe.getProcessId());
    }

    @Test
    public void isTranscribeOn_true() throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        boolean allow = arkCaseTranscribeService.isTranscribeOn();

        verify(propertyFileManager).loadMultiple(any(), any());

        assertTrue(allow);
    }

    @Test
    public void isTranscribeOn_false() throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        boolean allow = arkCaseTranscribeService.isTranscribeOn();

        verify(propertyFileManager).loadMultiple(any(), any());

        assertFalse(allow);
    }

    @Test
    public void isAutomaticTranscribeOn_true() throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        boolean allow = arkCaseTranscribeService.isAutomaticTranscribeOn();

        verify(propertyFileManager).loadMultiple(any(), any());

        assertTrue(allow);
    }

    @Test
    public void isAutomaticTranscribeOn_false() throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        boolean allow = arkCaseTranscribeService.isAutomaticTranscribeOn();

        verify(propertyFileManager).loadMultiple(any(), any());

        assertFalse(allow);
    }

    @Test
    public void isMediaDurationAllowed_true() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        boolean result = arkCaseTranscribeService.isMediaDurationAllowed(version);

        verify(propertyFileManager).loadMultiple(any(), any());

        assertTrue(result);
    }

    @Test
    public void isMediaDurationAllowed_false() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(1000000d);
        version.setVersionMimeType("video/mp4");

        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "true");
        properties.put("transcribe.automatic.enabled", "true");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "10");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        boolean result = arkCaseTranscribeService.isMediaDurationAllowed(version);

        verify(propertyFileManager).loadMultiple(any(), any());

        assertFalse(result);
    }

    @Test
    public void isAudioOrVideo_true() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setDurationSeconds(200d);
        version.setVersionMimeType("video/mp4");

        boolean result = arkCaseTranscribeService.isFileVersionTranscribable(version);

        assertTrue(result);
    }

    @Test
    public void isAudioOrVideo_false() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101l);
        version.setVersionMimeType("text/plain");

        boolean result = arkCaseTranscribeService.isFileVersionTranscribable(version);

        assertFalse(result);
    }

    @Test
    public void getConfiguration() throws Exception
    {
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "0.000000001");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        TranscribeConfiguration configuration = transcribeConfigurationPropertiesService.get();

        verify(propertyFileManager).loadMultiple(any(), any());

        assertNotNull(configuration);
        assertEquals(Integer.parseInt((String) properties.get("transcribe.confidence")), configuration.getConfidence());
        assertEquals(properties.get("transcribe.provider"), configuration.getProvider().toString());
        assertEquals(1, configuration.getProviders().size());
        assertEquals("AWS", configuration.getProviders().get(0).toString());
    }

    @Test
    public void getConfiguration_Exception() throws Exception
    {
        // Setting "transcribe.cost" to some text will throw exception - invalid number format
        Map<String, Object> properties = new HashMap<>();
        properties.put("transcribe.enabled", "false");
        properties.put("transcribe.automatic.enabled", "false");
        properties.put("transcribe.new.transcribe.for.new.version", "false");
        properties.put("transcribe.copy.transcribe.for.new.version", "true");
        properties.put("transcribe.cost", "some text");
        properties.put("transcribe.confidence", "80");
        properties.put("transcribe.number.of.files.for.processing", "10");
        properties.put("transcribe.word.count.per.item", "20");
        properties.put("transcribe.provider", "AWS");
        properties.put("transcribe.providers", "AWS");
        properties.put("transcribe.allowed.media.duration.in.seconds", 7200);

        when(propertyFileManager.loadMultiple(any(), any())).thenReturn(properties);

        try
        {
            transcribeConfigurationPropertiesService.get();
        }
        catch (Exception e)
        {
            verify(propertyFileManager).loadMultiple(any(), any());

            assertNotNull(e);
            assertTrue(e instanceof GetTranscribeConfigurationException);
            assertTrue(e.getCause() instanceof NumberFormatException);
        }
    }

    @Test
    public void saveConfiguration() throws Exception
    {
        List<TranscribeServiceProvider> providers = new ArrayList<>();
        providers.add(TranscribeServiceProvider.AWS);

        TranscribeConfiguration configuration = new TranscribeConfiguration();
        configuration.setEnabled(false);
        configuration.setAutomaticEnabled(false);
        configuration.setNewTranscriptionForNewVersion(false);
        configuration.setCopyTranscriptionForNewVersion(true);
        configuration.setCost(new BigDecimal("0.0000000001"));
        configuration.setConfidence(80);
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setWordCountPerItem(20);
        configuration.setProvider(TranscribeServiceProvider.AWS);
        configuration.setProviders(providers);
        configuration.setAllowedMediaDuration(7200);

        doNothing().when(propertyFileManager).storeMultiple(any(), any(), eq(false));

        TranscribeConfiguration saved = transcribeConfigurationPropertiesService.save(configuration);

        verify(propertyFileManager).storeMultiple(any(), any(), eq(false));

        assertNotNull(saved);
        assertEquals(configuration.getConfidence(), saved.getConfidence());
        assertEquals(configuration.getProvider().toString(), saved.getProvider().toString());
        assertEquals(configuration.getProviders().size(), saved.getProviders().size());
        assertEquals(configuration.getProviders().get(0).toString(), saved.getProviders().get(0).toString());
    }
}
