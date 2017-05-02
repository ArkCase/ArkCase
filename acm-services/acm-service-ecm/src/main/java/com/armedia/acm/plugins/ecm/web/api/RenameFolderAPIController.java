package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
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


@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class RenameFolderAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @RequestMapping(value = "/folder/{objectId}/{newName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder renameFolder(
            @PathVariable("objectId") Long objectId,
            @PathVariable("newName") String newName) throws AcmUserActionFailedException,
            AcmObjectNotFoundException, AcmFolderException
    {
        log.info("Renaming folder, folderId: {} with name: {}", objectId, newName);
        AcmFolder source = getFolderService().findById(objectId);
        try
        {
            AcmFolder renamedFolder = getFolderService().renameFolder(objectId, newName);
            log.info("Folder with id: {} successfully renamed to: {}", objectId, newName);
            AcmContainer container = getFolderService().findContainerByFolderId(renamedFolder.getId());

            getFolderEventPublisher().publishFolderRenamedEvent(renamedFolder, true,
                    container.getContainerObjectType(), container.getContainerObjectId());
            return renamedFolder;
        } catch (AcmUserActionFailedException | AcmFolderException | AcmObjectNotFoundException e)
        {
            log.error("Exception occurred while trying to rename folder with id: {}", objectId);
            getFolderEventPublisher().publishFolderRenamedEvent(source, false, null, null);
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
