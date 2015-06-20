package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import com.armedia.acm.plugins.ecm.service.FolderEventPublisher;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.NumberFormat;

/**
 * Created by marjan.stefanoski on 02.04.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class CreateFolderByPathAPIController {

    private AcmFolderService folderService;
    private EcmFileService ecmFileService;
    private FolderAndFilesUtils folderAndFilesUtils;

    private FolderEventPublisher folderEventPublisher;
    private FileEventPublisher fileEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/createFolderByPath", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmFolder addNewFolder(
            @RequestParam("targetObjectType") String targetObjectType,
            @RequestParam("targetObjectId") Long targetObjectId,
            @RequestParam("newPath") String newPath,
            @RequestParam(value = "docIds", required = false) String docIds,
            @RequestParam(value = "isCopy", required = false, defaultValue = "false") boolean isCopy,
            Authentication authentication,
            HttpSession session) throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFolderException {
        /**
         * This API is documented in ark-document-management.raml.  If you update the API, also update the RAML.
         */

        String ipAddress = (String) session.getAttribute(AcmFolderConstants.IP_ADDRESS_ATTRIBUTE);

        if( log.isInfoEnabled() ) {
            log.info("Creating new folder by path" + newPath);
        }

        try {
            AcmFolder newFolder = getFolderService().addNewFolderByPath(targetObjectType, targetObjectId, newPath);
            if( log.isInfoEnabled() ) {
                log.info("Created new folder " + newFolder.getId());
            }

            if ( isCopy ){
               copyDocumentsToNewFolder(targetObjectType, targetObjectId, docIds, newFolder, authentication, ipAddress);
            }  else {
                moveDocumentsToNewFolder(targetObjectType, targetObjectId, docIds, newFolder, authentication, ipAddress);
            }

            getFolderEventPublisher().publishFolderCreatedEvent(newFolder,authentication,ipAddress,true);
            return newFolder;
        } catch (  AcmCreateObjectFailedException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to create a new folder by path",e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null,authentication,ipAddress,false);
            throw e;
        } catch (AcmObjectNotFoundException e) {
            if( log.isErrorEnabled() ){
                log.error("Exception occurred while trying to create new folder by path ",e);
            }
            getFolderEventPublisher().publishFolderCreatedEvent(null, authentication, ipAddress, false);
            throw e;
        }
    }

    private void copyDocumentsToNewFolder(
            String targetObjectType,
            Long targetObjectId,
            String docIds,
            AcmFolder newFolder,
            Authentication auth,
            String ipAddress)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        if ( docIds != null )
        {
            String[] arrDocIds = docIds.split(",");
            for ( String docId : arrDocIds )
            {
                if ( docId == null || docId.trim().isEmpty() )
                {
                    continue;
                }

                Long lngDocId = getFolderAndFilesUtils().convertToLong(docId.trim());
                if ( lngDocId == null )
                {
                    continue;
                }

                EcmFile copied = getEcmFileService().copyFile(lngDocId, targetObjectId, targetObjectType, newFolder.getId());
                getFileEventPublisher().publishFileCopiedEvent(copied, auth, ipAddress, true);

            }
        }
    }

    private void moveDocumentsToNewFolder(
            String targetObjectType,
            Long targetObjectId,
            String docIds,
            AcmFolder newFolder,
            Authentication auth,
            String ipAddress)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmCreateObjectFailedException
    {
        if ( docIds != null )
        {
            String[] arrDocIds = docIds.split(",");
            for ( String docId : arrDocIds )
            {
                if ( docId == null || docId.trim().isEmpty() )
                {
                    continue;
                }

                Long lngDocId = getFolderAndFilesUtils().convertToLong(docId.trim());
                if ( lngDocId == null )
                {
                    continue;
                }

                EcmFile moved = getEcmFileService().moveFile(lngDocId, targetObjectId, targetObjectType, newFolder.getId());
                getFileEventPublisher().publishFileMovedEvent(moved, auth, ipAddress, true);


            }
        }
    }

    public FolderEventPublisher getFolderEventPublisher() {
        return folderEventPublisher;
    }

    public void setFolderEventPublisher(FolderEventPublisher folderEventPublisher) {
        this.folderEventPublisher = folderEventPublisher;
    }

    public AcmFolderService getFolderService() {
        return folderService;
    }

    public void setFolderService(AcmFolderService folderService) {
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
}

