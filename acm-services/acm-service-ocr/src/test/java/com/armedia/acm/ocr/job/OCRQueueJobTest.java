package com.armedia.acm.ocr.job;

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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.ocr.factory.OCRServiceFactory;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRServiceProvider;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.service.ArkCaseOCRServiceImpl;
import com.armedia.acm.ocr.service.OCRService;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.service.objectlock.model.AcmObjectLock;
import com.armedia.acm.service.objectlock.service.AcmObjectLockService;
import com.armedia.acm.service.objectlock.service.AcmObjectLockingManager;

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

@RunWith(MockitoJUnitRunner.class)
public class OCRQueueJobTest
{
    private OCRQueueJob ocrQueueJob;

    @Mock
    private ArkCaseOCRServiceImpl arkCaseOCRService;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Mock
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Mock
    private OCRServiceFactory ocrServiceFactory;

    @Mock
    private OCRService ocrService;

    @Mock
    private AcmObjectLockService objectLockService;

    @Mock
    private AcmObjectLockingManager acmObjectLockingManager;

    private Map<String, Object> processVariables1;
    private Map<String, Object> processVariables2;
    private Map<String, Object> processVariables3;
    private Map<String, Object> processVariables4;
    private Map<String, Object> processVariables5;

    @Before
    public void setUp() throws Exception
    {
        arkCaseOCRService.setOCRServiceFactory(ocrServiceFactory);

        acmObjectLockingManager = createMock(AcmObjectLockingManager.class);

        ocrQueueJob = new OCRQueueJob();
        ocrQueueJob.setArkCaseOCRService(arkCaseOCRService);
        ocrQueueJob.setActivitiRuntimeService(activitiRuntimeService);
        ocrQueueJob.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);
        ocrQueueJob.setObjectLockService(objectLockService);
        ocrQueueJob.setObjectLockingManager(acmObjectLockingManager);

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
        OCRConfiguration configuration = new OCRConfiguration();
        configuration.setNumberOfFilesForProcessing(10);
        configuration.setProvider(OCRServiceProvider.TESSERACT);

        AcmObjectLock lock = new AcmObjectLock();
        lock.setCreator(OCRConstants.OCR_SYSTEM_USER);

        OCR serviceProviderOCR = new OCR();
        serviceProviderOCR.setStatus(OCRStatusType.PROCESSING.toString());

        EcmFile file = new EcmFile();
        file.setFileId(1L);

        EcmFileVersion version = new EcmFileVersion();
        version.setFile(file);
        version.setId(1L);

        EcmFileVersion version1 = new EcmFileVersion();
        version1.setFile(file);
        version1.setId(2L);

        List<EcmFileVersion> versions = new ArrayList<>();
        versions.add(version);
        versions.add(version1);

        file.setVersions(versions);

        OCR ocr = new OCR();
        ocr.setProcessId("123");
        ocr.setStatus(OCRStatusType.PROCESSING.toString());
        ocr.setEcmFileVersion(version);

        OCR ocr1 = new OCR();
        ocr1.setProcessId("456");
        ocr1.setStatus(OCRStatusType.PROCESSING.toString());
        ocr1.setEcmFileVersion(version1);

        List<OCR> ocrs = new ArrayList<>();
        ocrs.add(ocr);
        ocrs.add(ocr1);

        String variableKey = OCRBusinessProcessVariableKey.STATUS.toString();
        String variableValue = OCRStatusType.QUEUED.toString();

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

        when(arkCaseOCRService.getConfiguration()).thenReturn(configuration);
        when(arkCaseOCRService.getAllByStatus(OCRStatusType.PROCESSING.toString())).thenReturn(ocrs);
        when(activitiRuntimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.variableValueEqualsIgnoreCase(variableKey, variableValue)).thenReturn(processInstanceQuery);
        when(processInstanceQuery.includeProcessVariables()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.list()).thenReturn(processInstances);
        when(arkCaseOCRService.get(105l)).thenReturn(ocr);
        when(arkCaseOCRService.getOCRServiceFactory()).thenReturn(ocrServiceFactory);
        when(ocrServiceFactory.getService(OCRServiceProvider.TESSERACT)).thenReturn(ocrService);
        when(ocrService.create(ocr)).thenReturn(serviceProviderOCR);
        doNothing().when(arkCaseOCRService).signal(processInstance1, OCRStatusType.PROCESSING.toString(),
                OCRActionType.PROCESSING.toString());
        doNothing().when(auditPropertyEntityAdapter).setUserId(OCRConstants.OCR_SYSTEM_USER);
        when(ocrQueueJob.getObjectLockService().findLock(any(), any())).thenReturn(lock);

        expect(acmObjectLockingManager.acquireObjectLock(eq(1L), eq("FILE"), eq(OCRConstants.OCR_SYSTEM_USER), eq(null), eq(true),
                eq(OCRConstants.OCR_SYSTEM_USER)))
                        .andAnswer(() -> {
                            AcmObjectLock lock1 = new AcmObjectLock();
                            lock.setCreator(OCRConstants.OCR_SYSTEM_USER);
                            lock.setId(1l);
                            lock.setObjectId(1L);
                            lock.setObjectType("FILE");
                            lock.setExpiry(null);
                            return lock;
                        });

        ocrQueueJob.executeTask();

        verify(arkCaseOCRService).getConfiguration();
        verify(arkCaseOCRService).getAllByStatus(OCRStatusType.PROCESSING.toString());
        verify(activitiRuntimeService).createProcessInstanceQuery();
        verify(processInstanceQuery).variableValueEqualsIgnoreCase(variableKey, variableValue);
        verify(processInstanceQuery).list();
        verify(arkCaseOCRService).get(105L);
        verify(arkCaseOCRService).getOCRServiceFactory();
        verify(ocrServiceFactory).getService(OCRServiceProvider.TESSERACT);
        verify(ocrService).create(ocr);
        verify(arkCaseOCRService).signal(processInstance1, OCRStatusType.PROCESSING.toString(),
                OCRActionType.PROCESSING.toString());
        verify(auditPropertyEntityAdapter).setUserId(OCRConstants.OCR_SYSTEM_USER);
    }
}
