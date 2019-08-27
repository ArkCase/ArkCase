package com.armedia.acm.plugins.ecm.web.api;

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

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmMultipartFile;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.EcmFilePostUploadEvent;
import com.armedia.acm.plugins.ecm.model.EcmFileUploaderConfig;
import com.armedia.acm.plugins.ecm.model.FileChunkDetails;
import com.armedia.acm.plugins.ecm.model.FileDetails;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileChunkService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class FileUploadAPIController implements ApplicationEventPublisherAware
{
    private final String uploadFileType = "attachment";
    private Logger log = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private ObjectConverter objectConverter;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private ApplicationEventPublisher applicationEventPublisher;
    private FileChunkService fileChunkService;
    private EcmFileUploaderConfig ecmFileUploaderConfig;
    private ArkPermissionEvaluator permissionEvaluator;

    // #parentObjectType == 'USER_ORG' applies to uploading profile picture
    @PreAuthorize("hasPermission(#parentObjectId, #parentObjectType, 'uploadOrReplaceFile') or #parentObjectType == 'USER_ORG'")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EcmFile> uploadFile(@RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId, @RequestParam(value = "folderId", required = false) Long folderId,
            @RequestParam(value = "fileType", required = false, defaultValue = uploadFileType) String fileType,
            @RequestParam(value = "fileLang", required = false) String fileLang, MultipartHttpServletRequest request,
            Authentication authentication, HttpSession session)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, IOException, AcmAccessControlException
    {
        AcmFolder folder = getParentFolder(parentObjectType, parentObjectId, folderId);

        if (!getArkPermissionEvaluator().hasPermission(authentication, folder.getId(), "FOLDER", "write|group-write"))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The user {" + authentication.getName() + "} is not allowed to write to folder with id=" + folder.getId());
        }

        String folderCmisId = folder.getCmisFolderId();
        List<EcmFile> uploaded = uploadFiles(authentication, parentObjectType, parentObjectId, fileType, fileLang, folderCmisId, request,
                session);
        return uploaded;
    }

    @PreAuthorize("hasPermission(#parentObjectId, #parentObjectType, 'uploadOrReplaceFile')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "!"
            + MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String uploadFileForBrowsersWithoutFileUploadViaXHR(@RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId, @RequestParam(value = "folderId", required = false) Long folderId,
            @RequestParam(value = "fileType", required = false, defaultValue = uploadFileType) String fileType,
            MultipartHttpServletRequest request, HttpServletResponse response, Authentication authentication, HttpSession session)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, IOException, AcmAccessControlException
    {
        AcmFolder folder = getParentFolder(parentObjectType, parentObjectId, folderId);

        if (!getArkPermissionEvaluator().hasPermission(authentication, folder.getId(), "FOLDER", "write|group-write"))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The user {" + authentication.getName() + "} is not allowed to write to folder with id=" + folder.getId());
        }

        String responseMimeType = MediaType.TEXT_PLAIN_VALUE;
        response.setContentType(responseMimeType);

        String folderCmisId = folder.getCmisFolderId();
        List<EcmFile> uploaded = uploadFiles(authentication, parentObjectType, parentObjectId, fileType, folderCmisId, request, session);

        String jsonUploadedFiles = getObjectConverter().getJsonMarshaller().marshal(uploaded);

        return jsonUploadedFiles;
    }

    protected List<EcmFile> uploadFiles(Authentication authentication, String parentObjectType, Long parentObjectId, String fileType,
            String folderCmisId, MultipartHttpServletRequest request, HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {

        return uploadFiles(authentication, parentObjectType, parentObjectId, fileType, null, folderCmisId, request, session);
    }

    protected List<EcmFile> uploadFiles(Authentication authentication, String parentObjectType, Long parentObjectId, String fileType,
            String fileLang, String folderCmisId, MultipartHttpServletRequest request, HttpSession session)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, IOException
    {

        // for multiple files
        MultiValueMap<String, MultipartFile> attachments = request.getMultiFileMap();

        List<EcmFile> uploadedFiles = new ArrayList<>();

        if (attachments != null)
        {
            for (Map.Entry<String, List<MultipartFile>> entry : attachments.entrySet())
            {
                final List<MultipartFile> attachmentsList = entry.getValue();

                if (attachmentsList != null && !attachmentsList.isEmpty())
                {
                    for (final MultipartFile attachment : attachmentsList)
                    {
                        AcmMultipartFile acmMultipartFile = new AcmMultipartFile(attachment, true);

                        EcmFile metadata = new EcmFile();
                        metadata.setFileType(fileType);
                        metadata.setFileLang(fileLang);
                        metadata.setFileName(attachment.getOriginalFilename());
                        metadata.setFileActiveVersionMimeType(acmMultipartFile.getContentType());
                        metadata.setUuid(request.getParameter("uuid"));
                        EcmFile temp = getEcmFileService().upload(authentication, acmMultipartFile, folderCmisId, parentObjectType,
                                parentObjectId,
                                metadata);
                        uploadedFiles.add(temp);

                        applicationEventPublisher.publishEvent(new EcmFilePostUploadEvent(temp, authentication.getName()));
                    }
                }
            }
        }

        return uploadedFiles;
    }

    @PreAuthorize("hasPermission(#ecmFileId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/{ecmFileId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteFile(@PathVariable("ecmFileId") String ecmFileId, Authentication authentication)
    {
        if (log.isInfoEnabled())
        {
            log.info("The user '" + authentication.getName() + "' deleted file: '" + ecmFileId + "'");
        }

        // since jQuery File Upload wants the file name as an attribute name, we have to build the JSON manually
        // (since we can't write a POJO to have field names of random file names)
        JSONObject retval = new JSONObject();
        JSONArray files = new JSONArray();
        retval.put("files", files);

        JSONObject deleted = new JSONObject();
        deleted.put("The File Name.txt", true);
        files.put(deleted);

        String filesString = retval.toString();

        return new ResponseEntity<>(filesString, HttpStatus.OK);

    }

    private AcmFolder getParentFolder(String parentObjectType, Long parentObjectId, Long folderId)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        AcmFolder folder = null;
        if (folderId != null)
        {
            folder = getAcmFolderService().findById(folderId);
            if (folder == null)
            {
                throw new AcmUserActionFailedException(EcmFileConstants.USER_ACTION_UPLOAD_FILE, EcmFileConstants.OBJECT_FILE_TYPE, null,
                        "Destination Folder not found", null);
            }
        }
        else
        {
            AcmContainer container = getEcmFileService().getOrCreateContainer(parentObjectType, parentObjectId);
            if (container.getFolder() == null)
            {
                // not really possible since the cm_folder_id is not nullable. But we'll account for it anyway
                throw new IllegalStateException("Container '" + container.getId() + "' does not have a folder!");
            }
            folder = container.getFolder();
        }
        return folder;
    }

    @RequestMapping(value = "/uploadChunks", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public FileChunkDetails uploadChunks(@RequestParam("isFileChunk") boolean isFileChunk,
            @RequestParam(value = "parentObjectType", required = false) String parentObjectType,
            @RequestParam(value = "parentObjectId", required = false) Long parentObjectId,
            @RequestParam(value = "folderId", required = false) Long folderId,
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "fileLang", required = false) String fileLang,
            HttpServletRequest request, Authentication authentication, HttpSession session) throws Exception
    {

        log.debug("Starting a file upload by user {}", authentication.getName());

        String fileName = "";
        String uniqueArkCaseHashFileIdentifier = ecmFileUploaderConfig.getUniqueHashFileIdentifier();

        if (!isFileChunk)
        {
            AcmFolder folder = getParentFolder(parentObjectType, parentObjectId, folderId);

            if (!getArkPermissionEvaluator().hasPermission(authentication, folder.getId(), "FOLDER", "write|group-write"))
            {
                throw new AcmAccessControlException(Arrays.asList(""),
                        "The user {" + authentication.getName() + "} is not allowed to write to folder with id=" + folder.getId());
            }

            List<EcmFile> files = uploadFiles(authentication, parentObjectType, parentObjectId, fileType, fileLang,
                    folder.getCmisFolderId(), (MultipartHttpServletRequest) request, session);
            if (files != null && files.size() == 1)
            {
                fileName = files.get(0).getFileName();
            }
        }
        else
        {
            if (!getArkPermissionEvaluator().hasPermission(authentication, folderId, "FOLDER", "write|group-write"))
            {
                throw new AcmAccessControlException(Arrays.asList(""),
                        "The user {" + authentication.getName() + "} is not allowed to write to folder with id=" + folderId);
            }
            fileName = getEcmFileService().uploadFileChunk((MultipartHttpServletRequest) request, fileName,
                    uniqueArkCaseHashFileIdentifier);
        }

        FileChunkDetails fileChunkDetails = new FileChunkDetails();
        fileChunkDetails.setFileName(fileName);
        fileChunkDetails.setUuid(request.getParameter("uuid"));
        fileChunkDetails.setFolderId(Long.parseLong(request.getParameter("folderId")));
        return fileChunkDetails;
    }

    @RequestMapping(value = "/mergeChunks", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public ResponseEntity mergeChunks(@RequestBody FileDetails fileDetails, Authentication authentication) throws Exception
    {
        log.info("Merging chunks file [{}] per user [{}]", fileDetails.getObjectId(), authentication.getName());
        boolean hasPermission = isActionAllowed(authentication, "write|group-write", fileDetails.getObjectId());
        if (!hasPermission)
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        AcmFolder folder = getParentFolder(fileDetails.getObjectType(), fileDetails.getObjectId(), fileDetails.getFolderId());
        Document existingFile = null;

        if (fileDetails.getExistingFileId() != null)
        {
            EcmFile existingEcmFile = getEcmFileService().findById(fileDetails.getExistingFileId());
            existingFile = (Document) getEcmFileService().findObjectById(existingEcmFile.getCmisRepositoryId(),
                    existingEcmFile.getVersionSeriesId());
        }
        getFileChunkService().mergeAndUploadFiles(fileDetails, folder, existingFile, authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public String getUploadFileType()
    {
        return uploadFileType;
    }

    public FileChunkService getFileChunkService()
    {
        return fileChunkService;
    }

    public void setFileChunkService(FileChunkService fileChunkService)
    {
        this.fileChunkService = fileChunkService;
    }

    public EcmFileUploaderConfig getEcmFileUploaderConfig()
    {
        return ecmFileUploaderConfig;
    }

    public void setEcmFileUploaderConfig(EcmFileUploaderConfig ecmFileUploaderConfig)
    {
        this.ecmFileUploaderConfig = ecmFileUploaderConfig;
    }

    private boolean isActionAllowed(Authentication authentication, String action, Long targetId)
    {
        return permissionEvaluator.hasPermission(authentication, targetId, "FILE", action);
    }

    public void setPermissionEvaluator(ArkPermissionEvaluator permissionEvaluator)
    {
        this.permissionEvaluator = permissionEvaluator;
    }

}
