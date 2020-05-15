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
 *WindowsEventLogger
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.tool.mediaengine.exception.CreateMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.exception.GetMediaEngineToolException;
import com.armedia.acm.tool.mediaengine.model.MediaEngineDTO;
import com.armedia.acm.tool.mediaengine.service.MediaEngineIntegrationEventPublisher;
import com.armedia.acm.tool.ocr.model.OCRDTO;
import com.armedia.acm.tool.ocr.model.OCRIntegrationConstants;
import org.apache.commons.exec.CommandLine;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.scheduling.annotation.Async;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TesseractServiceImpl implements OCRIntegrationService
{

    private final Logger LOG = LogManager.getLogger(getClass());
    private MediaEngineIntegrationEventPublisher mediaEngineIntegrationEventPublisher;

    @Override
    @Async
    public MediaEngineDTO create(MediaEngineDTO mediaEngineDTO) throws CreateMediaEngineToolException
    {
        String activeVersionMimeType = mediaEngineDTO.getProperties().get("activeVersionMimeType");
        String fileId = mediaEngineDTO.getProperties().get(OCRIntegrationConstants.MAP_PROP_FILE_ID);

        File tmpFile = mediaEngineDTO.getMediaEcmFileVersion();

        if (tmpFile != null)
        {
            String source = tmpFile.getAbsolutePath();

            if (activeVersionMimeType.equalsIgnoreCase(OCRIntegrationConstants.MEDIA_TYPE_PDF_RECOGNITION_KEY))
            {
                source = runImageMagick(mediaEngineDTO, source);
            }

            try
            {
                source = runTesseract(mediaEngineDTO, source);
            }
            catch (Exception e)
            {
                getMediaEngineIntegrationEventPublisher().publishFailedEvent(mediaEngineDTO, OCRIntegrationConstants.OCR_SYSTEM_USER, null,
                        true, OCRIntegrationConstants.SERVICE, "");
                String message = "Error while running Tesseract for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
                LOG.error(message, e);
                throw new CreateMediaEngineToolException(message, e);
            }

            Integer status = null;
            try
            {
                status = runQPDF(mediaEngineDTO, source);
            }
            catch (Exception e)
            {
                LOG.error("Can't create linearized PDF for FILE_ID=[{}], REASON=[{}]", fileId, e.getMessage(), e);
            }

            String destination = mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId() + ".tmp";
            File processStatusTemp = new File(destination);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(processStatusTemp)))
            {
                bw.write(status.toString());
            }
            catch (IOException e)
            {
                LOG.error("Unable to write Process Status to file. PROCESS_ID=[{}], FILE_ID=[{}], REASON=[{}]",
                        mediaEngineDTO.getProcessId(), fileId, e.getMessage(), e);
            }
        }

        return mediaEngineDTO;
    }

    private Integer readResultStatusFromFile(String remoteId, String tempPath) throws GetMediaEngineToolException
    {
        Integer resultStatus = null;

        File processStatusTemp = new File(tempPath + remoteId + ".tmp");
        try (Scanner s = new Scanner(processStatusTemp))
        {
            resultStatus = s.nextInt();
        }
        catch (FileNotFoundException e)
        {
            throw new GetMediaEngineToolException(
                    (String.format("Unable to read result status for OCR job. REMOTE_ID=[%s], REASON=[%s]", remoteId, e.getMessage())));
        }

        return resultStatus;
    }

    @Override
    public MediaEngineDTO get(String remoteId, Map<String, Object> props) throws GetMediaEngineToolException
    {
        if (StringUtils.isNotEmpty(remoteId))
        {
            try
            {
                MediaEngineDTO mediaEngineDTO = new OCRDTO();
                String tempPath = (String) props.get("tempPath");
                Integer processResultStatus = readResultStatusFromFile(remoteId, tempPath);
                //Integer processResultStatus = -1;

                if (processResultStatus == null)
                {
                    throw new GetMediaEngineToolException(String.format("Unable to get OCR job result status. REMOTE_ID=[%s]", remoteId));
                }

                if (processResultStatus == 0)
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
            Integer numberOfPages = Integer.valueOf(mediaEngineDTO.getProperties().get("numberOfPages"));
            if (numberOfPages > 1)
            {
                for (int i = 0; i < numberOfPages; i++)
                {
                    File magickTmp = new File(mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                            + OCRIntegrationConstants.MAGICK_TMP + "-" + i + OCRIntegrationConstants.TEMP_FILE_PNG_SUFFIX);
                    FileUtils.deleteQuietly(magickTmp);
                }
            }
            else
            {
                File magickTmp = new File(mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                        + OCRIntegrationConstants.MAGICK_TMP
                        + OCRIntegrationConstants.TEMP_FILE_PNG_SUFFIX);

                FileUtils.deleteQuietly(magickTmp);
            }

            File uploadedTmp = new File(
                    mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                            + "." + mediaEngineDTO.getProperties().get("extension"));

            File tesseractTmp = new File(mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                    + OCRIntegrationConstants.TESSERACT_TMP + OCRIntegrationConstants.TEMP_FILE_PDF_SUFFIX);

            File qpdfTmp = new File(mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                    + OCRIntegrationConstants.QPDF_TMP
                    + OCRIntegrationConstants.TEMP_FILE_PDF_SUFFIX);

            File processStatusTmp = new File(mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId() + ".tmp");

            File savedList = new File(
                    mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId() + "savedList.txt");

            FileUtils.deleteQuietly(uploadedTmp);
            FileUtils.deleteQuietly(tesseractTmp);
            FileUtils.deleteQuietly(qpdfTmp);
            FileUtils.deleteQuietly(processStatusTmp);
            FileUtils.deleteQuietly(savedList);
        }
        catch (Exception e)
        {
            LOG.error("Error while purging OCR temp files with REMOTE_ID=[{}]. REASON=[{}]",
                    mediaEngineDTO.getRemoteId(), e.getMessage());
            return false;
        }

        return true;
    }

    private String runImageMagick(MediaEngineDTO mediaEngineDTO, String source) throws CreateMediaEngineToolException
    {
        String destination = mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                + OCRIntegrationConstants.MAGICK_TMP;

        String command = buildCommand(OCRIntegrationConstants.IMAGE_MAGICK, mediaEngineDTO, source, destination);
        try
        {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            CommandLine commandToBeExecuted = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(new File(mediaEngineDTO.getTempPath()));
            executor.execute(commandToBeExecuted, resultHandler);
            resultHandler.waitFor();

            Integer numberOfPages = Integer.valueOf(mediaEngineDTO.getProperties().get("numberOfPages"));
            if (numberOfPages > 1)
            {
                File savedList = new File(
                        mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId() + "savedList.txt");
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(savedList)))
                {
                    for (int i = 0; i < numberOfPages; i++)
                    {
                        bw.write(destination + "-" + i + OCRIntegrationConstants.TEMP_FILE_PNG_SUFFIX);
                        bw.newLine();
                    }
                }
                return savedList.getAbsolutePath();
            }

            return destination + OCRIntegrationConstants.TEMP_FILE_PNG_SUFFIX;
        }
        catch (IOException | InterruptedException e)
        {
            String fileId = mediaEngineDTO.getProperties().get(OCRIntegrationConstants.MAP_PROP_FILE_ID);
            String message = "Error while converting pdf to image with Image Magick for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateMediaEngineToolException(message, e);
        }
    }

    private String runTesseract(MediaEngineDTO mediaEngineDTO, String source) throws CreateMediaEngineToolException
    {
        String destination = mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                + OCRIntegrationConstants.TESSERACT_TMP;
        String command = buildCommand(OCRIntegrationConstants.TESSERACT_COMMAND_PREFIX, mediaEngineDTO, source, destination);
        try
        {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            CommandLine commandToBeExecuted = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(new File(mediaEngineDTO.getTempPath()));
            executor.execute(commandToBeExecuted, resultHandler);
            resultHandler.waitFor();
            return destination + OCRIntegrationConstants.TEMP_FILE_PDF_SUFFIX;
        }
        catch (InterruptedException | IOException e)
        {
            String fileId = mediaEngineDTO.getProperties().get(OCRIntegrationConstants.MAP_PROP_FILE_ID);
            String message = "Error while trying to create OCR-ed pdf with Tesseract for FILE_ID=" + fileId + ". Reason: " + e.getMessage();
            LOG.error(message, e);
            throw new CreateMediaEngineToolException(message, e);
        }
    }

    private int runQPDF(MediaEngineDTO mediaEngineDTO, String source) throws CreateMediaEngineToolException
    {
        String destination = (mediaEngineDTO.getTempPath() + mediaEngineDTO.getRemoteId()
                + OCRIntegrationConstants.QPDF_TMP
                + OCRIntegrationConstants.TEMP_FILE_PDF_SUFFIX);
        String command = buildCommand(OCRIntegrationConstants.QPDF, mediaEngineDTO, source, destination);
        try
        {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            CommandLine commandToBeExecuted = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(new File(mediaEngineDTO.getTempPath()));
            executor.execute(commandToBeExecuted, resultHandler);
            resultHandler.waitFor();
            return resultHandler.getExitValue();
        }
        catch (IOException | InterruptedException e)
        {

            String fileId = mediaEngineDTO.getProperties().get(OCRIntegrationConstants.MAP_PROP_FILE_ID);
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
        case OCRIntegrationConstants.IMAGE_MAGICK:
            joiner.add(command)
                    .add("-trim")
                    .add("-density 300")
                    .add(source)
                    .add("-quality 100")
                    .add("-sharpen 0x1.0")
                    .add(destination + OCRIntegrationConstants.TEMP_FILE_PNG_SUFFIX);
            break;
        case OCRIntegrationConstants.TESSERACT_COMMAND_PREFIX:
            joiner.add(command)
                    .add(source)
                    .add(destination)
                    .add("-l")
                    .add(mediaEngineDTO.getLanguage())
                    .add("PDF");
            break;
        case OCRIntegrationConstants.QPDF:
            joiner.add(command)
                    .add(source)
                    .add("--linearize")
                    .add(destination);
            break;
        }
        return joiner.toString();
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
