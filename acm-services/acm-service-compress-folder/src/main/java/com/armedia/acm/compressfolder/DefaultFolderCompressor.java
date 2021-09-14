package com.armedia.acm.compressfolder;

/*-
 * #%L
 * ACM Service: Folder Compressing Service
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

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FILE_TYPE;
import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;
import static org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;

import com.armedia.acm.compressfolder.model.CompressNode;
import com.armedia.acm.compressfolder.model.CompressorServiceConfig;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderDownloadedEvent;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileDownloadedEvent;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.email.service.AcmMailTemplateConfigurationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.web.api.MDCConstants;

/**
 * Service for compressing folder to a zip file. The folder is recursively traversed and all its' contents is added to
 * the zip file. The contents is added to the current tmp directory as defined by <code>java.io.tmpdir</code> system
 * property. In case a size limit is set, and the output to the compressed file surpasses the limit, the compressing
 * operation stops and the resulting file is removed from the file system.
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 13, 2016
 *
 */
public class DefaultFolderCompressor implements FolderCompressor, ApplicationEventPublisherAware
{

    /**
     * The default max size of the compressed file expressed in default size unit.
     *
     * @see #DEFAULT_SIZE_UNIT
     */
    private static final long DEFAULT_MAX_SIZE = 2;

    /**
     * The default size unit used to calculate the max compressed size in bytes.
     */
    private static final SizeUnit DEFAULT_SIZE_UNIT = SizeUnit.GIGA;

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    /**
     * Used to retrieve folder information from the system.
     */
    private AcmFolderService folderService;

    /**
     * Used to retrieve file information from the system.
     */
    private EcmFileService fileService;

    private CompressorServiceConfig compressorServiceConfig;

    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * A formatting string that is used to generate the output file name. It takes 3 parameters, <code>tmpDir</code>,
     * <code>folderId</code> and <code>folderName</code>, for example <code>
     *      %1$sacm-%2$s.zip
     * </code>
     */
    private String compressedZipNameFormat = "%1$sacm-%2$s.zip";

    /**
     * Maximum size of the output file expressed in <code>sizeUnit</code>s.
     *
     * @see #sizeUnit
     */
    private long maxSize = DEFAULT_MAX_SIZE;

    /**
     * Size unit used to calculate the max compressed size in bytes.
     */
    private SizeUnit sizeUnit = DEFAULT_SIZE_UNIT;

    private static final String PROCESS_USER = "FILES_COMPRESSOR";

    private static final String CREATE_ZIP = "zip_completed";

    /**
     * Used for generating unique name
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy_M_d_k_m_s");

    private AcmFolderDao acmFolderDao;

    private AcmMailTemplateConfigurationService templateService;

    private NotificationDao notificationDao;

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private MessageChannel genericMessagesChannel;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.compressfolder.FolderCompressor#compressFolder(java.lang.Long)
     */
    @Override
    public String compressFolder(Long folderId) throws AcmFolderException
    {
        return compressFolder(folderId, maxSize, sizeUnit);
    }

    @Override
    public String compressFolder(CompressNode compressNode) throws AcmFolderException
    {
        return compressFolder(compressNode.getRootFolderId(), compressNode, maxSize, sizeUnit);
    }

    @Override
    @Async("fileCompressThreadPoolTaskExecutor")
    public String compressFolder(CompressNode compressNode, Authentication auth) throws AcmFolderException
    {
        String fileName = compressFolder(compressNode.getRootFolderId(), compressNode, maxSize, sizeUnit);
        send(fileName, auth);

        return fileName;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.compressfolder.FolderCompressor#compressFolder(java.lang.Long, long,
     * com.armedia.acm.compressfolder.SizeUnit)
     */
    @Override
    public String compressFolder(Long folderId, long size, SizeUnit sizeUnit) throws AcmFolderException
    {
        return compressFolder(folderId, null, size, sizeUnit);
    }

