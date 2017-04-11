package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class AddNewFolderAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @RequestMapping(value = "/folder/{parentFolderId}/{newFolderName}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(
            @PathVariable("parentFolderId") Long parentFolderId,
            @PathVariable("newFolderName") String newFolderName) throws AcmCreateObjectFailedException,
            AcmUserActionFailedException, AcmObjectNotFoundException
    {
        /**
         * This API is documented in ark-document-management.raml.  If you update the API, also update the RAML.
         */

        log.info("Creating new folder into: {} with name: {}", parentFolderId, newFolderName);
        try
        {
            AcmContainer container = getFolderService().findContainerByFolderId(parentFolderId);
            AcmFolder newFolder = getFolderService().addNewFolder(parentFolderId, newFolderName,
                    container.getContainerObjectId(), container.getContainerObjectType());

            log.info("Created new folder: {} with name: {}", newFolder.getId(), newFolderName);
            getFolderEventPublisher().publishFolderCreatedEvent(newFolder, true, container.getContainerObjectType(),
                    container.getContainerObjectId());
            return newFolder;
        } catch (AcmCreateObjectFailedException | AcmObjectNotFoundException e)
        {
            log.error("Exception occurred while trying to create new folder: {} ", newFolderName, e);
            // create mock source to audit the event
            AcmFolder mockFolder = new AcmFolder();
            mockFolder.setName(newFolderName);
            mockFolder.setId(1L);
            mockFolder.setModified(new Date());
            mockFolder.setModifier(AuthenticationUtils.getUsername());
            getFolderEventPublisher().publishFolderCreatedEvent(mockFolder, false, null,
                    null);
            throw e;
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
}

