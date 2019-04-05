package com.armedia.acm.services.ocr.service;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.armedia.acm.plugins.ecm.dao.EcmFileVersionDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.services.mediaengine.exception.CreateMediaEngineException;
import com.armedia.acm.services.mediaengine.model.MediaEngine;
import com.armedia.acm.services.mediaengine.model.MediaEngineStatusType;
import com.armedia.acm.services.mediaengine.model.MediaEngineType;
import com.armedia.acm.services.mediaengine.pipeline.MediaEnginePipelineContext;
import com.armedia.acm.services.mediaengine.rules.MediaEngineBusinessProcessRulesExecutor;
import com.armedia.acm.services.mediaengine.service.MediaEngineEventPublisher;
import com.armedia.acm.services.ocr.dao.OCRDao;
import com.armedia.acm.services.ocr.model.OCR;
import com.armedia.acm.services.ocr.model.OCRConfiguration;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.spring.SpringContextHolder;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by Vladimir Cherepnalkovski
 */
@RunWith(MockitoJUnitRunner.class)
public class ArkCaseOCRServiceTest extends EasyMockSupport
{

    private ArkCaseOCRServiceImpl arkCaseOCRService;
    private OCRConfigurationService ocrConfigurationService;

    @Mock
    private OCRConfiguration ocrConfiguration;

    @Mock
    private RuntimeService activitiRuntimeService;

    @Mock
    private ProcessInstance processInstance;

    @Mock
    private EcmFileVersionDao ecmFileVersionDao;

    @Mock
    private ProcessInstanceQuery processInstanceQuery;

    @Mock
    private PipelineManager<MediaEngine, MediaEnginePipelineContext> pipelineManager;

    @Mock
    private MediaEngineBusinessProcessRulesExecutor mediaEngineBusinessProcessRulesExecutor;

    @Mock
    private MediaEngineEventPublisher mediaEngineEventPublisher;

    @Mock
    private OCRDao ocrDao;

    @Mock
    private SpringContextHolder springContextHolder;

    private static String OCR = "OCR";
    private static String TESSERACT = "TESSERACT";
    private static String IMAGE_FORMAT = "image/";

    @Before
    public void setUp()
    {
        ocrConfiguration = new OCRConfiguration();
        ocrConfiguration.setEnabled(true);
        ocrConfiguration.setAutomaticEnabled(true);
        ocrConfiguration.setExcludedFileTypes("");

        ocrConfigurationService = new OCRConfigurationService();
        ocrConfigurationService.setOcrConfig(ocrConfiguration);

        arkCaseOCRService = new ArkCaseOCRServiceImpl();
        arkCaseOCRService.setEcmFileVersionDao(ecmFileVersionDao);
        arkCaseOCRService.setOcrConfigurationService(ocrConfigurationService);
        arkCaseOCRService.setPipelineManager(pipelineManager);
        arkCaseOCRService.setMediaEngineBusinessProcessRulesExecutor(mediaEngineBusinessProcessRulesExecutor);
        arkCaseOCRService.setActivitiRuntimeService(activitiRuntimeService);
        arkCaseOCRService.setMediaEngineEventPublisher(mediaEngineEventPublisher);
        arkCaseOCRService.setOcrDao(ocrDao);
        arkCaseOCRService.setSpringContextHolder(springContextHolder);
    }

    @Test
    public void create_By_Version_Id() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileType(IMAGE_FORMAT);
        file.setFileId(105L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType(IMAGE_FORMAT);
        version.setFile(file);

        OCR ocr = new OCR();
        ocr.setId(102L);
        ocr.setType(MediaEngineType.AUTOMATIC.toString());
        ocr.setMediaEcmFileVersion(version);

        when(ecmFileVersionDao.find(version.getId())).thenReturn(version);
        when(ocrDao.findByFileIdAndStatus(file.getId(), MediaEngineStatusType.QUEUED)).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenReturn(ocr);

        MediaEngine created = arkCaseOCRService.create(version.getId(), MediaEngineType.AUTOMATIC);

        verify(ecmFileVersionDao).find(version.getId());
        verify(ocrDao).findByFileIdAndStatus(file.getId(), MediaEngineStatusType.QUEUED);
        verify(pipelineManager).executeOperation(any(), any(), any());

        assertNotNull(created);
        assertEquals(ocr, created);
    }

    @Test
    public void create_By_Version() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileType(IMAGE_FORMAT);
        file.setFileId(105L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType(IMAGE_FORMAT);
        version.setFile(file);

        OCR ocr = new OCR();
        ocr.setId(102L);
        ocr.setType(MediaEngineType.AUTOMATIC.toString());
        ocr.setMediaEcmFileVersion(version);

        when(ocrDao.findByFileIdAndStatus(file.getId(), MediaEngineStatusType.QUEUED)).thenReturn(null);
        when(pipelineManager.executeOperation(any(), any(), any())).thenReturn(ocr);

        MediaEngine created = arkCaseOCRService.create(version, MediaEngineType.AUTOMATIC);

        verify(ocrDao).findByFileIdAndStatus(file.getId(), MediaEngineStatusType.QUEUED);
        verify(pipelineManager).executeOperation(any(), any(), any());

        assertNotNull(created);
        assertEquals(ocr, created);
    }

    @Test
    public void create_By_Version_OCR_Not_Allowed() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileType(IMAGE_FORMAT);
        file.setFileId(105L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType(IMAGE_FORMAT);
        version.setFile(file);

        OCR ocr = new OCR();
        ocr.setId(102L);
        ocr.setType(MediaEngineType.AUTOMATIC.toString());
        ocr.setMediaEcmFileVersion(version);

        ocrConfiguration.setEnabled(false);

        try
        {
            arkCaseOCRService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("OCR service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_File_Not_Processable() throws Exception
    {
        EcmFile file = new EcmFile();
        file.setFileType(IMAGE_FORMAT);
        file.setFileId(105L);

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType("text/plain");
        version.setFile(file);

        try
        {
            arkCaseOCRService.create(version, MediaEngineType.AUTOMATIC);
        }
        catch (Exception e)
        {
            assertTrue(e instanceof CreateMediaEngineException);
            assertEquals("OCR service is not allowed.", e.getMessage());
        }
    }

    @Test
    public void create_By_Version_Already_Exist() throws Exception
    {
        OCR ocr = new OCR();
        ocr.setId(102L);
        ocr.setType(MediaEngineType.AUTOMATIC.toString());

        EcmFile file = new EcmFile();
        file.setFileId(123L);
        file.setFileType("user_signature");

        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(IMAGE_FORMAT);
        version.setFile(file);

        when(ocrDao.findByFileIdAndStatus(file.getId(), MediaEngineStatusType.QUEUED)).thenReturn(null);

        arkCaseOCRService.create(version, MediaEngineType.AUTOMATIC);

        verify(ocrDao).findByFileIdAndStatus(file.getId(), MediaEngineStatusType.QUEUED);
        verify(pipelineManager).executeOperation(any(), any(), any());

    }

    @Test
    public void isProcessable_true() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setDurationSeconds(200d);
        version.setVersionMimeType(IMAGE_FORMAT);

        boolean result = arkCaseOCRService.isProcessable(version);

        assertTrue(result);
    }

    @Test
    public void isProcessable_false() throws Exception
    {
        EcmFileVersion version = new EcmFileVersion();
        version.setId(101L);
        version.setVersionMimeType("text/plain");

        boolean result = arkCaseOCRService.isProcessable(version);

        assertFalse(result);
    }
}