    @Override
    public String compressFolder(Long folderId, CompressNode compressNode, long size, SizeUnit sizeUnit)
            throws AcmFolderException
    {
        AcmFolder folder = Optional.ofNullable(folderService.findById(folderId)).orElseThrow(() -> new AcmFolderException(folderId));

        String filename = getCompressedFolderFilePath(folder);
        log.debug("ZIP creation: using [{}] as temporary file name", filename);
        File file = new File(filename);

        try (ZipOutputStream zos = new ZipOutputStream(new MaxThroughputAwareFileOutputStream(file, size, sizeUnit)))
        {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            compressFolder(zos, folder, "", compressNode);
        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException | IOException e)
        {
            FileUtils.deleteQuietly(file);
            throw new AcmFolderException(e);
        }

        try
        {
            publishCompressFolderDownloadEvents(folder, compressNode);
        }
        catch (AcmObjectNotFoundException | AcmUserActionFailedException e)
        {
            log.error("Compress folder publishing error.", e);
        }

        return filename;
    }

    @Override
    @Async("fileCompressThreadPoolTaskExecutor")
    public void compressFiles(List<Long> fileIds, Authentication auth)
    {
        String filePath = compressFiles(fileIds);
        send(filePath, auth);
    }

    @Override
    public String compressFiles(List<Long> fileIds)
    {

        String filePath = null;
        getAuditPropertyEntityAdapter().setUserId(PROCESS_USER);

        try
        {
            List<String> fileFolderList = new ArrayList<>();

            filePath = getCompressedZipPath();
            log.debug("ZIP creation: using [{}] as temporary file name", filePath);
            File file = new File(filePath);

            FileOutputStream fos = new FileOutputStream(file);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.setLevel(Deflater.BEST_COMPRESSION);

            List<EcmFile> filesForCompression = fileService.findByIds(fileIds);

            List<InputStream> filesContent = filesForCompression.parallelStream().map(fileForCompression -> {
                try
                {
                    InputStream fileByteStream = fileService.downloadAsInputStream(fileForCompression);

                    return fileByteStream;
                }
                catch (AcmUserActionFailedException e)
                {
                    log.error("Error while downloading stream for object with [{}] id.", file, e);
                }
                return null;

            }).collect(Collectors.toList());

            filesForCompression.forEach(fileForCompression -> {
                try
                {
                    String objectName = getUniqueObjectName(fileFolderList, DATE_FORMATTER, fileForCompression,
                            fileForCompression.getFileName());
                    fileFolderList.add(objectName);

                    String entryName = concatStrings(objectName +
                            fileForCompression.getFileActiveVersionNameExtension());

                    ZipEntry zipEntry = new ZipEntry(entryName);
                    zos.putNextEntry(zipEntry);

                    InputStream inputStream = filesContent.get(filesForCompression.indexOf(fileForCompression));
                    if (inputStream != null)
                    {
                        copy(inputStream, zos);
                    }
                }
                catch (IOException e)
                {
                    log.error("ZIP creation: Error while creating zip entry for object with [{}] id.", file, e);
                }
            });

            try
            {
                zos.close();
                fos.close();
            }
            catch (IOException e)
            {
                log.warn("Could not close CMIS content stream: {}", e.getMessage(), e);
            }

            filesForCompression.forEach(this::publishFileDownloadEvent);
        }
        catch (IOException e)
        {
            log.warn("Could not create zip file: {}", e.getMessage(), e);
        }

        return filePath;
    }

    private void send(String filePath, Authentication auth)
    {

        Map<String, Object> message = new HashMap<>();
        message.put("filePath", filePath);
        message.put("user", auth.getName());
        message.put("eventType", CREATE_ZIP);
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();

        genericMessagesChannel.send(progressMessage);

    }

