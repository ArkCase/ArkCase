package com.armedia.acm.services.transcribe.job;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.transcribe.factory.TranscribeServiceFactory;
import com.armedia.acm.services.transcribe.model.Transcribe;
import com.armedia.acm.services.transcribe.model.TranscribeActionType;
import com.armedia.acm.services.transcribe.model.TranscribeBusinessProcessVariableKey;
import com.armedia.acm.services.transcribe.model.TranscribeConfiguration;
import com.armedia.acm.services.transcribe.model.TranscribeServiceProvider;
import com.armedia.acm.services.transcribe.model.TranscribeStatusType;
import com.armedia.acm.services.transcribe.service.ArkCaseTranscribeServiceImpl;
import com.armedia.acm.services.transcribe.service.TranscribeService;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    private ArkCaseTranscribeServiceImpl arkCaseTranscribeService;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Mock
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Mock
    private TranscribeServiceFactory transcribeServiceFactory;

    @Mock
    private TranscribeService transcribeService;

    private Map<String, Object> processVariables1;
    private Map<String, Object> processVariables2;
    private Map<String, Object> processVariables3;
    private Map<String, Object> processVariables4;
    private Map<String, Object> processVariables5;

    @Before
    public void setUp() throws Exception
    {
        arkCaseTranscribeService.setTranscribeServiceFactory(transcribeServiceFactory);

        transcribeQueueJob = new TranscribeQueueJob();
        transcribeQueueJob.setArkCaseTranscribeService(arkCaseTranscribeService);
        transcribeQueueJob.setActivitiRuntimeService(activitiRuntimeService);
        transcribeQueueJob.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(2018, 3, 1);
        processVariables1 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(101l));
        processVariables1.put("CREATED", calendar1.getTime());

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(2018, 3, 2);
        processVariables2 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(102l));
        processVariables2.put("CREATED", calendar2.getTime());

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(2018, 3, 2);
        processVariables3 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(103l));
        processVariables3.put("CREATED", calendar3.getTime());

        Calendar calendar4 = Calendar.getInstance();
        calendar4.set(2018, 3, 3);
        processVariables4 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(104l));
        processVariables4.put("CREATED", calendar4.getTime());

        Calendar calendar5 = Calendar.getInstance();
        calendar5.set(2018, 3, 4);
        processVariables5 = new HashMap<>();
        processVariables1.put("IDS", Arrays.asList(105l));
        processVariables5.put("CREATED", calendar5.getTime());

    }

    @Test
    public void executeTask() throws Exception
    {
        TranscribeConfiguration configuration = new TranscribeConfiguration();
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setProvider(TranscribeServiceProvider.AWS);

        Transcribe serviceProviderTranscribe = new Transcribe();
        serviceProviderTranscribe.setStatus(TranscribeStatusType.PROCESSING.toString());

        Transcribe transcribe1 = new Transcribe();
        transcribe1.setProcessId("123");
        transcribe1.setStatus(TranscribeStatusType.PROCESSING.toString());

        Transcribe transcribe2 = new Transcribe();
        transcribe2.setProcessId("456");
        transcribe2.setStatus(TranscribeStatusType.PROCESSING.toString());

        Transcribe transcribe3 = new Transcribe();
        transcribe3.setProcessId("789");
        transcribe3.setStatus(TranscribeStatusType.PROCESSING.toString());

        Transcribe transcribe4 = new Transcribe();
        transcribe4.setProcessId("789");
        transcribe4.setStatus(TranscribeStatusType.PROCESSING.toString());

        List<Transcribe> transcribes = new ArrayList<>();
        transcribes.add(transcribe1);
        transcribes.add(transcribe2);
        transcribes.add(transcribe3);
        transcribes.add(transcribe4);

        String variableKey = TranscribeBusinessProcessVariableKey.STATUS.toString();
        String variableValue = TranscribeStatusType.QUEUED.toString();

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

        when(arkCaseTranscribeService.getConfiguration()).thenReturn(configuration);
        when(arkCaseTranscribeService.getAllByStatus(TranscribeStatusType.PROCESSING.toString())).thenReturn(transcribes);
        when(activitiRuntimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.variableValueEqualsIgnoreCase(variableKey, variableValue)).thenReturn(processInstanceQuery);
        when(processInstanceQuery.includeProcessVariables()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.list()).thenReturn(processInstances);
        when(arkCaseTranscribeService.get(105l)).thenReturn(transcribe1);
        when(arkCaseTranscribeService.getTranscribeServiceFactory()).thenReturn(transcribeServiceFactory);
        when(transcribeServiceFactory.getService(TranscribeServiceProvider.AWS)).thenReturn(transcribeService);
        when(transcribeService.create(transcribe1)).thenReturn(serviceProviderTranscribe);
        doNothing().when(arkCaseTranscribeService).signal(processInstance1, TranscribeStatusType.PROCESSING.toString(),
                TranscribeActionType.PROCESSING.toString());
        doNothing().when(auditPropertyEntityAdapter).setUserId("TRANSCRIBE_SERVICE");

        transcribeQueueJob.executeTask();

        verify(arkCaseTranscribeService).getConfiguration();
        verify(arkCaseTranscribeService).getAllByStatus(TranscribeStatusType.PROCESSING.toString());
        verify(activitiRuntimeService).createProcessInstanceQuery();
        verify(processInstanceQuery).variableValueEqualsIgnoreCase(variableKey, variableValue);
        verify(processInstanceQuery).list();
        verify(arkCaseTranscribeService).get(105L);
        verify(arkCaseTranscribeService).getTranscribeServiceFactory();
        verify(transcribeServiceFactory).getService(TranscribeServiceProvider.AWS);
        verify(transcribeService).create(transcribe1);
        verify(arkCaseTranscribeService).signal(processInstance1, TranscribeStatusType.PROCESSING.toString(),
                TranscribeActionType.PROCESSING.toString());
        verify(auditPropertyEntityAdapter).setUserId("TRANSCRIBE_SERVICE");
    }
}
