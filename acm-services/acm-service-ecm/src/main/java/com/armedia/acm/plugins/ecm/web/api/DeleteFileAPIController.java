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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectLockException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.RecycleBinItemDTO;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemEventPublisher;
import com.armedia.acm.plugins.ecm.service.RecycleBinItemService;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * Created by marjan.stefanoski on 06.04.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class DeleteFileAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService fileService;
    private FileEventPublisher fileEventPublisher;
    private RecycleBinItemService recycleBinItemService;
    private RecycleBinItemEventPublisher recycleBinItemEventPublisher;

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/id/{fileId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteFile(@PathVariable("fileId") Long objectId, Authentication authentication, HttpSession session)
            throws AcmUserActionFailedException
    {
        log.info("File with id: {} will be deleted", objectId);
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        EcmFile source = getFileService().findById(objectId);
        try
        {
            getFileService().deleteFile(objectId, source.getParentObjectId(), source.getParentObjectType());
            log.info("Links for file with id :{} successfully deleted by user {} ", objectId, authentication.getName());
            getFileEventPublisher().publishFileDeletedEvent(source, authentication, ipAddress, true);
            log.info("File with id :{} moved to recycle bin", objectId);
            getRecycleBinItemEventPublisher().publishFileMovedToRecycleBinEvent(source, authentication, ipAddress, true);

            return prepareJsonReturnMsg(EcmFileConstants.SUCCESS_DELETE_MSG, objectId, source.getFileName());
        }
        catch (AcmUserActionFailedException e)
        {
            log.error("Exception occurred while trying to delete file with id: {}, reason {}", objectId, e.getMessage(), e);
            getFileEventPublisher().publishFileDeletedEvent(source, authentication, ipAddress, false);
            throw e;
        }
        catch (AcmObjectNotFoundException e)
        {
            log.debug("File with id: {} not found in the DB, reason {} ", objectId, e.getMessage(), e);
            getFileEventPublisher().publishFileDeletedEvent(source, authentication, ipAddress, false);
            return prepareJsonReturnMsg(EcmFileConstants.SUCCESS_DELETE_MSG, objectId);
        }
    }

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/deleteTemporary/{fileId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String putFileIntoRecycleBin(@PathVariable("fileId") Long objectId, Authentication authentication, HttpSession session)
            throws AcmAppErrorJsonMsg
    {
        log.info("File with id: {} will be temporary deleted, by user: {}", objectId, authentication.getName());
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        EcmFile source = getFileService().findById(objectId);
        try
        {
            getFileService().putFileIntoRecycleBin(objectId, authentication, session);
            log.info("File with id: {} temporary deleted, by {}", objectId, source.getModifier());
            getRecycleBinItemEventPublisher().publishFileMovedToRecycleBinEvent(source, authentication, ipAddress, true);
            getFileEventPublisher().publishFileMovedToRecycleBinEvent(source, authentication, ipAddress, true);
            return prepareJsonReturnMsg(EcmFileConstants.SUCCESS_TEMPORARY_DELETE_MSG, objectId, source.getFileName());
        }
        catch (AcmObjectLockException e)
        {
            log.error("Exception occurred while trying to temporary delete file with id: {}, reason {}", objectId, e.getMessage());
            getRecycleBinItemEventPublisher().publishFileMovedToRecycleBinEvent(source, authentication, ipAddress, false);
            throw new AcmAppErrorJsonMsg("File is locked and can't be deleted", EcmFileConstants.FILE, Long.toString(objectId), e);
        }
        catch (AcmUserActionFailedException | AcmCreateObjectFailedException e)
        {
            log.error("Exception occurred while trying to temporary delete file with id: {}, reason {}", objectId, e.getMessage());
            getRecycleBinItemEventPublisher().publishFileMovedToRecycleBinEvent(source, authentication, ipAddress, false);
            throw new AcmAppErrorJsonMsg("File can't be deleted", EcmFileConstants.FILE, Long.toString(objectId), e);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.debug("File with id: {} not found in the DB, reason {}", objectId, e.getMessage(), e);
            throw new AcmAppErrorJsonMsg(EcmFileConstants.FILE_NOT_FOUND_DB, EcmFileConstants.FILE, "fileId", e);
        }
    }

    @RequestMapping(value = "/permanent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void removeItemsFromRecycleBin(@RequestBody List<RecycleBinItemDTO> filesToBeDeleted,
                                          Authentication authentication, HttpSession session) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        String ipAddress = (String) session.getAttribute(EcmFileConstants.IP_ADDRESS_ATTRIBUTE);
        for (RecycleBinItemDTO file : filesToBeDeleted)
        {
            EcmFile source = getFileService().findById(file.getObjectId());
            getFileService().deleteFilePermanently(source.getId(), file.getId());
            log.info("File with id: {} permanently deleted", file.getObjectId());
            getFileEventPublisher().publishFileDeletedEvent(source, authentication, ipAddress, true);
        }
    }

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/fileLinks/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EcmFile> getFileLinks(@PathVariable("fileId") Long objectId, Authentication authentication, HttpSession session)
            throws AcmObjectNotFoundException
    {
        return getFileService().getFileLinks(objectId);
    }

    protected String prepareJsonReturnMsg(String msg, Long fileId, String fileName)
    {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedFileId", fileId);
        objectToReturnJSON.put("name", fileName);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
    }

    protected String prepareJsonReturnMsg(String msg, Long fileId)
    {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedFileId", fileId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
    }

    public FileEventPublisher getFileEventPublisher()
    {
        return fileEventPublisher;
    }

    public void setFileEventPublisher(FileEventPublisher fileEventPublisher)
    {
        this.fileEventPublisher = fileEventPublisher;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    public RecycleBinItemService getRecycleBinItemService()
    {
        return recycleBinItemService;
    }

    public void setRecycleBinItemService(RecycleBinItemService recycleBinItemService)
    {
        this.recycleBinItemService = recycleBinItemService;
    }

    public RecycleBinItemEventPublisher getRecycleBinItemEventPublisher()
    {
        return recycleBinItemEventPublisher;
    }

    public void setRecycleBinItemEventPublisher(RecycleBinItemEventPublisher recycleBinItemEventPublisher)
    {
        this.recycleBinItemEventPublisher = recycleBinItemEventPublisher;
    }
}
