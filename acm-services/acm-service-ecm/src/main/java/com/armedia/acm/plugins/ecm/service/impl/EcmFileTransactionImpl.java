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
import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.pipeline.EcmFileTransactionPipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileTransaction;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.exception.TikaException;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by armdev on 4/22/14.
 */
public class EcmFileTransactionImpl implements EcmFileTransaction
{
    private MuleContextManager muleContextManager;
    private EcmFileDao ecmFileDao;
    private AcmFolderDao folderDao;
    private FolderAndFilesUtils folderAndFilesUtils;
    private EcmTikaFileServiceImpl ecmTikaFileService;
    private FileEventPublisher fileEventPublisher;
    private PipelineManager<EcmFile, EcmFileTransactionPipelineContext> ecmFileUploadPipelineManager;
    private PipelineManager<EcmFile, EcmFileTransactionPipelineContext> ecmFileUpdatePipelineManager;
    private CmisConfigUtils cmisConfigUtils;
    private Logger log = LoggerFactory.getLogger(getClass());
    private Map<String, List<String>> mimeTypesByTika;

    public static List<String> getAllTikaMimeTypesForFile(Map<String, List<String>> mimeTypesByTika, String value)
    {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : mimeTypesByTika.entrySet())
        {
            if(value.contains(";"))
            {
                value = value.split(";")[0];
            }
            if (entry.getValue().contains(value))
            {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    @Override
    public EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, InputStream fileContents, EcmFile metadata,
            Document existingCmisDocument) throws MuleException, IOException
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
            if(activeVersionMimeType.contains(";"))
            {
                activeVersionMimeType = metadata.getFileActiveVersionMimeType().split(";")[0];
            }

            if ((detectedMetadata.getContentType().equals(activeVersionMimeType)) ||
                    (getAllTikaMimeTypesForFile(mimeTypesByTika, activeVersionMimeType)
                            .contains(detectedMetadata.getContentType())))
            {

                Pair<String, String> mimeTypeAndExtension = buildMimeTypeAndExtension(detectedMetadata, ecmUniqueFilename,
                        metadata.getFileActiveVersionMimeType());
                String finalMimeType = mimeTypeAndExtension.getLeft();
                String finalExtension = mimeTypeAndExtension.getRight();

                ecmUniqueFilename = getFolderAndFilesUtils().getBaseFileName(ecmUniqueFilename, finalExtension);

                EcmFileTransactionPipelineContext pipelineContext = buildEcmFileTransactionPipelineContext(authentication,
                        tempFileContents, targetCmisFolderId, container, metadata.getFileName(), existingCmisDocument,
                        detectedMetadata, ecmUniqueFilename);

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
                    if (e.getCause() != null && MuleException.class.isAssignableFrom(e.getCause().getClass()))
                    {
                        throw (MuleException) e.getCause();
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

    @Override
    public EcmFile addFileTransaction(Authentication authentication, String ecmUniqueFilename, AcmContainer container,
            String targetCmisFolderId, InputStream fileContents, EcmFile metadata)
            throws MuleException, IOException
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
            throws MuleException, IOException
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
            String cmisRepositoryId, Document existingCmisDocument) throws MuleException, IOException
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
        File file = null;
        try
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
            file = File.createTempFile("arkcase-update-file-transaction-", null);
            FileUtils.copyInputStreamToFile(fileInputStream, file);
            pipelineContext.setFileContents(file);

            EcmTikaFile ecmTikaFile = new EcmTikaFile();

            try
            {
                ecmTikaFile = getEcmTikaFileService().detectFileUsingTika(file, ecmFile.getFileName());
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

        log.debug("Returning from updateFileTransaction method");
        return ecmFile;
    }

    @Override
    public EcmFile updateFileTransactionEventAware(Authentication authentication, EcmFile ecmFile, InputStream fileInputStream)
            throws IOException
    {

        ecmFile = updateFileTransaction(authentication, ecmFile, fileInputStream);
        String ipAddress = null;
        if (authentication != null)
        {
            if (authentication.getDetails() != null && authentication.getDetails() instanceof AcmAuthenticationDetails)
            {
                ipAddress = ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
            }
        }

        getFileEventPublisher().publishFileActiveVersionSetEvent(ecmFile, authentication, ipAddress, true);

        return ecmFile;
    }

    @Override
    public String downloadFileTransaction(EcmFile ecmFile) throws MuleException
    {
        try
        {
            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, cmisConfigUtils.getCmisConfiguration(ecmFile.getCmisRepositoryId()));
            MuleMessage message = getMuleContextManager().send("vm://downloadFileFlow.in", ecmFile.getVersionSeriesId(), messageProps);

            String result = getContent((ContentStream) message.getPayload());

            return result;
        }
        catch (MuleException e)
        {
            log.error("Cannot download file: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InputStream downloadFileTransactionAsInputStream(EcmFile ecmFile) throws MuleException
    {
        try
        {
            CMISCloudConnectorConnectionManager manager = cmisConfigUtils.getCmisConfiguration(ecmFile.getCmisRepositoryId());

            String alfrescoUser = manager.getUsername();
            Authentication authentication = SecurityContextHolder.getContext() != null
                    ? SecurityContextHolder.getContext().getAuthentication()
                    : null;

            if (authentication != null && authentication.getName() != null)
            {
                alfrescoUser = StringUtils.substringBeforeLast(authentication.getName(), "@");
            }

            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, alfrescoUser);
            MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

            Map<String, Object> messageProps = new HashMap<>();
            messageProps.put(EcmFileConstants.CONFIGURATION_REFERENCE, manager);
            MuleMessage message = getMuleContextManager().send("vm://downloadFileFlow.in", ecmFile.getVersionSeriesId(), messageProps);

            InputStream result = ((ContentStream) message.getPayload()).getStream();

            return result;
        }
        catch (MuleException e)
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

    public MuleContextManager getMuleContextManager()
    {
        return muleContextManager;
    }

    public void setMuleContextManager(MuleContextManager muleContextManager)
    {
        this.muleContextManager = muleContextManager;
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

    public Map<String, List<String>> getMimeTypesByTika() {
        return mimeTypesByTika;
    }

    public void setMimeTypesByTika(Map<String, List<String>> mimeTypesByTika) {
        this.mimeTypesByTika = mimeTypesByTika;
    }
}
