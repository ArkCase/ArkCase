package com.armedia.acm.services.job;

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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;
import com.armedia.acm.services.mediaengine.factory.MediaEngineServiceFactory;
import com.armedia.acm.services.mediaengine.mapper.MediaEngineMapper;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineActionType;
import com.armedia.acm.services.mediaengine.model.MediaEngineBusinessProcessVariableKey;
import com.armedia.acm.services.mediaengine.model.MediaEngineConstants;
import com.armedia.acm.services.mediaengine.model.MediaEngineServices;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.transcribe.factory.TranscribeProviderFactory;
import com.armedia.acm.services.transcribe.job.TranscribeQueueJob;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeConstants;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeServiceImpl;
import com.armedia.acm.services.transcribe.service.TranscribeConfigurationService;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.transcribe.service.AWSTranscribeServiceImpl;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/15/2018
 */
@RunWith(MockitoJUnitRunner.class)
public class TranscribeQueueJobTest
{
    private TranscribeQueueJob transcribeQueueJob;

    @Mock
    private TranscribeConfigurationService transcribeConfigurationService;

    @Mock
    private ArkCaseTranscribeServiceImpl arkCaseTranscribeService;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Mock
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Mock
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    @Mock
    private MediaEngineMapper mediaEngineMapper;

    @Mock
    private AcmObjectLockService objectLockService;

    @Mock
    private AcmObjectLockingManager objectLockingManager;

    @Mock
    private TranscribeProviderFactory transcribeProviderFactory;

    @Mock
    private AWSTranscribeServiceImpl awsTranscribeService;

    private Map<String, Object> processVariables1;
    private Map<String, Object> processVariables2;
    private Map<String, Object> processVariables3;
    private Map<String, Object> processVariables4;
    private Map<String, Object> processVariables5;

    private static final String AWSprovider = "AWS";

