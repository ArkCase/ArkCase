package com.armedia.acm.tool.ocr.service;

/*-
 * #%L
 * acm-ocr-tool
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

import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.model.MediaEngineProcess;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import com.armedia.acm.tool.ocr.model.OCRConstants;
import com.armedia.acm.tool.ocr.model.OCRDTO;

import org.activiti.engine.RuntimeService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TesseractServiceImpl implements OCRIntegrationService
{

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private RuntimeService activitiRuntimeService;
    private Map<String, MediaEngineProcess> processes = new HashMap<>();
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;
    private Runtime rt;

    @Override
    public MediaEngineDTO create(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        String activeVersionMimeType = mediaEngineDTO.getProperties().get("activeVersionMimeType");
        String extension = mediaEngineDTO.getProperties().get("extension");
        String fileId = mediaEngineDTO.getProperties().get(OCRConstants.MAP_PROP_FILE_ID);

        File tmpFile = null;
        try
        {
            long timeStamp = System.currentTimeMillis();
            try (InputStream stream = mediaEngineDTO.getMediaEcmFileVersion())
            {
                tmpFile = File.createTempFile(mediaEngineDTO.getId() + OCRConstants.TEMP_FILE_PREFIX + timeStamp,
                        ("." + extension),
                        new File(System.getProperty(OCRConstants.TMP_DIR)));
                FileUtils.copyInputStreamToFile(stream, tmpFile);
                getActivitiRuntimeService().setVariable(mediaEngineDTO.getProcessId(), OCRConstants.UPLOADED_TMP,
                        tmpFile.getAbsolutePath());
            }
        }
        catch (IOException e)
        {
            getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, OCRConstants.OCR_SYSTEM_USER, null,
                    true, OCRConstants.SERVICE);
            throw new CreateMediaEngineToolException(String.format("Unable to create OCR-ed pdf, REASON=[%s].", e.getMessage()), e);
        }

        if (tmpFile != null)
        {
            String source = tmpFile.getAbsolutePath();

            if (activeVersionMimeType.equalsIgnoreCase(OCRConstants.MEDIA_TYPE_PDF_RECOGNITION_KEY))
            {
                runImageMagick(mediaEngineDTO, source);
                source = (String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.MAGICK_TMP);
            }
            try
            {
                runTesseract(mediaEngineDTO, source);
                source = (String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(),
                        OCRConstants.TESSERACT_TMP);
            }
            catch (Exception e)
            {
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, OCRConstants.OCR_SYSTEM_USER, null,
                        true, OCRConstants.SERVICE);
                String message = "Error while running Tesseract for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
                LOG.error(message, e);
                throw new CreateMediaEngineToolException(message, e);
            }

            Process pr = null;
            try
            {
                pr = runQPDF(mediaEngineDTO, source);
            }
            catch (Exception e)
            {
                LOG.error("Can't create linearized PDF for FILE_ID=[{}], REASON+[{}]", fileId, e.getMessage(), e);
            }
            getProcesses().put(mediaEngineDTO.getRemoteId(), new MediaEngineProcess(mediaEngineDTO.getRemoteId(), pr));
        }
        return mediaEngineDTO;
    }

    @Override
    public MediaEngineDTO get(String remoteId, String serviceName, String providerName) throws GetMediaEngineToolException
    {
        if (StringUtils.isNotEmpty(remoteId))
        {
            try
            {
                Process process = getProcesses().get(remoteId).getProcess();
                if (process == null)
                {
                    throw new GetMediaEngineToolException(
                            String.format("Unable to get OCR job. There is no process for OCR with REMOTE_ID=[%s].", remoteId));
                }
                int resultStatus = process.exitValue();

                MediaEngineDTO mediaEngineDTO = new OCRDTO();
                if (resultStatus == 0)
                {
                    mediaEngineDTO.setStatus("COMPLETED");
                    mediaEngineDTO.setRemoteId(remoteId);
                }
                else
                {
                    String status = "FAILED";
                    mediaEngineDTO.setStatus(status);
                    mediaEngineDTO.setRemoteId(remoteId);
                }

                return mediaEngineDTO;
            }
            catch (Exception e)
            {
                throw new GetMediaEngineToolException(String.format("Unable to get OCR job. REASON=[%s].", e.getMessage()), e);
            }
        }

        throw new GetMediaEngineToolException("Unable to get OCR job. Remote ID not provided.");
    }

    @Override
    public boolean purge(MediaEngineDTO mediaEngineDTO)
    {
        try
        {
            if (getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.UPLOADED_TMP) != null)
            {
                File uploadedTmp = new File(
                        (String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.UPLOADED_TMP));
                FileUtils.deleteQuietly(uploadedTmp);
            }
            if (getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.MAGICK_TMP) != null)
            {
                File magickTmp = new File(
                        (String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.MAGICK_TMP));
                FileUtils.deleteQuietly(magickTmp);
            }
            if (getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.TESSERACT_TMP) != null)
            {
                File tesseractTmp = new File(
                        (String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.TESSERACT_TMP));
                FileUtils.deleteQuietly(tesseractTmp);
            }
            if (getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.QPDF_TMP) != null)
            {
                File qpdfTmp = new File(
                        (String) getActivitiRuntimeService().getVariable(mediaEngineDTO.getProcessId(), OCRConstants.QPDF_TMP));
                FileUtils.deleteQuietly(qpdfTmp);
            }

            getProcesses().remove(mediaEngineDTO.getRemoteId());

            return true;
        }
        catch (Exception e)
        {
            LOG.error("Error while purging OCR temp files with REMOTE_ID=[{}]. REASON=[{}]",
                    mediaEngineDTO.getRemoteId(), e.getMessage());
            return false;
        }
    }

    private void runImageMagick(MediaEngineDTO mediaEngineDTO, String source) throws CreateMediaEngineToolException
    {
        rt = Runtime.getRuntime();
        long timeStamp = System.currentTimeMillis();
        String destination = System.getProperty(OCRConstants.TMP_DIR) + mediaEngineDTO.getId() + OCRConstants.TEMP_FILE_PREFIX + timeStamp
                + OCRConstants.MAGICK_TMP
                + OCRConstants.TEMP_FILE_PNG_SUFFIX;
        String command = buildCommand(OCRConstants.IMAGE_MAGICK, mediaEngineDTO, source, destination);
        try
        {
            Process pr = rt.exec(command);
            pr.waitFor();
            getActivitiRuntimeService().setVariable(mediaEngineDTO.getProcessId(), OCRConstants.MAGICK_TMP, destination);
        }
        catch (IOException | InterruptedException e)
        {
            String fileId = mediaEngineDTO.getProperties().get(OCRConstants.MAP_PROP_FILE_ID);
            String message = "Error while converting pdf to image with Image Magick for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateMediaEngineToolException(message, e);
        }
    }

    private void runTesseract(MediaEngineDTO mediaEngineDTO, String source) throws CreateMediaEngineToolException
    {
        rt = Runtime.getRuntime();
        long timeStamp = System.currentTimeMillis();
        String destination = System.getProperty(OCRConstants.TMP_DIR) + mediaEngineDTO.getId() + OCRConstants.TEMP_FILE_PREFIX
                + timeStamp
                + OCRConstants.TESSERACT_TMP;
        String command = buildCommand(OCRConstants.TESSERACT_COMMAND_PREFIX, mediaEngineDTO, source, destination);
        try
        {
            Process pr = rt.exec(command);
            pr.waitFor();
            getActivitiRuntimeService().setVariable(mediaEngineDTO.getProcessId(), OCRConstants.TESSERACT_TMP, (destination +
                    OCRConstants.TEMP_FILE_PDF_SUFFIX));
        }
        catch (InterruptedException | IOException e)
        {
            String fileId = mediaEngineDTO.getProperties().get(OCRConstants.MAP_PROP_FILE_ID);
            String message = "Error while trying to create OCR-ed pdf with Tesseract for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateMediaEngineToolException(message, e);
        }
    }

    private Process runQPDF(MediaEngineDTO mediaEngineDTO, String source) throws CreateMediaEngineToolException
    {
        rt = Runtime.getRuntime();
        long timeStamp = System.currentTimeMillis();
        String destination = (System.getProperty(OCRConstants.TMP_DIR) + mediaEngineDTO.getId() + OCRConstants.TEMP_FILE_PREFIX
                + timeStamp
                + OCRConstants.QPDF_TMP
                + OCRConstants.TEMP_FILE_PDF_SUFFIX);
        String command = buildCommand(OCRConstants.QPDF, mediaEngineDTO, source, destination);
        try
        {

            Process pr = rt.exec(command);
            getActivitiRuntimeService().setVariable(mediaEngineDTO.getProcessId(), OCRConstants.QPDF_TMP, destination);
            return pr;
        }
        catch (IOException e)
        {

            String fileId = mediaEngineDTO.getProperties().get(OCRConstants.MAP_PROP_FILE_ID);
            String message = "Error while trying to create OCR-ed pdf with Tesseract for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateMediaEngineToolException(message, e);
        }
    }

    private String buildCommand(String command, MediaEngineDTO mediaEngineDTO, String source, String destination)
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
                    .add(mediaEngineDTO.getLanguage())
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

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public Map<String, MediaEngineProcess> getProcesses()
    {
        return processes;
    }

    public void setProcesses(Map<String, MediaEngineProcess> processes)
    {
        this.processes = processes;
    }

    public MediaEngineIntegrationEventPublisher getMediaEngineIntegrationEventPublisher()
    {
        return mediaEngineIntegrationEventPublisher;
    }

    public void setMediaEngineIntegrationEventPublisher(MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher)
    {
        this.mediaEngineIntegrationEventPublisher = mediaEngineIntegrationEventPublisher;
    }
}