    /**
     * Recursively traverses the folder and adds in contents to the instance of the <code>ZipOutputStream</code>
     * preserving the folder structure.
     *
     * @param zos
     *            the instance of <code>ZipOutputStream</code> that is used to compress the folder.
     * @param folder
     *            current folder being traversed.
     * @param parentPath
     *            path to the parent folder used to construct the zip folder structure in order for it to be
     *            identical as the structure of the folder that is being compressed.
     * @throws AcmUserActionFailedException
     *             can be thrown while querying for folder children.
     * @throws AcmObjectNotFoundException
     *             can be thrown while querying for folder children.
     *             can be thrown while retrieving the <code>InputStream</code> for a file.
     * @throws IOException
     *             can be thrown while writing to the output zip file.
     */
    private void compressFolder(ZipOutputStream zos, AcmFolder folder, String parentPath, CompressNode compressNode)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, IOException
    {
        List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId()).stream().filter(obj -> obj.getObjectType() != null)
                .collect(Collectors.toList());
        List<String> fileFolderList = new ArrayList<>();

        // all child objects of OBJECT_FILE_TYPE
        List<AcmObject> files = folderChildren.stream().filter(c -> OBJECT_FILE_TYPE.equalsIgnoreCase(c.getObjectType()))
                .collect(Collectors.toList());
        files.forEach(c -> {
            try
            {
                EcmFile file = EcmFile.class.cast(c);
                if (canBeCompressed(file, files, folder, compressNode))
                {

                    String objectName = getUniqueObjectName(fileFolderList, DATE_FORMATTER, c, file.getFileName());
                    fileFolderList.add(objectName);

                    String entryName = concatStrings(parentPath, objectName + file.getFileActiveVersionNameExtension());
                    zos.putNextEntry(new ZipEntry(entryName));
                    InputStream fileByteStream = fileService.downloadAsInputStream(c.getId());
                    copy(fileByteStream, zos);
                }
                zos.closeEntry();
            }
            catch (IOException e)
            {
                log.warn("ZIP creation: Error while creating zip entry for object with [{}] id of [{}] type.", c.getId(),
                        c.getObjectType(),
                        e);
            }
            catch (AcmUserActionFailedException e)
            {
                log.warn("Error while downloading stream for object with [{}] id of [{}] type.", c.getId(), c.getObjectType(), e);
            }
        });