    @Before
    public void setUp() throws Exception
    {
        arkCaseTranscribeService.setMediaEngineServiceFactory(mediaEngineServiceFactory);

        transcribeQueueJob = new TranscribeQueueJob();
        transcribeQueueJob.setArkCaseTranscribeService(arkCaseTranscribeService);
        transcribeQueueJob.setActivitiRuntimeService(activitiRuntimeService);
        transcribeQueueJob.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        transcribeQueueJob.setMediaEngineMapper(mediaEngineMapper);
        transcribeQueueJob.setTranscribeProviderFactory(transcribeProviderFactory);
        transcribeQueueJob.setObjectLockingManager(objectLockingManager);
        transcribeQueueJob.setObjectLockService(objectLockService);
        transcribeQueueJob.setTranscribeConfigurationService(transcribeConfigurationService);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2018, 3, 1);
        processVariables1 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(101L));
        processVariables1.put("CREATED", calendar1.getTime());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2018, 3, 2);
        processVariables2 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(102L));
        processVariables2.put("CREATED", calendar2.getTime());

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(2018, 3, 2);
        processVariables3 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(103L));
        processVariables3.put("CREATED", calendar3.getTime());

        Calendar calendar4 = Calendar.getInstance();
        calendar4.set(2018, 3, 3);
        processVariables4 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(104L));
        processVariables4.put("CREATED", calendar4.getTime());

        Calendar calendar5 = Calendar.getInstance();
        calendar5.set(2018, 3, 4);
        processVariables5 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(105L));
        processVariables5.put("CREATED", calendar5.getTime());

    }

    @Test
    public void executeTask() throws Exception
    {
        TranscribeConfiguration configuration = new TranscribeConfiguration();
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setProvider(AWSprovider);
        configuration.setService(MediaEngineServices.TRANSCRIBE);
        configuration.setTempPath("default");

        Transcribe serviceProviderTranscribe = new Transcribe();

        serviceProviderTranscribe.setStatus(MediaEngineStatusType.PROCESSING.toString());

        EcmFile file = new EcmFile();
        file.setFileId(105L);

        EcmFileVersion version = new EcmFileVersion();
        version.setFile(file);

        Transcribe transcribe1 = new Transcribe();
        transcribe1.setProcessId("123");
        transcribe1.setStatus(MediaEngineStatusType.PROCESSING.toString());
        transcribe1.setMediaEcmFileVersion(version);

        Transcribe transcribe2 = new Transcribe();
        transcribe2.setProcessId("456");
        transcribe2.setStatus(MediaEngineStatusType.PROCESSING.toString());

        Transcribe transcribe3 = new Transcribe();
        transcribe3.setProcessId("789");
        transcribe3.setStatus(MediaEngineStatusType.PROCESSING.toString());

        Transcribe transcribe4 = new Transcribe();
        transcribe4.setProcessId("789");
        transcribe4.setStatus(MediaEngineStatusType.PROCESSING.toString());

        List<MediaEngine> transcribes = new ArrayList<>();
        transcribes.add(transcribe1);
        transcribes.add(transcribe2);
        transcribes.add(transcribe3);
        transcribes.add(transcribe4);

        String variableKey = MediaEngineBusinessProcessVariableKey.STATUS.toString();
        String variableValue = MediaEngineStatusType.QUEUED.toString();

        String serviceNameKey = MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString();
        String serviceNameValue = TranscribeConstants.SERVICE;

        ProcessInstance processInstance1 = new ProcessInstanceTest("1", processVariables1);
        ProcessInstance processInstance2 = new ProcessInstanceTest("2", processVariables2);
        ProcessInstance processInstance3 = new ProcessInstanceTest("3", processVariables3);
        ProcessInstance processInstance4 = new ProcessInstanceTest("4", processVariables4);
        ProcessInstance processInstance5 = new ProcessInstanceTest("5", processVariables5);

        List<ProcessInstance> processInstances = new ArrayList<>();
        processInstances.add(processInstance5);
        processInstances.add(processInstance2);
        processInstances.add(processInstance4);
        processInstances.add(processInstance1);
        processInstances.add(processInstance3);

        MediaEngineDTO mediaEngineDTO = new MediaEngineDTO();
        mediaEngineDTO.setProcessId("123");
        mediaEngineDTO.setStatus(MediaEngineStatusType.PROCESSING.toString());
        mediaEngineDTO.setMediaEcmFileVersion(Mockito.mock(File.class));

        AcmObjectLock lock = new AcmObjectLock();
        lock.setCreator(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        when(transcribeConfigurationService.loadProperties()).thenReturn(configuration);
        when(arkCaseTranscribeService.getAllByStatus(MediaEngineStatusType.PROCESSING.toString())).thenReturn(transcribes);
        when(activitiRuntimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.variableValueEqualsIgnoreCase(variableKey, variableValue))
                .thenReturn(processInstanceQuery);
        when(processInstanceQuery.variableValueEqualsIgnoreCase(variableKey, variableValue)
                .variableValueEqualsIgnoreCase(serviceNameKey, serviceNameValue))
                        .thenReturn(processInstanceQuery);
        when(processInstanceQuery.includeProcessVariables()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.list()).thenReturn(processInstances);
        when(arkCaseTranscribeService.get(105L)).thenReturn(transcribe1);
        when(transcribeQueueJob.getObjectLockService().findLock(105L, EcmFileConstants.OBJECT_FILE_TYPE)).thenReturn(lock);
        when(transcribeQueueJob.getObjectLockingManager().acquireObjectLock(105L, EcmFileConstants.OBJECT_FILE_TYPE,
                MediaEngineConstants.LOCK_TYPE_WRITE, null, true, TranscribeConstants.TRANSCRIBE_SYSTEM_USER))
                        .thenReturn(lock);
        when(transcribeQueueJob.getMediaEngineMapper().mediaEngineToDTO(transcribe1, "default")).thenReturn(mediaEngineDTO);
        when(transcribeQueueJob.getTranscribeProviderFactory().getProvider(AWSprovider)).thenReturn(awsTranscribeService);

        doNothing().when(arkCaseTranscribeService).signal(processInstance1, MediaEngineStatusType.PROCESSING.toString(),
                MediaEngineActionType.PROCESSING.toString());
        doNothing().when(auditPropertyEntityAdapter).setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);

        transcribeQueueJob.executeTask();

        verify(transcribeConfigurationService, times(1)).loadProperties();
        verify(arkCaseTranscribeService).getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
        verify(activitiRuntimeService).createProcessInstanceQuery();
        verify(processInstanceQuery, times(2)).variableValueEqualsIgnoreCase(variableKey, variableValue);
        verify(processInstanceQuery).list();
        verify(arkCaseTranscribeService).get(105L);
        verify(objectLockService).findLock(105L, EcmFileConstants.OBJECT_FILE_TYPE);
        verify(objectLockingManager).acquireObjectLock(105L, EcmFileConstants.OBJECT_FILE_TYPE,
                MediaEngineConstants.LOCK_TYPE_WRITE, null, true, TranscribeConstants.TRANSCRIBE_SYSTEM_USER);
        verify(transcribeProviderFactory).getProvider(AWSprovider);
        verify(awsTranscribeService).create(mediaEngineDTO);
        verify(arkCaseTranscribeService).signal(processInstance1, MediaEngineStatusType.PROCESSING.toString(),
                MediaEngineActionType.PROCESSING.toString());
        verify(auditPropertyEntityAdapter).setUserId(TranscribeConstants.TRANSCRIBE_SYSTEM_USER);
    }
}
