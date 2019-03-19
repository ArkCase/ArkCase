package com.armedia.acm.ocr.provider.tesseract.service;

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

import com.armedia.acm.core.model.ArkCaseProcess;
import com.armedia.acm.ocr.dao.OCRDao;
import com.armedia.acm.ocr.exception.CreateOCRException;
import com.armedia.acm.ocr.exception.GetConfigurationException;
import com.armedia.acm.ocr.exception.GetOCRException;
import com.armedia.acm.ocr.model.OCR;
import com.armedia.acm.ocr.model.OCRConstants;
import com.armedia.acm.ocr.model.OCRStatusType;
import com.armedia.acm.ocr.service.OCRConfigurationPropertiesService;
import com.armedia.acm.ocr.service.OCREventPublisher;
import com.armedia.acm.ocr.service.OCRService;
import com.armedia.acm.pluginmanager.service.AcmConfigurablePlugin;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;

import org.activiti.engine.RuntimeService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TesseractOCRService implements OCRService, AcmConfigurablePlugin
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private OCREventPublisher ocrEventPublisher;
    private OCRConfigurationPropertiesService ocrConfigurationPropertiesService;
    private EcmFileTransaction ecmFileTransaction;
    private OCRDao ocrDao;
    private RuntimeService activitiRuntimeService;
    private Map<String, ArkCaseProcess> processes = new HashMap<>();
    private Runtime rt;

    @Override
    public OCR create(OCR ocr) throws CreateOCRException
    {
        EcmFile file = ocr.getEcmFileVersion().getFile();
        File tmpFile = null;
        try
        {
            long timeStamp = System.currentTimeMillis();
            try (InputStream stream = ecmFileTransaction.downloadFileTransactionAsInputStream(file))
            {
                tmpFile = File.createTempFile(ocr.getId() + OCRConstants.TEMP_FILE_PREFIX + timeStamp, ("." + file.getFileExtension()),
                        new File(System.getProperty("java.io.tmpdir")));
                FileUtils.copyInputStreamToFile(stream, tmpFile);
                getActivitiRuntimeService().setVariable(ocr.getProcessId(), OCRConstants.UPLOADED_TMP, tmpFile.getAbsolutePath());
            }
        }
        catch (MuleException | IOException e)
        {
            throw new CreateOCRException(String.format("Unable to create OCR-ed pdf,  REASON=[%s].", e.getMessage()), e);
        }

        if (tmpFile != null)
        {
            String source = tmpFile.getAbsolutePath();

            if (file.getFileActiveVersionMimeType().equalsIgnoreCase(OCRConstants.MEDIA_TYPE_PDF_RECOGNITION_KEY))
            {
                runImageMagick(ocr, source);
                source = (String) getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.MAGICK_TMP);
            }
            try
            {
                runTesseract(ocr, source);
                source = (String) getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.TESSERACT_TMP);
            }
            catch (Exception e)
            {
                String message = "Error while running Tesseract for FILE_ID=" +
                        +ocr.getEcmFileVersion().getFile().getFileId() + ". Reason: " + e.getMessage();
                LOG.error(message, e);
                throw new CreateOCRException(message, e);
            }

            Process pr = null;
            try
            {
                pr = runQPDF(ocr, source);
            }
            catch (Exception e)
            {
                LOG.error("Can't create linearized PDF for FILE_ID=[{}], REASON+[{}]", ocr.getEcmFileVersion().getFile().getId(),
                        e.getMessage(), e);
            }
            processes.put(ocr.getRemoteId(), new ArkCaseProcess(ocr.getRemoteId(), pr));
        }
        return ocr;
    }

    private void runImageMagick(OCR ocr, String source) throws CreateOCRException
    {
        rt = Runtime.getRuntime();
        long timeStamp = System.currentTimeMillis();
        String destination = System.getProperty("java.io.tmpdir") + ocr.getId() + OCRConstants.TEMP_FILE_PREFIX + timeStamp
                + OCRConstants.MAGICK_TMP
                + OCRConstants.TEMP_FILE_PNG_SUFFIX;
        String command = buildCommand(OCRConstants.IMAGE_MAGICK, ocr, source, destination);
        try
        {
            Process pr = rt.exec(command);
            pr.waitFor();
            getActivitiRuntimeService().setVariable(ocr.getProcessId(), OCRConstants.MAGICK_TMP, destination);
        }
        catch (IOException | InterruptedException e)
        {
            String message = "Error while converting pdf to image with Image Magick for FILE_ID=" +
                    +ocr.getEcmFileVersion().getFile().getFileId() + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateOCRException(message, e);
        }
    }

    private void runTesseract(OCR ocr, String source) throws CreateOCRException
    {
        rt = Runtime.getRuntime();
        long timeStamp = System.currentTimeMillis();
        String destination = System.getProperty("java.io.tmpdir") + ocr.getId() + OCRConstants.TEMP_FILE_PREFIX + timeStamp
                + OCRConstants.TESSERACT_TMP;
        String command = buildCommand(OCRConstants.TESSERACT_COMMAND_PREFIX, ocr, source, destination);
        try
        {
            Process pr = rt.exec(command);
            pr.waitFor();
            getActivitiRuntimeService().setVariable(ocr.getProcessId(), OCRConstants.TESSERACT_TMP, (destination +
                    OCRConstants.TEMP_FILE_PDF_SUFFIX));
        }
        catch (InterruptedException | IOException e)
        {
            String message = "Error while trying to create OCR-ed pdf with Tesseract for FILE_ID="
                    + ocr.getEcmFileVersion().getFile().getFileId() + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateOCRException(message, e);
        }
    }

    private Process runQPDF(OCR ocr, String source)
    {
        rt = Runtime.getRuntime();
        long timeStamp = System.currentTimeMillis();
        String destination = (System.getProperty("java.io.tmpdir") + ocr.getId() + OCRConstants.TEMP_FILE_PREFIX + timeStamp
                + OCRConstants.QPDF_TMP
                + OCRConstants.TEMP_FILE_PDF_SUFFIX);
        String command = buildCommand(OCRConstants.QPDF, ocr, source, destination);
        try
        {

            Process pr = rt.exec(command);
            getActivitiRuntimeService().setVariable(ocr.getProcessId(), OCRConstants.QPDF_TMP, destination);
            return pr;
        }
        catch (IOException e)
        {
            LOG.error("Error while running QPDF, for FILE_ID=[{}]. REASON=[{}]  ", ocr.getEcmFileVersion().getFile().getFileId(),
                    e.getMessage(), e);
        }
        return null;
    }

    private String buildCommand(String command, OCR ocr, String source, String destination)
    {
        StringJoiner joiner = new StringJoiner(" ");

        switch (command)
        {
        case OCRConstants.IMAGE_MAGICK:
            joiner.add(command)
                    .add("-trim")
                    .add("-density 300")
                    .add(source)
                    .add("-quality 100")
                    .add("-sharpen 0x1.0")
                    .add(destination);
            break;
        case OCRConstants.TESSERACT_COMMAND_PREFIX:
            joiner.add(command)
                    .add(source)
                    .add(destination)
                    .add("-l")
                    .add(ocr.getLanguage())
                    .add("PDF");
            break;
        case OCRConstants.QPDF:
            joiner.add(command)
                    .add(source)
                    .add("--linearize")
                    .add(destination);
            break;
        }
        return joiner.toString();
    }

    @Override
    public OCR get(String remoteId) throws GetOCRException
    {
        if (StringUtils.isNotEmpty(remoteId))
        {
            try
            {
                Process process = processes.get(remoteId).getProcess();
                if (process == null)
                {
                    return ocrDao.findByRemoteId(remoteId);

                }
                int resultStatus = process.exitValue();

                OCR ocr = new OCR();
                if (resultStatus == 0)
                {
                    ocr.setStatus(OCRStatusType.COMPLETED.toString());
                    ocr.setRemoteId(remoteId);
                }
                else
                {
                    String status = OCRStatusType.FAILED.toString();
                    ocr.setStatus(status);
                    ocr.setRemoteId(remoteId);
                }

                return ocr;
            }
            catch (Exception e)
            {
                throw new GetOCRException(String.format("Unable to get OCR job. REASON=[%s].", e.getMessage()), e);
            }
        }

        throw new GetOCRException("Unable to get OCR job. Remote ID not provided.");

    }

    @Override
    public List<OCR> getAll() throws GetOCRException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<OCR> getAllByStatus(String status) throws GetOCRException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<OCR> getPage(int start, int n) throws GetOCRException
    {
        throw new NotImplementedException();
    }

    @Override
    public List<OCR> getPageByStatus(int start, int n, String status) throws GetOCRException
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean purge(OCR ocr)
    {
        try
        {
            if (getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.UPLOADED_TMP) != null)
            {
                File uploadedTmp = new File(
                        (String) getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.UPLOADED_TMP));
                FileUtils.deleteQuietly(uploadedTmp);
            }
            if (getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.MAGICK_TMP) != null)
            {
                File magickTmp = new File((String) getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.MAGICK_TMP));
                FileUtils.deleteQuietly(magickTmp);
            }
            if (getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.TESSERACT_TMP) != null)
            {
                File tesseractTmp = new File(
                        (String) getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.TESSERACT_TMP));
                FileUtils.deleteQuietly(tesseractTmp);
            }
            if (getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.QPDF_TMP) != null)
            {
                File qpdfTmp = new File((String) getActivitiRuntimeService().getVariable(ocr.getProcessId(), OCRConstants.QPDF_TMP));
                FileUtils.deleteQuietly(qpdfTmp);
            }

            processes.remove(ocr.getRemoteId());

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Error while purging OCR temp files with REMOTE_ID=[{}]. REASON=[{}]",
                    ocr.getRemoteId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isEnabled()
    {
        try
        {
            return ocrConfigurationPropertiesService.get().isEnableOCR();
        }
        catch (GetConfigurationException e)
        {
            LOG.warn("Could not read ocr configuration. {} ", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getName()
    {
        return OCRConstants.TESSERACT_SERVICE;
    }

    public OCREventPublisher getOcrEventPublisher()
    {
        return ocrEventPublisher;
    }

    public void setOcrEventPublisher(OCREventPublisher ocrEventPublisher)
    {
        this.ocrEventPublisher = ocrEventPublisher;
    }

    public OCRConfigurationPropertiesService getOcrConfigurationPropertiesService()
    {
        return ocrConfigurationPropertiesService;
    }

    public void setOcrConfigurationPropertiesService(OCRConfigurationPropertiesService ocrConfigurationPropertiesService)
    {
        this.ocrConfigurationPropertiesService = ocrConfigurationPropertiesService;
    }

    public Map<String, ArkCaseProcess> getProcesses()
    {
        return processes;
    }

    public void setProcesses(Map<String, ArkCaseProcess> processes)
    {
        this.processes = processes;
    }

    public EcmFileTransaction getEcmFileTransaction()
    {
        return ecmFileTransaction;
    }

    public void setEcmFileTransaction(EcmFileTransaction ecmFileTransaction)
    {
        this.ecmFileTransaction = ecmFileTransaction;
    }

    public OCRDao getOcrDao()
    {
        return ocrDao;
    }

    public void setOcrDao(OCRDao ocrDao)
    {
        this.ocrDao = ocrDao;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }
}
