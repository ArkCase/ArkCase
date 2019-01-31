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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.ocr.factory.OCRServiceFactory;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRActionType;
import com.armedia.acm.ocr.model.OCRBusinessProcessVariableKey;
import com.armedia.acm.ocr.model.OCRConfiguration;
import com.armedia.acm.ocr.model.OCRServiceProvider;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.service.ArkCaseOCRServiceImpl;
import com.armedia.acm.ocr.service.OCRService;

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

    private Map<String, Object> processVariables1;
    private Map<String, Object> processVariables2;
    private Map<String, Object> processVariables3;
    private Map<String, Object> processVariables4;
    private Map<String, Object> processVariables5;

    @Before
    public void setUp() throws Exception
    {
        arkCaseOCRService.setOCRServiceFactory(ocrServiceFactory);

        ocrQueueJob = new OCRQueueJob();
        ocrQueueJob.setArkCaseOCRService(arkCaseOCRService);
        ocrQueueJob.setActivitiRuntimeService(activitiRuntimeService);
        ocrQueueJob.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);

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

        OCR serviceProviderOCR = new OCR();
        serviceProviderOCR.setStatus(OCRStatusType.PROCESSING.toString());

        OCR ocr = new OCR();
        ocr.setProcessId("123");
        ocr.setStatus(OCRStatusType.PROCESSING.toString());

        OCR ocr1 = new OCR();
        ocr1.setProcessId("456");
        ocr1.setStatus(OCRStatusType.PROCESSING.toString());

        OCR ocr2 = new OCR();
        ocr2.setProcessId("789");
        ocr2.setStatus(OCRStatusType.PROCESSING.toString());

        OCR ocr3 = new OCR();
        ocr3.setProcessId("789");
        ocr3.setStatus(OCRStatusType.PROCESSING.toString());

        List<OCR> ocrs = new ArrayList<>();
        ocrs.add(ocr);
        ocrs.add(ocr1);
        ocrs.add(ocr2);
        ocrs.add(ocr3);

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
        doNothing().when(auditPropertyEntityAdapter).setUserId("OCR_SERVICE");

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
        verify(auditPropertyEntityAdapter).setUserId("OCR_SERVICE");
    }
}