        // all child objects of OBJECT_FOLDER_TYPE
        List<AcmObject> folders = folderChildren.stream().filter(c -> OBJECT_FOLDER_TYPE.equalsIgnoreCase(c.getObjectType()))
                .collect(Collectors.toList());
        folders.forEach(c -> {
            try
            {
                AcmFolder childFolder = AcmFolder.class.cast(c);

                String objectName = getUniqueObjectName(fileFolderList, DATE_FORMATTER, c, childFolder.getName());
                String entryName = concatStrings(parentPath, objectName, "/");
                if (isFolderRequestedToBeCompressed(compressNode, childFolder))
                {
                    fileFolderList.add(objectName);
                    zos.putNextEntry(new ZipEntry(entryName));
                    zos.closeEntry();
                }
                compressFolder(zos, childFolder, entryName, compressNode);
            }
            catch (IOException e)
            {
                log.error("ZIP creation: Error while creating zip entry for object with [{}] id of [{}] type.", c.getId(),
                        c.getObjectType(),
                        e);
            }
            catch (AcmUserActionFailedException | AcmObjectNotFoundException e)
            {
                log.error("Error while downloading stream for object with [{}] id of [{}] type.", c.getId(), c.getObjectType(), e);
            }
        });

    }

    public void publishCompressFolderDownloadEvents(AcmFolder folder, CompressNode compressNode)
            throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        if (isFolderRequestedToBeCompressed(compressNode, folder)
                && (Objects.isNull(folder.getParentFolder()) || !isFolderRequestedToBeCompressed(compressNode, folder.getParentFolder())))
        {
            publishFolderDownloadEvent(folder);
        }
        else
        {
            List<AcmObject> folderChildren = folderService.getFolderChildren(folder.getId());

            // all child objects of OBJECT_FILE_TYPE
            List<AcmObject> files = folderChildren.stream().filter(c -> OBJECT_FILE_TYPE.equalsIgnoreCase(c.getObjectType()))
                    .collect(Collectors.toList());
            for (AcmObject acmObject : files)
            {
                EcmFile file = EcmFile.class.cast(acmObject);
                if (canBeCompressed(file, files, folder, compressNode))
                {
                    publishFileDownloadEvent(file);
                }
            }

            // all child objects of OBJECT_FOLDER_TYPE
            List<AcmObject> folders = folderChildren.stream().filter(c -> OBJECT_FOLDER_TYPE.equalsIgnoreCase(c.getObjectType()))
                    .collect(Collectors.toList());
            for (AcmObject acmObject : folders)
            {
                AcmFolder childFolder = AcmFolder.class.cast(acmObject);
                publishCompressFolderDownloadEvents(childFolder, compressNode);
            }
        }
    }

    public void publishFileDownloadEvent(EcmFile file)
    {
        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        String ipAddress = MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY);

        EcmFileDownloadedEvent event = new EcmFileDownloadedEvent(file);
        event.setIpAddress(ipAddress);
        event.setUserId(userId);
        event.setSucceeded(true);
        getApplicationEventPublisher().publishEvent(event);
    }

    public void publishFolderDownloadEvent(AcmFolder folder) throws AcmObjectNotFoundException
    {
        String userId = MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY);
        String ipAddress = MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY);

        AcmContainer container = folderService.findContainerByFolderId(folder.getId());

        AcmFolderDownloadedEvent event = new AcmFolderDownloadedEvent(folder);
        event.setIpAddress(ipAddress);
        event.setUserId(userId);
        event.setSucceeded(true);
        event.setParentObjectType(container.getContainerObjectType());
        event.setParentObjectId(container.getContainerObjectId());
        getApplicationEventPublisher().publishEvent(event);
    }

    public boolean isFolderRequestedToBeCompressed(CompressNode compressNode, AcmFolder childFolder)
    {
        if (Objects.nonNull(compressNode) && Objects.nonNull(childFolder))
        {
            return compressNode
                    .getSelectedNodes()
                    .stream()
                    .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(childFolder.getId()) && fileFolderNode.isFolder())
                    ||
                    isFolderParentSelected(compressNode, childFolder)
                    ||
                    isRootFolderSelected(compressNode);
        }
        else
        {
            return true;
        }
    }

    private boolean isFolderParentSelected(CompressNode compressNode, AcmFolder childFolder)
    {
        if (childFolder.getParentFolder() != null)
        {
            if (compressNode.getSelectedNodes().stream()
                    .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(childFolder.getParentFolder().getId())))
            {
                return true;
            }
            return isFolderParentSelected(compressNode, childFolder.getParentFolder());
        }
        return false;
    }

    /*
     * If filename is duplicate, we will have to rename it.
     * Otherwise, the zip file errors out.
     * Here we just append an underscore "_" and date the file was created
     */
    private String getUniqueObjectName(List<String> fileFolderList, DateTimeFormatter formatter, AcmObject obj, String objectName)
    {
        if (fileFolderList.contains(objectName))
        {
            Date objectnameUniqueness = (obj instanceof AcmEntity) ? AcmEntity.class.cast(obj).getCreated() : new Date();
            objectName = objectName + "_" + DATE_FORMATTER.print(objectnameUniqueness.getTime());
        }
        return objectName;
    }

    private boolean isFileSelected(Long fileId, CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(fileId) && !fileFolderNode.isFolder());
    }

    private boolean isFileParentFolderSelected(Long parentFolderId, CompressNode compressNode)
    {
        AcmFolder parentFolder = getAcmFolderDao().find(parentFolderId);
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(parentFolderId) && fileFolderNode.isFolder())
                ||
                isFolderParentSelected(compressNode, parentFolder);
    }

    private boolean isRootFolderSelected(CompressNode compressNode)
    {
        return compressNode.getSelectedNodes()
                .stream()
                .anyMatch(fileFolderNode -> fileFolderNode.getObjectId().equals(compressNode.getRootFolderId())
                        && fileFolderNode.isFolder());
    }

    private boolean canBeCompressed(EcmFile file, List<AcmObject> files, AcmFolder folder, CompressNode compressNode)
    {
        // TODO : Check isConverted only for Response folder
        if (folderService.isFolderOrParentFolderWithName(folder, "03 Response") && isConverted(file, files))
        {
            return false;
        }
        if (compressNode == null)
        {
            return true;
        }
        else
            return isFileSelected(file.getId(), compressNode) || isFileParentFolderSelected(folder.getId(), compressNode)
                    || isRootFolderSelected(compressNode);
    }

    @Override
    public List<EcmFile> filterConvertedFiles(List<EcmFile> files)
    {
        List<AcmObject> objects = files.stream().map(obj -> (AcmObject) obj).collect(Collectors.toList());
        return files.stream().filter(f -> !isConverted(f, objects)).collect(Collectors.toList());
    }

    /**
     * @param
     * @param files
     * @return
     */
    private boolean isConverted(EcmFile file, List<AcmObject> files)
    {
        // TODO: Currently, base file name is used to link the original file with the PDF rendition. We should devise a
        // way to associate the rendition with the original file trough means other than base file name.
        if (".docx".equalsIgnoreCase(file.getFileActiveVersionNameExtension())
        || ".pptx".equalsIgnoreCase(file.getFileActiveVersionNameExtension()))
        {
            return files.stream()
                    .map(EcmFile.class::cast)
                    .filter(f -> ".pdf".equalsIgnoreCase(f.getFileActiveVersionNameExtension()))
                    .anyMatch(f -> f.getFileName().equals(file.getFileName())
                            || f.getFileName().equals(file.getFileName() + "-converted-" + EcmFile.class.cast(file).getActiveVersionTag()));

        }
        else
        {
            return false;
        }
    }

    /**
     * Returns path of compressed folder file
     */
    @Override
    public String getCompressedFolderFilePath(AcmFolder folder)
    {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String pathSeparator = System.getProperty("file.separator");

        String filename = String.format(compressorServiceConfig.getFileNameFormat(),
                tmpDir.endsWith(pathSeparator) ? tmpDir : concatStrings(tmpDir, pathSeparator), folder.getId(), folder.getName());
        return filename;
    }

    public String getCompressedZipPath()
    {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String pathSeparator = System.getProperty("file.separator");

        String filename = String.format(compressedZipNameFormat,
                tmpDir.endsWith(pathSeparator) ? tmpDir : concatStrings(tmpDir, pathSeparator), UUID.randomUUID());
        return filename;
    }

    /**
     * Utility method used for string concatenation.
     *
     * @param pathParts
     *            an array of strings to be concatenated.
     * @return the concatenated string.
     */
    private String concatStrings(String... pathParts)
    {
        return Stream.of(pathParts).collect(Collectors.joining());
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public void setMaxSize(long maxSize)
    {
        this.maxSize = maxSize;
    }

    public void setSizeUnit(String sizeUnit)
    {
        this.sizeUnit = SizeUnit.valueOf(sizeUnit);
    }

    public AcmFolderDao getAcmFolderDao()
    {
        return acmFolderDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao)
    {
        this.acmFolderDao = acmFolderDao;
    }

    public AcmMailTemplateConfigurationService getTemplateService()
    {
        return templateService;
    }

    public void setTemplateService(AcmMailTemplateConfigurationService templateService)
    {
        this.templateService = templateService;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public CompressorServiceConfig getCompressorServiceConfig()
    {
        return compressorServiceConfig;
    }

    public void setCompressorServiceConfig(CompressorServiceConfig compressorServiceConfig)
    {
        this.compressorServiceConfig = compressorServiceConfig;
    }

    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
