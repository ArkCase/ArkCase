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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.data.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 02.04.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class CreateFolderByPathAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AcmContainerDao containerDao;
    private AcmFolderService folderService;
    private EcmFileService ecmFileService;
    private FolderAndFilesUtils folderAndFilesUtils;
    private ArkPermissionEvaluator arkPermissionEvaluator;
    private FolderEventPublisher folderEventPublisher;
    private FileEventPublisher fileEventPublisher;

    @RequestMapping(value = "/createFolderByPath", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(@RequestParam("targetObjectType") String targetObjectType,
            @RequestParam("targetObjectId") Long targetObjectId, @RequestParam("newPath") String newPath,
            @RequestParam(value = "docIds", required = false) String docIds,
            @RequestParam(value = "isCopy", required = false, defaultValue = "false") boolean isCopy, Authentication authentication,
            HttpSession session) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            AcmFolderException, AcmAccessControlException
    {
        /**
         * This API is documented in ark-document-management.raml. If you update the API, also update the RAML.
         */

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        if (log.isInfoEnabled())
        {
            log.info("Creating new folder by path" + newPath);
        }

        try
        {
            List<Long> docLongIds = Arrays.asList(docIds.split(";")).stream().map(Long::parseLong).collect(Collectors.toList());
            for (Long docId : docLongIds)
            {
                if (!getArkPermissionEvaluator().hasPermission(authentication, docId, "FILE", "read|group-read|write|group-write"))
                {
                    throw new AcmAccessControlException(Arrays.asList(""),
                            "The user {" + authentication.getName() + "} is not allowed to read from file with id=" + docId);
                }
            }

            AcmContainer container = getContainerDao().findFolderByObjectTypeAndId(targetObjectType, targetObjectId);
            if (container == null)
            {
                throw new AcmObjectNotFoundException(targetObjectType, targetObjectId, "Container object not found", null);
            }
            if (!getArkPermissionEvaluator().hasPermission(authentication, container.getFolder().getId(), "FOLDER", "write|group-write"))
            {
                throw new AcmAccessControlException(Arrays.asList(""), "The user {" + authentication.getName()
                        + "} is not allowed to write to target folder with id=" + container.getFolder().getId());
            }

            AcmFolder newFolder = getFolderService().addNewFolderByPath(targetObjectType, targetObjectId, newPath);
            if (log.isInfoEnabled())
            {
                log.info("Created new folder " + newFolder.getId());
            }

            if (isCopy)
            {
                copyDocumentsToNewFolder(targetObjectType, targetObjectId, docIds, newFolder, authentication, ipAddress);
            }
            else
            {
                moveDocumentsToNewFolder(targetObjectType, targetObjectId, docIds, newFolder, authentication, ipAddress);
            }

            getFolderEventPublisher().publishFolderCreatedEvent(newFolder, authentication, ipAddress, true);
            return newFolder;
        }
        catch (AcmCreateObjectFailedException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to create a new folder by path", e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null, authentication, ipAddress, false);
            throw e;
        }
        catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.error("Exception occurred while trying to create new folder by path ", e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null, authentication, ipAddress, false);
            throw e;
        }
    }

    private void copyDocumentsToNewFolder(String targetObjectType, Long targetObjectId, String docIds, AcmFolder newFolder,
            Authentication auth, String ipAddress)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        if (docIds != null)
        {
            String[] arrDocIds = docIds.split(",");
            for (String docId : arrDocIds)
            {
                if (docId == null || docId.trim().isEmpty())
                {
                    continue;
                }

                Long lngDocId = getFolderAndFilesUtils().convertToLong(docId.trim());
                if (lngDocId == null)
                {
                    continue;
                }

                EcmFile original = getEcmFileService().findById(lngDocId);
                EcmFile copied = getEcmFileService().copyFile(lngDocId, targetObjectId, targetObjectType, newFolder.getId());
                getFileEventPublisher().publishFileCopiedEvent(copied, original, auth, ipAddress, true);

            }
        }
    }

    private void moveDocumentsToNewFolder(String targetObjectType, Long targetObjectId, String docIds, AcmFolder newFolder,
            Authentication auth, String ipAddress)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        if (docIds != null)
        {
            String[] arrDocIds = docIds.split(",");
            for (String docId : arrDocIds)
            {
                if (docId == null || docId.trim().isEmpty())
                {
                    continue;
                }

                Long lngDocId = getFolderAndFilesUtils().convertToLong(docId.trim());
                if (lngDocId == null)
                {
                    continue;
                }

                EcmFile moved = getEcmFileService().moveFile(lngDocId, targetObjectId, targetObjectType, newFolder.getId());
                getFileEventPublisher().publishFileMovedEvent(moved, auth, ipAddress, true);

            }
        }
    }

    public FolderEventPublisher getFolderEventPublisher()
    {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher)
    {
        this.folderEventPublisher = folderEventPublisher;
    }

    public AcmFolderService getFolderService()
    {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
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

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

    public AcmContainerDao getContainerDao()
    {
        return containerDao;
    }

    public void setContainerDao(AcmContainerDao containerDao)
    {
        this.containerDao = containerDao;
    }
}
