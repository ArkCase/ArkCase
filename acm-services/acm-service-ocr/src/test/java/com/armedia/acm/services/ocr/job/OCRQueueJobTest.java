package com.armedia.acm.services.ocr.job;

/*-
 * #%L
 * ACM Services: Optical character recognition via Tesseract
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.services.ocr.factory.OCRProviderFactory;
import com.armedia.acm.services.ocr.model.OCR;
import com.armedia.acm.services.ocr.model.OCRConfiguration;
import com.armedia.acm.services.ocr.model.OCRConstants;
import com.armedia.acm.services.ocr.service.ArkCaseOCRServiceImpl;
import com.armedia.acm.services.ocr.service.OCRConfigurationService;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.ocr.service.TesseractServiceImpl;

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

@RunWith(MockitoJUnitRunner.class)
public class OCRQueueJobTest
{
    private OCRQueueJob ocrQueueJob;

    @Mock
    private ArkCaseOCRServiceImpl arkCaseOCRService;

    @Mock
    private OCRConfigurationService ocrConfigurationService;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Mock
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Mock
    private MediaEngineServiceFactory mediaEngineServiceFactory;

    @Mock
    private AcmObjectLockService objectLockService;

    @Mock
    private AcmObjectLockingManager objectLockingManager;

    @Mock
    private OCRProviderFactory ocrProviderFactory;

    @Mock
    private MediaEngineMapper mediaEngineMapper;

    @Mock
    TesseractServiceImpl tesseractService;

    private static final String TESSERACT = "TESSERACT";

    private Map<String, Object> processVariables1;
    private Map<String, Object> processVariables2;
    private Map<String, Object> processVariables3;
    private Map<String, Object> processVariables4;
    private Map<String, Object> processVariables5;

    @Before
    public void setUp() throws Exception
    {
        arkCaseOCRService.setMediaEngineServiceFactory(mediaEngineServiceFactory);

        ocrQueueJob = new OCRQueueJob();
        ocrQueueJob.setArkCaseOCRService(arkCaseOCRService);
        ocrQueueJob.setActivitiRuntimeService(activitiRuntimeService);
        ocrQueueJob.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        ocrQueueJob.setOcrConfigurationService(ocrConfigurationService);
        ocrQueueJob.setObjectLockingManager(objectLockingManager);
        ocrQueueJob.setObjectLockService(objectLockService);
        ocrQueueJob.setOcrProviderFactory(ocrProviderFactory);
        ocrQueueJob.setMediaEngineMapper(mediaEngineMapper);

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
        OCRConfiguration configuration = new OCRConfiguration();
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setProvider(TESSERACT);
        configuration.setService(MediaEngineServices.OCR);
        configuration.setTempPath("default");

        OCR serviceProviderOCR = new OCR();
        serviceProviderOCR.setStatus(MediaEngineStatusType.PROCESSING.toString());

        EcmFile file = new EcmFile();
        file.setFileId(105L);

        EcmFileVersion version = new EcmFileVersion();
        version.setFile(file);

        OCR ocr = new OCR();
        ocr.setProcessId("123");
        ocr.setStatus(MediaEngineStatusType.PROCESSING.toString());
        ocr.setMediaEcmFileVersion(version);

        OCR ocr1 = new OCR();
        ocr1.setProcessId("456");
        ocr1.setStatus(MediaEngineStatusType.PROCESSING.toString());

        OCR ocr2 = new OCR();
        ocr2.setProcessId("789");
        ocr2.setStatus(MediaEngineStatusType.PROCESSING.toString());

        OCR ocr3 = new OCR();
        ocr3.setProcessId("789");
        ocr3.setStatus(MediaEngineStatusType.PROCESSING.toString());

        List<MediaEngine> ocrs = new ArrayList<>();
        ocrs.add(ocr);
        ocrs.add(ocr1);
        ocrs.add(ocr2);
        ocrs.add(ocr3);

        String variableKey = MediaEngineBusinessProcessVariableKey.STATUS.toString();
        String variableValue = MediaEngineStatusType.QUEUED.toString();

        String serviceNameKey = MediaEngineBusinessProcessVariableKey.SERVICE_NAME.toString();
        String serviceNameValue = OCRConstants.SERVICE;

        MediaEngineDTO mediaEngineDTO = new MediaEngineDTO();
        mediaEngineDTO.setProcessId("123");
        mediaEngineDTO.setStatus(MediaEngineStatusType.PROCESSING.toString());
        mediaEngineDTO.setMediaEcmFileVersion(Mockito.mock(File.class));

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

        AcmObjectLock lock = new AcmObjectLock();
        lock.setCreator(OCRConstants.OCR_SYSTEM_USER);

        when(ocrConfigurationService.loadProperties()).thenReturn(configuration);

        when(arkCaseOCRService.getAllByStatus(MediaEngineStatusType.PROCESSING.toString())).thenReturn(ocrs);
        when(activitiRuntimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.variableValueEqualsIgnoreCase(variableKey, variableValue))
                .thenReturn(processInstanceQuery);
        when(processInstanceQuery.variableValueEqualsIgnoreCase(variableKey, variableValue)
                .variableValueEqualsIgnoreCase(serviceNameKey, serviceNameValue))
                        .thenReturn(processInstanceQuery);
        when(processInstanceQuery.includeProcessVariables()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.list()).thenReturn(processInstances);
        when(arkCaseOCRService.get(105L)).thenReturn(ocr);
        when(ocrQueueJob.getObjectLockService().findLock(105L, EcmFileConstants.OBJECT_FILE_TYPE)).thenReturn(lock);
        when(ocrQueueJob.getObjectLockingManager().acquireObjectLock(105L, EcmFileConstants.OBJECT_FILE_TYPE,
                MediaEngineConstants.LOCK_TYPE_WRITE, null, true, OCRConstants.OCR_SYSTEM_USER))
                        .thenReturn(lock);
        when(ocrQueueJob.getMediaEngineMapper().mediaEngineToDTO(ocr, "default")).thenReturn(mediaEngineDTO);
        when(ocrQueueJob.getOcrProviderFactory().getProvider(TESSERACT)).thenReturn(tesseractService);
        doNothing().when(arkCaseOCRService).signal(processInstance1, MediaEngineStatusType.PROCESSING.toString(),
                MediaEngineActionType.PROCESSING.toString());
        doNothing().when(auditPropertyEntityAdapter).setUserId(OCRConstants.OCR_SYSTEM_USER);

        ocrQueueJob.executeTask();

        verify(ocrConfigurationService, times(3)).loadProperties();
        verify(arkCaseOCRService).getAllByStatus(MediaEngineStatusType.PROCESSING.toString());
        verify(activitiRuntimeService).createProcessInstanceQuery();
        verify(processInstanceQuery, times(2)).variableValueEqualsIgnoreCase(variableKey, variableValue);
        verify(processInstanceQuery).list();
        verify(arkCaseOCRService).get(105L);
        verify(objectLockService).findLock(105l, EcmFileConstants.OBJECT_FILE_TYPE);
        verify(objectLockingManager).acquireObjectLock(105l, EcmFileConstants.OBJECT_FILE_TYPE,
                MediaEngineConstants.LOCK_TYPE_WRITE, null, true, OCRConstants.OCR_SYSTEM_USER);
        verify(ocrProviderFactory).getProvider(TESSERACT);
        verify(tesseractService).create(mediaEngineDTO);
        verify(arkCaseOCRService).signal(processInstance1, MediaEngineStatusType.PROCESSING.toString(),
                MediaEngineActionType.PROCESSING.toString());
        verify(auditPropertyEntityAdapter).setUserId(OCRConstants.OCR_SYSTEM_USER);
    }
}
