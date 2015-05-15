package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.FolderDTO;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by marjan.stefanoski on 01.05.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class CopyFolderAPIController {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private AcmFolderService folderService;
    private FolderEventPublisher folderEventPublisher;

    @RequestMapping(value = "/folder/copy/{folderId}/{dstFolderId}/{targetObjectType}/{targetObjectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FolderDTO copyFolder(
            @PathVariable("folderId") Long folderId,
            @PathVariable("dstFolderId") Long dstFolderId,
            @PathVariable("targetObjectType") String targetObjectType,
            @PathVariable("targetObjectId") Long targetObjectId,
            Authentication authentication,
            HttpSession session
    ) throws AcmUserActionFailedException {

        /**
         * This API is documented in ark-document-management.raml.  If you update the API, also update the RAML.
         */

        if (log.isInfoEnabled()) {
            log.info("Folder with id: " + folderId + " will be copy into folder with id: " + dstFolderId);
        }
        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);
        AcmFolder source = getFolderService().findById(folderId);
        try {
            if (log.isInfoEnabled()) {
                log.info("Folder with id: " + folderId + " successfully copied to the location with id: " + dstFolderId);
            }
            AcmFolder folder = getFolderService().copyFolder(folderId,dstFolderId,targetObjectId,targetObjectType);
            getFolderEventPublisher().publishFolderCopiedEvent(source, authentication, ipAddress, true);
            getFolderEventPublisher().publishFolderCreatedEvent(folder, authentication, ipAddress, true);
            FolderDTO folderDTO = new FolderDTO();
            folderDTO.setOriginalFolderId(folderId);
            folderDTO.setNewFolder(folder);
            return folderDTO;
        } catch ( AcmFolderException| AcmCreateObjectFailedException | AcmObjectNotFoundException e ) {
            if (log.isErrorEnabled()) {
                log.error("Exception occurred while trying to copy folder with id: " + folderId + " to the location with id:" + dstFolderId + "  "+e.getMessage(),e);
            }
            getFolderEventPublisher().publishFolderCopiedEvent(source,authentication,ipAddress,false);
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_COPY_FOLDER,AcmFolderConstants.OBJECT_FOLDER_TYPE,source.getId(),"Exception occurred while trying to copy folder "+e.getMessage(),e);
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
