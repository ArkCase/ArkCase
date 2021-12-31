package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.exception.ArkCaseFileNameAlreadyExistsException;
import com.armedia.acm.camelcontext.exception.ArkCaseFileRepositoryException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmAallowedUploadFileTypesConfig;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConfig;
import com.armedia.acm.plugins.ecm.model.FileUploadStage;
import com.armedia.acm.plugins.ecm.model.ProgressbarDetails;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.ProgressIndicatorService;
import com.armedia.acm.plugins.ecm.service.ProgressbarExecutor;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransactionImpl implements EcmFileTransaction
{
    private CamelContextManager camelContextManager;
    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmTikaFileServiceImpl ecmTikaFileService;
    private FileEventPublisher fileEventPublisher;
    private PipelineManager<EcmFile, EcmFileTransactionPipelineContext> ecmFileUploadPipelineManager;
    private PipelineManager<EcmFile, EcmFileTransactionPipelineContext> ecmFileUpdatePipelineManager;
    private CmisConfigUtils cmisConfigUtils;
    private Logger log = LogManager.getLogger(getClass());
    private AcmAallowedUploadFileTypesConfig allowedUploadFileTypesConfig;
    private EcmFileConfig ecmFileConfig;
    private ProgressIndicatorService progressIndicatorService;
    private EcmFileService ecmFileService;

    public static List<String> getAllAllowedUploadFileTypes(Map<String, List<String>> allowedUploadFileTypes, String value)
    {
        if (value.contains(";"))
        {
            value = value.split(";")[0];
        }
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : allowedUploadFileTypes.entrySet())
        {
            if (entry.getValue().contains(value))
            {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    @Override
    @Deprecated
    public EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, InputStream fileContents, EcmFile metadata,
            Document existingCmisDocument) throws ArkCaseFileRepositoryException, IOException
    {

        log.debug("Creating ecm file pipeline context");

        File tempFileContents = null;
        try
        {
            tempFileContents = File.createTempFile("arkcase-upload-temp-file-", null);
            FileUtils.copyInputStreamToFile(fileContents, tempFileContents);

            EcmTikaFile detectedMetadata = null;

            try
            {
                detectedMetadata = extractFileMetadata(tempFileContents, metadata.getFileName());
            }
            catch (SAXException | TikaException e)
            {
                log.error("Could not extract metadata with Tika: [{}]", e.getMessage(), e);
            }

            String activeVersionMimeType = metadata.getFileActiveVersionMimeType();
            if (activeVersionMimeType == null && detectedMetadata != null)
            {
                activeVersionMimeType = detectedMetadata.getContentType();
            }

            if (activeVersionMimeType != null && activeVersionMimeType.contains(";"))
            {
                activeVersionMimeType = metadata.getFileActiveVersionMimeType().split(";")[0];
            }

            if (activeVersionMimeType != null && detectedMetadata != null
                    && isFileTypeUploadAllowed(detectedMetadata, activeVersionMimeType))
            {

                Pair<String, String> mimeTypeAndExtension = buildMimeTypeAndExtension(detectedMetadata, ecmUniqueFilename,
                        metadata.getFileActiveVersionMimeType());
                String finalMimeType = mimeTypeAndExtension.getLeft();
                String finalExtension = mimeTypeAndExtension.getRight();

                ecmUniqueFilename = getFolderAndFilesUtils().createUniqueIdentificator(ecmUniqueFilename);

                EcmFileTransactionPipelineContext pipelineContext = buildEcmFileTransactionPipelineContext(authentication,
                        tempFileContents, targetCmisFolderId, container, metadata.getFileName(), existingCmisDocument,
                        detectedMetadata, ecmUniqueFilename);

                try (InputStream is = FileUtils.openInputStream(tempFileContents))
                {
                    String fileHash = DigestUtils.md5Hex(is);
                    pipelineContext.setFileHash(fileHash);
                }

                boolean searchablePDF = false;
                if (ecmFileConfig.getSnowboundEnableOcr())
                {
                    searchablePDF = folderAndFilesUtils.isSearchablePDF(tempFileContents, finalMimeType);
                }
                pipelineContext.setSearchablePDF(searchablePDF);

                String fileName = getFolderAndFilesUtils().getBaseFileName(metadata.getFileName(), finalExtension);
                metadata.setFileName(fileName);
                metadata.setFileActiveVersionMimeType(finalMimeType);
                metadata.setFileActiveVersionNameExtension(finalExtension);

                try
                {
                    log.debug("Calling pipeline manager handlers");
                    getEcmFileUploadPipelineManager().executeOperation(metadata, pipelineContext, () -> metadata);
                }
                catch (Exception e)
                {
                    log.error("pipeline handler call failed: {}", e.getMessage(), e);
                    if (pipelineContext.isFileNameAlreadyInEcmSystem())
                    {
                        log.debug("File: {} already exists in ecm system", metadata.getFileName());
                        throw new ArkCaseFileNameAlreadyExistsException("fileName already exists");
                    }
                    if (e.getCause() != null && ArkCaseFileRepositoryException.class.isAssignableFrom(e.getCause().getClass()))
                    {
                        throw (ArkCaseFileRepositoryException) e.getCause();
                    }
                }
                log.debug("Returning from addFileTransaction method");
                return pipelineContext.getEcmFile();
            }
            else
            {
                log.error("Uploaded file with name [{}] - MIME type [{}] is not compatible with advertised type [{}]",
                        metadata.getFileName(), metadata.getFileType(), metadata.getFileActiveVersionMimeType());
                throw new IOException("Uploaded file's " + metadata.getFileName() + " MIME type " + metadata.getFileActiveVersionMimeType()
                        + " is not compatible. " + metadata.getFileType());
            }
        }
        finally
        {
            FileUtils.deleteQuietly(tempFileContents);
        }
    }

    private boolean isFileTypeUploadAllowed(EcmTikaFile detectedMetadata, String activeVersionMimeType)
    {
        List<String> allAllowedUploadFileTypes = getAllAllowedUploadFileTypes(allowedUploadFileTypesConfig.getAllowedUploadFileTypes(),
                activeVersionMimeType);
        return !getAllowedUploadFileTypesConfig().getRestrictFileTypesUpload()
                || detectedMetadata.getContentType().equals(activeVersionMimeType)
                || allAllowedUploadFileTypes.contains(detectedMetadata.getContentType().replaceAll("\\.", "__"));
    }

    @Override
    @Deprecated
    public EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, InputStream fileContents, EcmFile metadata) throws ArkCaseFileRepositoryException, IOException
    {
        Document existingCmisDocument = null;
        return addFileTransaction(authentication, ecmUniqueFilename, container, targetCmisFolderId, fileContents,
                metadata, existingCmisDocument);

    }

    @Override
    @Deprecated
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType,
            String fileCategory, InputStream fileInputStream, String mimeType, String fileName,
            String cmisFolderId, AcmContainer container, String cmisRepositoryId)
            throws ArkCaseFileRepositoryException, IOException
    {
        Document existingCmisDocument = null;
        return addFileTransaction(originalFileName, authentication, fileType, fileCategory, fileInputStream,
                mimeType, fileName, cmisFolderId, container, cmisRepositoryId, existingCmisDocument);
    }

    @Override
    @Deprecated
    public EcmFile addFileTransaction(String originalFileName, Authentication authentication, String fileType,
            String fileCategory, InputStream fileContents, String fileContentType,
            String fileName, String targetCmisFolderId, AcmContainer container,
            String cmisRepositoryId, Document existingCmisDocument) throws ArkCaseFileRepositoryException, IOException
    {

        log.debug("Creating ecm file pipeline context");

        EcmFile metadata = new EcmFile();
        metadata.setFileActiveVersionMimeType(fileContentType);
        metadata.setFileType(fileType);
        metadata.setFileName(fileName);
        metadata.setCategory(fileCategory);
        metadata.setCmisRepositoryId(cmisRepositoryId);

        return addFileTransaction(authentication, originalFileName, container, targetCmisFolderId, fileContents,
                metadata, existingCmisDocument);

    }

    @Override
    public EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, EcmFile metadata, Document existingCmisDocument, MultipartFile file)
            throws ArkCaseFileRepositoryException, IOException
    {

        log.debug("Creating ecm file pipeline context");

        File tempFileContents = null;
        try
        {
            log.debug("Putting fileInputStream in a decorator stream so that the number of bytes can be counted");
            CountingInputStream countingInputStream = new CountingInputStream(file.getInputStream());
            // this reports progress on file system. Also should store info for the broker, for which part of the
            // progress it is loading for the filesystem or the activity upload from 50% to 59%
            if (StringUtils.isNotEmpty(metadata.getUuid()))
            {
                ProgressbarDetails progressbarDetails = new ProgressbarDetails();
                progressbarDetails.setProgressbar(true);
                progressbarDetails.setStage(2);
                progressbarDetails.setUuid(metadata.getUuid());
                progressbarDetails.setObjectId(container.getContainerObjectId());
                progressbarDetails.setObjectType(container.getContainerObjectType());
                progressbarDetails.setFileName(metadata.getFileName());
                progressbarDetails.setObjectNumber(container.getContainerObjectTitle());
                log.debug("Start stage two for file {}. The file will be written to file system", metadata.getFileName());
                progressIndicatorService.start(countingInputStream, file.getSize(), container.getContainerObjectId(),
                        container.getContainerObjectType(), file.getOriginalFilename(), authentication.getName(), progressbarDetails);
            }
            tempFileContents = File.createTempFile("arkcase-upload-temp-file-", null);
            FileUtils.copyInputStreamToFile(countingInputStream, tempFileContents);

            // start progress
            EcmTikaFile detectedMetadata = null;

            try
            {
                detectedMetadata = extractFileMetadata(tempFileContents, metadata.getFileName());
            }
            catch (SAXException | TikaException e)
            {
                log.error("Could not extract metadata with Tika: [{}]", e.getMessage(), e);
            }

            String activeVersionMimeType = metadata.getFileActiveVersionMimeType();
            if (activeVersionMimeType == null && detectedMetadata != null)
            {
                activeVersionMimeType = detectedMetadata.getContentType();
            }

            if (activeVersionMimeType != null && activeVersionMimeType.contains(";"))
            {
                activeVersionMimeType = metadata.getFileActiveVersionMimeType().split(";")[0];
            }
            if (detectedMetadata.getContentType() != null && detectedMetadata.getContentType().contains(";"))
            {
                detectedMetadata.setContentType(detectedMetadata.getContentType().split(";")[0]);
            }
            if (detectedMetadata.getContentType() == null && file.getContentType() != null)
            {
                detectedMetadata.setContentType(file.getContentType());
            }
            if (activeVersionMimeType != null && detectedMetadata != null
                    && isFileTypeUploadAllowed(detectedMetadata, activeVersionMimeType))
            {

                Pair<String, String> mimeTypeAndExtension = buildMimeTypeAndExtension(detectedMetadata, ecmUniqueFilename,
                        metadata.getFileActiveVersionMimeType());
                String finalMimeType = mimeTypeAndExtension.getLeft();
                String finalExtension = mimeTypeAndExtension.getRight();

                ecmUniqueFilename = getFolderAndFilesUtils().createUniqueIdentificator(ecmUniqueFilename);

                EcmFileTransactionPipelineContext pipelineContext = buildEcmFileTransactionPipelineContext(authentication,
                        tempFileContents, targetCmisFolderId, container, metadata.getFileName(), existingCmisDocument,
                        detectedMetadata, ecmUniqueFilename);

                try (InputStream is = FileUtils.openInputStream(tempFileContents))
                {
                    String fileHash = DigestUtils.md5Hex(is);
                    pipelineContext.setFileHash(fileHash);
                }

                boolean searchablePDF = false;
                log.debug("SNOWBOUND ENABLED OCR = [{}]", ecmFileConfig.getSnowboundEnableOcr());
                if (ecmFileConfig.getSnowboundEnableOcr())
                {
                    searchablePDF = folderAndFilesUtils.isSearchablePDF(tempFileContents, finalMimeType);
                    log.debug("SearchablePDF = [{}]", searchablePDF);
                }
                pipelineContext.setSearchablePDF(searchablePDF);

                String fileName = getFolderAndFilesUtils().getBaseFileName(metadata.getFileName(), finalExtension);
                metadata.setFileName(fileName);
                metadata.setFileActiveVersionMimeType(finalMimeType);
                metadata.setFileActiveVersionNameExtension(finalExtension);

                // stop the progressbar executor
                if (StringUtils.isNotEmpty(metadata.getUuid()))
                {
                    log.debug("Stop progressbar executor in stage 2, for file {} and set file upload success to {}", metadata.getUuid(),
                            false);
                    progressIndicatorService.end(metadata.getUuid(), true);
                }

                try
                {
                    log.debug("Calling pipeline manager handlers");
                    getEcmFileUploadPipelineManager().executeOperation(metadata, pipelineContext, () -> metadata);
                }
                catch (Exception e)
                {
                    log.error("pipeline handler call failed: {}", e.getMessage(), e);
                    if (pipelineContext.isFileNameAlreadyInEcmSystem())
                    {
                        stopProgressBar(metadata);
                        log.debug("File: {} already exists in ecm system", metadata.getFileName());
                        throw new ArkCaseFileNameAlreadyExistsException("fileName already exists");
                    }
                }
                log.debug("Returning from addFileTransaction method");
                return pipelineContext.getEcmFile();
            }
            else
            {
                stopProgressBar(metadata);
                log.error("Uploaded file with name [{}] - MIME type [{}] is not compatible with advertised type [{}]",
                        metadata.getFileName(), metadata.getFileType(), metadata.getFileActiveVersionMimeType());
                throw new IOException("Uploaded file's " + metadata.getFileName() + " MIME type " + metadata.getFileActiveVersionMimeType()
                        + " is not compatible. " + metadata.getFileType());
            }
        }
        finally
        {
            FileUtils.deleteQuietly(tempFileContents);
        }
    }

    private void stopProgressBar(EcmFile metadata)
    {
        // stop the progressbar executor
        ProgressbarExecutor progressbarExecutor = progressIndicatorService.getExecutor(metadata.getUuid());
        if (StringUtils.isNotEmpty(metadata.getUuid()) && progressbarExecutor != null
                && progressbarExecutor.getProgressbarDetails().getStage() == FileUploadStage.UPLOAD_CHUNKS_TO_FILESYSTEM.getValue())
        {
            log.debug("Stop progressbar executor in stage 2, for file {} and set file upload success to {}", metadata.getUuid(), false);
            progressIndicatorService.end(metadata.getUuid(), false);
        }
    }

    @Override
    public EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, EcmFile metadata, MultipartFile file) throws ArkCaseFileRepositoryException, IOException
    {
        Document existingCmisDocument = null;
        return addFileTransaction(authentication, ecmUniqueFilename, container, targetCmisFolderId,
                metadata, existingCmisDocument, file);
    }

    @Deprecated
    /**
     * @deprecated use extractFileMetadata(File, String)
     */
    protected EcmTikaFile extractFileMetadata(byte[] fileByteArray, String fileName) throws IOException,
            SAXException, TikaException
    {
        File file = null;
        try
        {
            file = File.createTempFile("arkcase-extract-file-metadata-", null);
            FileUtils.writeByteArrayToFile(file, fileByteArray);
            return extractFileMetadata(file, fileName);
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }
    }

    protected EcmTikaFile extractFileMetadata(File file, String fileName) throws IOException,
            SAXException, TikaException
    {
        EcmTikaFile retval = getEcmTikaFileService().detectFileUsingTika(file, fileName);
        return retval;
    }

    protected Pair<String, String> buildMimeTypeAndExtension(EcmTikaFile detectedFileMetadata, String filename, String mimeType)
    {
        String finalMimeType = detectedFileMetadata == null ? mimeType : detectedFileMetadata.getContentType();
        String finalExtension = (detectedFileMetadata == null || StringUtils.isEmpty(detectedFileMetadata.getNameExtension()))
                ? getFolderAndFilesUtils().getFileNameExtension(filename)
                : detectedFileMetadata.getNameExtension();

        // do not change content type in case of freevo
        if (mimeType != null && mimeType.contains("frevvo"))
        {
            finalMimeType = mimeType;
        }

        return Pair.of(finalMimeType, finalExtension);
    }

    /**
     * @param filename
     *            The extension in this file name is used as default, in case file type detection fails
     * @param mimeType
     *            MIME type to be used as default in case MIME type detection fails
     * @param fileByteArray
     *            File contents
     * @return Detected MIME type and extension for the fileByteArray, or the supplied default values if detection
     *         fails.
     * @deprecated Call buildMimeTypeAndExtension(EcmTikaFile, filename, mimeType) instead.
     */
    @Deprecated
    protected Pair<String, String> buildMimeTypeAndExtension(String filename, String mimeType, byte[] fileByteArray)
    {

        String finalMimeType;
        String finalExtension;
        File file = null;

        try
        {
            file = File.createTempFile("arkcase-build-mime-type-and-extension-", null);
            FileUtils.writeByteArrayToFile(file, fileByteArray);
            EcmTikaFile ecmTikaFile = getEcmTikaFileService().detectFileUsingTika(file, filename);
            finalMimeType = ecmTikaFile.getContentType();
            finalExtension = ecmTikaFile.getNameExtension();
        }
        catch (IOException | SAXException | TikaException e1)
        {
            finalMimeType = mimeType;
            finalExtension = getFolderAndFilesUtils().getFileNameExtension(filename);
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }

        // do not change content type in case of freevo
        if (mimeType != null && mimeType.contains("frevvo"))
        {
            finalMimeType = mimeType;
        }

        return Pair.of(finalMimeType, finalExtension);
    }

    @Deprecated
    protected EcmFileTransactionPipelineContext buildEcmFileTransactionPipelineContext(Authentication authentication,
            byte[] fileBytes,
            String cmisFolderId,
            AcmContainer container,
            String filename,
            Document existingCmisDocument,
            EcmTikaFile detectedFileMetadata,
            String ecmUniqueFilename,
            Object... otherArgs) throws IOException
    {
        // don't delete this file at the end of this method, since it is part of the returned
        // EcmFileTransactionPipelineContext
        File file = File.createTempFile("arkcase-build-ecm-file-transaction-pipeline-context-", null);
        FileUtils.writeByteArrayToFile(file, fileBytes);
        return buildEcmFileTransactionPipelineContext(authentication, file, cmisFolderId, container, filename,
                existingCmisDocument, detectedFileMetadata, ecmUniqueFilename, otherArgs);
    }

    protected EcmFileTransactionPipelineContext buildEcmFileTransactionPipelineContext(Authentication authentication,
            File fileContents,
            String cmisFolderId,
            AcmContainer container,
            String filename,
            Document existingCmisDocument,
            EcmTikaFile detectedFileMetadata,
            String ecmUniqueFilename,
            Object... otherArgs) throws IOException
    {

        EcmFileTransactionPipelineContext pipelineContext = new EcmFileTransactionPipelineContext();
        pipelineContext.setCmisFolderId(cmisFolderId);
        pipelineContext.setFileContents(fileContents);
        pipelineContext.setContainer(container);
        pipelineContext.setAuthentication(authentication);
        pipelineContext.setOriginalFileName(filename);
        pipelineContext.setCmisDocument(existingCmisDocument);
        pipelineContext.setDetectedFileMetadata(detectedFileMetadata);
        pipelineContext.setOriginalFileName(ecmUniqueFilename);
        return pipelineContext;
    }

    @Override
    public EcmFile updateFileTransaction(Authentication authentication, final EcmFile ecmFile, InputStream fileInputStream)
            throws IOException
    {
        return updateFileTransaction(authentication, ecmFile, fileInputStream, null);
    }

    @Override
    public EcmFile updateFileTransaction(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream, String fileExtension)
            throws IOException
    {
        File file = null;
        try
        {

            file = File.createTempFile("arkcase-update-file-transaction-", null);
            FileUtils.copyInputStreamToFile(fileInputStream, file);
            EcmFileTransactionPipelineContext pipelineContext = getEcmFileTransactionPipelineContext(authentication, ecmFile, file,
                    fileExtension);

            try
            {
                log.debug("Calling pipeline manager handlers");
                return getEcmFileUpdatePipelineManager().executeOperation(ecmFile, pipelineContext, () -> pipelineContext.getEcmFile());
            }
            catch (Exception e)
            {
                log.error("pipeline handler call failed: {}", e.getMessage(), e);
            }
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }

        return ecmFile;
    }

    @Override
    public EcmFile updateFileTransaction(Authentication authentication, EcmFile ecmFile, File file, String fileExtension)
            throws ArkCaseFileRepositoryException, IOException
    {

        EcmFileTransactionPipelineContext pipelineContext = getEcmFileTransactionPipelineContext(authentication, ecmFile, file,
                fileExtension);

        try
        {
            log.debug("Calling pipeline manager handlers");
            return getEcmFileUpdatePipelineManager().executeOperation(ecmFile, pipelineContext, () -> pipelineContext.getEcmFile());
        }
        catch (Exception e)
        {
            log.error("pipeline handler call failed: {}", e.getMessage(), e);
            throw new ArkCaseFileRepositoryException(e);
        }

    }

    private EcmFileTransactionPipelineContext getEcmFileTransactionPipelineContext(Authentication authentication, EcmFile ecmFile,
            File file, String fileExtension) throws IOException
    {
        if (ecmFile.getFileActiveVersionNameExtension() != null
                && ecmFile.getFileName().endsWith(ecmFile.getFileActiveVersionNameExtension()))
        {
            ecmFile.setFileName(getFolderAndFilesUtils().getBaseFileName(ecmFile.getFileName()));
        }

        log.debug("Creating ecm file pipeline context");
        EcmFileTransactionPipelineContext pipelineContext = new EcmFileTransactionPipelineContext();

        pipelineContext.setAuthentication(authentication);
        pipelineContext.setEcmFile(ecmFile);
        pipelineContext.setFileContents(file);

        try (InputStream is = FileUtils.openInputStream(file))
        {
            String fileHash = DigestUtils.md5Hex(is);
            pipelineContext.setFileHash(fileHash);
        }

        EcmTikaFile ecmTikaFile = new EcmTikaFile();

        try
        {
            if (Objects.isNull(fileExtension))
            {
                ecmTikaFile = getEcmTikaFileService().detectFileUsingTika(file, ecmFile.getFileName());
            }
            else
            {
                String fileName = ecmFile.getFileName() != null && ecmFile.getFileName().endsWith(ecmFile.getFileExtension())
                        ? ecmFile.getFileName()
                        : ecmFile.getFileName() + "." + fileExtension;

                ecmTikaFile = getEcmTikaFileService().detectFileUsingTika(file, fileName);
            }
        }
        catch (TikaException | SAXException | IOException e1)
        {
            log.debug("Can not auto detect file using Tika");
            ecmTikaFile.setContentType(ecmFile.getFileActiveVersionMimeType());
            ecmTikaFile.setNameExtension(getFolderAndFilesUtils().getFileNameExtension(ecmFile.getFileName()));
        }

        pipelineContext.setDetectedFileMetadata(ecmTikaFile);

        if (!ecmFile.getFileActiveVersionMimeType().contains("frevvo"))
        {
            ecmFile.setFileActiveVersionMimeType(ecmTikaFile.getContentType());
        }
        ecmFile.setFileActiveVersionNameExtension(ecmTikaFile.getNameExtension());

        boolean searchablePDF = false;
        if (ecmFileConfig.getSnowboundEnableOcr())
        {
            searchablePDF = folderAndFilesUtils.isSearchablePDF(file, ecmFile.getFileActiveVersionMimeType());
        }
        pipelineContext.setSearchablePDF(searchablePDF);
        return pipelineContext;
    }

    @Override
    @Deprecated
    public EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream)
            throws IOException
    {

        return updateFileTransactionEventAware(authentication, ecmFile, fileInputStream, null);
    }

    @Override
    @Deprecated
    public EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream,
            String fileExtension) throws IOException
    {

        ecmFile = updateFileTransaction(authentication, ecmFile, fileInputStream, fileExtension);
        return publishUpdateFileTransactionEvent(authentication, ecmFile);
    }

    @Override
    public EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, File file, String fileExtension)
            throws ArkCaseFileRepositoryException, IOException
    {
        ecmFile = updateFileTransaction(authentication, ecmFile, file, fileExtension);
        return publishUpdateFileTransactionEvent(authentication, ecmFile);
    }

    @Override
    public EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, File file)
            throws ArkCaseFileRepositoryException, IOException
    {
        ecmFile = updateFileTransaction(authentication, ecmFile, file, null);
        return publishUpdateFileTransactionEvent(authentication, ecmFile);
    }

    private EcmFile publishUpdateFileTransactionEvent(Authentication authentication, EcmFile ecmFile)
    {
        String ipAddress = null;
        if (authentication != null)
        {
            if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
            {
                ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
            }
        }

        getFileEventPublisher().publishFileActiveVersionSetEvent(ecmFile, authentication, ipAddress, true);
        getFileEventPublisher().publishFileUpdatedEvent(ecmFile, authentication, true);

        return ecmFile;
    }

    @Override
    public String downloadFileTransaction(EcmFile ecmFile) throws ArkCaseFileRepositoryException
    {
        try
        {
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
            messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
            messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, ecmFile.getVersionSeriesId());

            ContentStream resultStream = (ContentStream) getCamelContextManager().send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProps);

            String result = getContent(resultStream);

            return result;
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Cannot download file: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile) throws ArkCaseFileRepositoryException
    {
        return performDownloadFileTransactionAsInputStream(ecmFile, new String());
    }

    @Override
    public InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile, String fileVersion) throws ArkCaseFileRepositoryException
    {
        return performDownloadFileTransactionAsInputStream(ecmFile, fileVersion);
    }

    private InputStream performDownloadFileTransactionAsInputStream(EcmFile ecmFile, String fileVersion)
            throws ArkCaseFileRepositoryException
    {
        try
        {
            String alfrescoUser = EcmFileCamelUtils.getCmisUser();

            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

            String cmisId = fileVersion.isEmpty() ? ecmFile.getVersionSeriesId()
                    : getFolderAndFilesUtils().getVersionCmisId(ecmFile, fileVersion);

            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
            messageProps.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
            messageProps.put(CamelCMISConstants.CMIS_OBJECT_ID, cmisId);

            ContentStream resultStream = (ContentStream) getCamelContextManager().send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProps);

            InputStream result = resultStream.getStream();

            return result;
        }
        catch (ArkCaseFileRepositoryException e)
        {
            log.error("Cannot download file: " + e.getMessage(), e);
            throw e;
        }
    }

    private String getContent(ContentStream contentStream)
    {
        String content = "";
        InputStream inputStream = null;

        try
        {
            inputStream = contentStream.getStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            content = writer.toString();
        }
        catch (IOException e)
        {
            log.error("Could not copy input stream to the writer: " + e.getMessage(), e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    log.error("Could not close CMIS content stream: " + e.getMessage(), e);
                }
            }
        }

        return content;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public FileEventPublisher getFileEventPublisher()
    {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher)
    {
        this.fileEventPublisher = fileEventPublisher;
    }

    public PipelineManager<EcmFile, EcmFileTransactionPipelineContext> getEcmFileUploadPipelineManager()
    {
        return ecmFileUploadPipelineManager;
    }

    public void setEcmFileUploadPipelineManager(PipelineManager<EcmFile, EcmFileTransactionPipelineContext> ecmFileUploadPipelineManager)
    {
        this.ecmFileUploadPipelineManager = ecmFileUploadPipelineManager;
    }

    public PipelineManager<EcmFile, EcmFileTransactionPipelineContext> getEcmFileUpdatePipelineManager()
    {
        return ecmFileUpdatePipelineManager;
    }

    public void setEcmFileUpdatePipelineManager(PipelineManager<EcmFile, EcmFileTransactionPipelineContext> ecmFileUpdatePipelineManager)
    {
        this.ecmFileUpdatePipelineManager = ecmFileUpdatePipelineManager;
    }

    public EcmTikaFileServiceImpl getEcmTikaFileService()
    {
        return ecmTikaFileService;
    }

    public void setEcmTikaFileService(EcmTikaFileServiceImpl ecmTikaFileService)
    {
        this.ecmTikaFileService = ecmTikaFileService;
    }

    public CmisConfigUtils getCmisConfigUtils()
    {
        return cmisConfigUtils;
    }

    public void setCmisConfigUtils(CmisConfigUtils cmisConfigUtils)
    {
        this.cmisConfigUtils = cmisConfigUtils;
    }

    public AcmAallowedUploadFileTypesConfig getAllowedUploadFileTypesConfig()
    {
        return allowedUploadFileTypesConfig;
    }

    public void setAllowedUploadFileTypesConfig(AcmAallowedUploadFileTypesConfig allowedUploadFileTypesConfig)
    {
        this.allowedUploadFileTypesConfig = allowedUploadFileTypesConfig;
    }

    public ProgressIndicatorService getProgressIndicatorService()
    {
        return progressIndicatorService;
    }

    public void setProgressIndicatorService(ProgressIndicatorService progressIndicatorService)
    {
        this.progressIndicatorService = progressIndicatorService;
    }

    public EcmFileConfig getEcmFileConfig()
    {
        return ecmFileConfig;
    }

    public void setEcmFileConfig(EcmFileConfig ecmFileConfig)
    {
        this.ecmFileConfig = ecmFileConfig;
    }

    public CamelContextManager getCamelContextManager()
    {
        return camelContextManager;
    }

    public void setCamelContextManager(CamelContextManager camelContextManager)
    {
        this.camelContextManager = camelContextManager;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}
