package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 20.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class MoveFolderAPIController {

    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/folder/move/{folderToMoveId}/{dstFolderId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder moveFolder(
            @PathVariable("folderToMoveId") Long folderToMoveId,
            @PathVariable("dstFolderId") Long dstFolderId,
            Authentication authentication,
            HttpSession session
    ) throws AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Folder with id: " + folderToMoveId + " will be moved to the location with id: " + dstFolderId);
        }
        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);
        AcmFolder sourceFolderToBeMoved = getFolderService().findById(folderToMoveId);
        AcmFolder sourceDstFolder = getFolderService().findById(dstFolderId);
        try {
            AcmFolder movedFolder = getFolderService().moveFolder(sourceFolderToBeMoved, sourceDstFolder);
            if (log.isInfoEnabled()) {
                log.info("Folder with id: " + folderToMoveId + " successfully moved to the location with id: " + dstFolderId);
            }
            getFolderEventPublisher().publishFolderMovedEvent(movedFolder, authentication, ipAddress, true);
            getFolderEventPublisher().publishFolderCreatedEvent(sourceDstFolder, authentication, ipAddress, true);
            return movedFolder;
        } catch (AcmUserActionFailedException e) {
            if (log.isErrorEnabled()) {
                log.error("Exception occurred while trying to move folder with id: " + folderToMoveId + " to the location with id: " + dstFolderId);
            }
            getFolderEventPublisher().publishFolderMovedEvent(sourceFolderToBeMoved, authentication, ipAddress, false);
            throw e;
        } catch (AcmObjectNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.debug("Folder with id: " + e.getObjectId() + " not found in the DB");
            }
            getFolderEventPublisher().publishFolderMovedEvent(sourceFolderToBeMoved, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folderToMoveId, e.getMessage(), e);
        } catch (AcmFolderException e) {
            if (log.isErrorEnabled()) {
                log.error("Exception occurred while trying to move folder with id: " + folderToMoveId + " to the location with id:" + dstFolderId);
            }
            getFolderEventPublisher().publishFolderMovedEvent(sourceFolderToBeMoved, authentication, ipAddress, false);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_MOVE_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE, folderToMoveId, "Exception occurred while trying to move the folder.", e);
        }
    }
    public AcmFolderService getFolderService() {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService) {
        this.folderService = folderService;
    }

    public FolderEventPublisher getFolderEventPublisher() {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher) {
        this.folderEventPublisher = folderEventPublisher;
    }
}
