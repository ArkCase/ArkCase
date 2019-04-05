package com.armedia.acm.services.service;

/*-
 * #%L
 * ACM Service: Transcribe
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.mediaengine.dao.MediaEngineDao;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessModel;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.mediaengine.pipeline.MediaEnginePipelineContext;
import com.armedia.acm.services.mediaengine.rules.MediaEngineBusinessProcessRulesExecutor;
import com.armedia.acm.services.mediaengine.service.MediaEngineEventPublisher;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.transcribe.dao.TranscribeDao;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeServiceImpl;
import com.armedia.acm.services.transcribe.service.TranscribeConfigurationService;
import com.armedia.acm.spring.SpringContextHolder;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/01/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class ArkCaseTranscribeServiceTest extends EasyMockSupport
{
    private ArkCaseTranscribeServiceImpl arkCaseTranscribeService;
    private TranscribeConfigurationService transcribeConfigurationService;

    @Mock
    private TranscribeDao transcribeDao;

    @Mock
    private EcmFileVersionDao ecmFileVersionDao;

    @Mock
    private PipelineManager<MediaEngine, MediaEnginePipelineContext> pipelineManager;

    @Mock
    private MediaEngineBusinessProcessRulesExecutor mediaEngineBusinessProcessRulesExecutor;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstance processInstance;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Mock
    private MediaEngineEventPublisher mediaEngineEventPublisher;

    @Mock
    private SpringContextHolder springContextHolder;

    @Mock
    private TranscribeConfiguration transcribeConfiguration;

    private static String TRANSCRIBE = "TRANSCRIBE";
    private static String AWS_PROVIDER = "AWS";
    private static String MEDIA_ENGINE_WORKFLOW = "MediaEngineWorkFlow";
    private static String VIDEO_FORMAT = "video/mp4";

    @Before
    public void setUp()
    {
        transcribeConfiguration = new TranscribeConfiguration();
        transcribeConfiguration.setEnabled(true);
        transcribeConfiguration.setAutomaticEnabled(true);
        transcribeConfiguration.setNewMediaEngineForNewVersion(false);
        transcribeConfiguration.setCopyMediaEngineForNewVersion(true);
        transcribeConfiguration.setAllowedMediaDuration(7200);
        transcribeConfiguration.setSilentBetweenWords(BigDecimal.valueOf(2));
        transcribeConfiguration.setProvider(AWS_PROVIDER);
        transcribeConfiguration.setProviderPurgeAttempts(10);
        transcribeConfiguration.setConfidence(80);
        transcribeConfiguration.setTempPath("");
        transcribeConfiguration.setExcludedFileTypes("default");

        transcribeConfigurationService = new TranscribeConfigurationService();
        transcribeConfigurationService.setTranscribeConfig(transcribeConfiguration);

        arkCaseTranscribeService = new ArkCaseTranscribeServiceImpl();
        arkCaseTranscribeService.setTranscribeDao(transcribeDao);
        arkCaseTranscribeService.setPipelineManager(pipelineManager);
        arkCaseTranscribeService.setEcmFileVersionDao(ecmFileVersionDao);
        arkCaseTranscribeService.setMediaEngineBusinessProcessRulesExecutor(mediaEngineBusinessProcessRulesExecutor);
        arkCaseTranscribeService.setActivitiRuntimeService(activitiRuntimeService);
        arkCaseTranscribeService.setMediaEngineEventPublisher(mediaEngineEventPublisher);
        arkCaseTranscribeService.setSpringContextHolder(springContextHolder);
        arkCaseTranscribeService.setTranscribeConfigurationService(transcribeConfigurationService);
    }

    @Test
    public void create_By_Version_Id() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());

        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        Map<String, MediaEngineDao> daos = new HashMap<>();
        daos.put(TRANSCRIBE, transcribeDao);

        when(transcribeDao.getSupportedObjectType()).thenReturn(TRANSCRIBE);
        when(springContextHolder.getAllBeansOfType(MediaEngineDao.class)).thenReturn(daos);
        when(ecmFileVersionDao.find(version.getId())).thenReturn(version);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenReturn(transcribe);

        MediaEngine created = arkCaseTranscribeService.create(version.getId(), MediaEngineType.AUTOMATIC);

        verify(ecmFileVersionDao).find(version.getId());
        verify(springContextHolder).getAllBeansOfType(MediaEngineDao.class);
        verify(transcribeDao).getSupportedObjectType();
        verify(transcribeDao).findByMediaVersionId(version.getId());
        verify(pipelineManager).executeOperation(any(), any(), any());

        assertNotNull(created);
        assertEquals(transcribe, created);
    }

    @Test
    public void create_By_Version() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());

        EcmFile file = new EcmFile();
        file.setFileId(1033L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        Map<String, MediaEngineDao> daos = new HashMap<>();
        daos.put(TRANSCRIBE, transcribeDao);

        when(transcribeDao.getSupportedObjectType()).thenReturn(TRANSCRIBE);
        when(springContextHolder.getAllBeansOfType(MediaEngineDao.class)).thenReturn(daos);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenReturn(transcribe);

        MediaEngine created = arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);

        verify(springContextHolder).getAllBeansOfType(MediaEngineDao.class);
        verify(transcribeDao).getSupportedObjectType();
        verify(transcribeDao).findByMediaVersionId(version.getId());
        verify(pipelineManager).executeOperation(any(), any(), any());

        assertNotNull(created);
        assertEquals(transcribe, created);
    }

    @Test
    public void create_By_Version_Automatic_Not_Allowed() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        transcribeConfiguration.setAutomaticEnabled(false);

        Map<String, MediaEngineDao> daos = new HashMap<>();
        daos.put(TRANSCRIBE, transcribeDao);

        when(transcribeDao.getSupportedObjectType()).thenReturn(TRANSCRIBE);
        when(springContextHolder.getAllBeansOfType(MediaEngineDao.class)).thenReturn(daos);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);

        try
        {
            arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(springContextHolder).getAllBeansOfType(MediaEngineDao.class);
            verify(transcribeDao).getSupportedObjectType();

            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("Transcribe service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Transcribe_Not_Allowed() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        transcribeConfiguration.setEnabled(false);

        try
        {
            arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("TRANSCRIBE service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_File_Not_Media() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(101L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType("text/plain");
        version.setFile(file);

        try
        {
            arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("TRANSCRIBE service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Media_More_Than_2_Hours() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(1000000d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        try
        {
            arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("TRANSCRIBE service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Already_Exist() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());

        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        Map<String, MediaEngineDao> daos = new HashMap<>();
        daos.put(TRANSCRIBE, transcribeDao);

        when(transcribeDao.getSupportedObjectType()).thenReturn(TRANSCRIBE);
        when(springContextHolder.getAllBeansOfType(MediaEngineDao.class)).thenReturn(daos);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(transcribe);

        try
        {
            arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(springContextHolder).getAllBeansOfType(MediaEngineDao.class);
            verify(transcribeDao).getSupportedObjectType();
            verify(transcribeDao).findByMediaVersionId(version.getId());

            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("Creating Transcribe job is aborted. There is already Transcribe object for MEDIA_FILE_VERSION_ID=[101]",
                    e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Pipeline_Exception() throws Exception
    {
        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());

        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        Map<String, MediaEngineDao> daos = new HashMap<>();
        daos.put(TRANSCRIBE, transcribeDao);

        when(transcribeDao.getSupportedObjectType()).thenReturn(TRANSCRIBE);
        when(springContextHolder.getAllBeansOfType(MediaEngineDao.class)).thenReturn(daos);
        when(transcribeDao.findByMediaVersionId(version.getId())).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenThrow(new PipelineProcessException("Pipeline Exception"));

        try
        {
            arkCaseTranscribeService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            verify(transcribeDao).findByMediaVersionId(version.getId());
            verify(springContextHolder).getAllBeansOfType(MediaEngineDao.class);
            verify(transcribeDao).getSupportedObjectType();
            verify(pipelineManager).executeOperation(any(), any(), any());

            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("[TRANSCRIBE] for MEDIA_VERSION_ID=[101] was not created successfully. REASON=[Pipeline Exception]",
                    e.getMessage());
        }
    }

    @Test
    public void startBusinessProcess_Start_true() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(103L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setMediaEcmFileVersion(version);

        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(transcribe.getType());
        model.setName(MEDIA_ENGINE_WORKFLOW);
        model.setStart(true);

        Map processHandlerMap = Mockito.mock(HashMap.class);
        arkCaseTranscribeService.setProcessHandlerMap(processHandlerMap);

        when(processHandlerMap.get(any())).thenReturn(mediaEngineBusinessProcessRulesExecutor);
        when(mediaEngineBusinessProcessRulesExecutor.applyRules(any())).thenReturn(model);
        when(activitiRuntimeService.startProcessInstanceByKey(eq(model.getName()), (Map<String, Object>) any()))
                .thenReturn(processInstance);
        when(processInstance.getId()).thenReturn("123");

        arkCaseTranscribeService.startBusinessProcess(transcribe, arkCaseTranscribeService.getServiceName());

        verify(mediaEngineBusinessProcessRulesExecutor).applyRules(any());
        verify(activitiRuntimeService).startProcessInstanceByKey(eq(model.getName()), (Map<String, Object>) any());
        verify(processInstance).getId();

        assertEquals("123", transcribe.getProcessId());
    }

    @Test
    public void startBusinessProcess_Start_true_Existing_Process() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(103L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        List<Long> ids = new ArrayList<>();
        ids.add(999L);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setProcessId("123");
        transcribe.setRemoteId("remoteId");
        transcribe.setMediaEcmFileVersion(version);

        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("IDS", ids);
        processVariables.put("REMOTE_ID", transcribe.getRemoteId());
        processVariables.put("STATUS", MediaEngineStatusType.QUEUED);
        processVariables.put("ACTION", MediaEngineActionType.QUEUED);

        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(transcribe.getType());
        model.setName(MEDIA_ENGINE_WORKFLOW);
        model.setStart(true);

        Map processHandlerMap = Mockito.mock(HashMap.class);
        arkCaseTranscribeService.setProcessHandlerMap(processHandlerMap);

        when(processHandlerMap.get(any())).thenReturn(mediaEngineBusinessProcessRulesExecutor);

        when(mediaEngineBusinessProcessRulesExecutor.applyRules(any())).thenReturn(model);
        when(activitiRuntimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceId(transcribe.getProcessId())).thenReturn(processInstanceQuery);
        when(processInstanceQuery.includeProcessVariables()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.singleResult()).thenReturn(processInstance);
        when(processInstance.getProcessVariables()).thenReturn(processVariables);
        doNothing().when(activitiRuntimeService).setVariable(transcribe.getProcessId(), "IDS", ids);
        when(processInstance.getId()).thenReturn(transcribe.getProcessId());

        arkCaseTranscribeService.startBusinessProcess(transcribe, arkCaseTranscribeService.getServiceName());

        verify(mediaEngineBusinessProcessRulesExecutor).applyRules(any());
        verify(activitiRuntimeService).createProcessInstanceQuery();
        verify(processInstanceQuery).processInstanceId(transcribe.getProcessId());
        verify(processInstanceQuery).includeProcessVariables();
        verify(processInstanceQuery).singleResult();
        verify(processInstance).getProcessVariables();
        verify(activitiRuntimeService).setVariable(transcribe.getProcessId(), "IDS", ids);
        verify(processInstance).getId();

        assertEquals(2, ids.size());
        assertTrue(ids.contains(999L));
        assertTrue(ids.contains(transcribe.getId()));
    }

    @Test
    public void startBusinessProcess_Start_false() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileId(103L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);
        version.setFile(file);

        Transcribe transcribe = new Transcribe();
        transcribe.setId(102L);
        transcribe.setType(MediaEngineType.AUTOMATIC.toString());
        transcribe.setMediaEcmFileVersion(version);

        MediaEngineBusinessProcessModel model = new MediaEngineBusinessProcessModel();
        model.setType(transcribe.getType());
        model.setName(MEDIA_ENGINE_WORKFLOW);
        model.setStart(false);

        Map processHandlerMap = Mockito.mock(HashMap.class);
        arkCaseTranscribeService.setProcessHandlerMap(processHandlerMap);

        when(processHandlerMap.get(any())).thenReturn(mediaEngineBusinessProcessRulesExecutor);

        when(mediaEngineBusinessProcessRulesExecutor.applyRules(any())).thenReturn(model);

        arkCaseTranscribeService.startBusinessProcess(transcribe, arkCaseTranscribeService.getServiceName());

        verify(mediaEngineBusinessProcessRulesExecutor).applyRules(any());

        assertEquals(null, transcribe.getProcessId());
    }

    @Test
    public void isMediaDurationAllowed_true() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);

        boolean result = arkCaseTranscribeService.isMediaDurationAllowed(version);

        assertTrue(result);
    }

    @Test
    public void isMediaDurationAllowed_false() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(1000000d);
        version.setVersionMimeType(VIDEO_FORMAT);

        boolean result = arkCaseTranscribeService.isMediaDurationAllowed(version);

        assertFalse(result);
    }

    @Test
    public void isAudioOrVideo_true() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(VIDEO_FORMAT);

        boolean result = arkCaseTranscribeService.isProcessable(version);

        assertTrue(result);
    }

    @Test
    public void isAudioOrVideo_false() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType("text/plain");

        boolean result = arkCaseTranscribeService.isProcessable(version);

        assertFalse(result);
    }
}
