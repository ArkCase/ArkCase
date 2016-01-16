package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maksud.sharif on 1/15/2016.
 */

@Controller
@RequestMapping({"/api/v1/service/ecm", "/api/latest/service/ecm"})
public class FolderDocumentCountAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read')")
    @RequestMapping(value = "/folder/counts/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Integer> folderDocumentCountList(
            Authentication auth,
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = false) String category
    ) throws AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException {
        // just to ensure a folder really exists
        AcmContainer container = getEcmFileService().getOrCreateContainer(objectType, objectId);

        if (container.getFolder() == null) {
            // not really possible since the cm_folder_id is not nullable.  But we'll account for it anyway
            throw new IllegalStateException("Container '" + container.getId() + "' does not have a folder!");
        }

        // the special category "all" should not be sent to Solr
        category = "all".equals(category) ? null : category;

        AcmCmisObjectList retval = getEcmFileService().listFolderContents(
                auth,
                container,
                category,
                sortBy,
                sortDirection,
                startRow,
                maxRows);

        Map<String, Integer> documentCounts = new HashMap<>();

        documentCounts.put("base", 0);
        ArrayList<AcmCmisObject> folders = new ArrayList<>();

        retval.getChildren().forEach(child -> {
            if (child.getObjectType().equals("folder")) {
                //add to folders
                folders.add(child);
            } else {
                //add to count
                int count = documentCounts.get("base");
                count++;
                documentCounts.put("base", count);
            }
        });

        //loop through folders
        folders.forEach(folder -> {
            // just to ensure a folder really exists
            try {
                AcmContainer subFolderContainer = getEcmFileService().getOrCreateContainer(objectType, folder.getObjectId());
                if (subFolderContainer.getFolder() == null) {
                    // not really possible since the cm_folder_id is not nullable.  But we'll account for it anyway
                    throw new IllegalStateException("Container '" + subFolderContainer.getId() + "' does not have a folder!");
                }

                int count = findDocumentCount(auth, subFolderContainer, folder.getObjectId(), startRow, maxRows, sortBy, sortDirection);
                String subFolderName = folder.getName();
                documentCounts.put(subFolderName, count);

            } catch (AcmCreateObjectFailedException | AcmUserActionFailedException e) {
                if (log.isErrorEnabled()) {
                    log.debug("Folder with id: " + folder.getObjectId() + " could not be retrieved/created");
                }
            }
        });

        return documentCounts;
    }

    private int findDocumentCount(Authentication authentication, AcmContainer container, Long folderId, int startRow, int maxRows, String sortBy, String sortDirection) {
        try {
            AcmCmisObjectList objectList = getEcmFileService().listAllSubFolderChildren(null, authentication, container, folderId, startRow, maxRows, sortBy, sortDirection);
            int count = 0;
            for (AcmCmisObject child : objectList.getChildren()) {
                ArrayList<AcmCmisObject> folders = new ArrayList<>();

                if (child.getType().equals("folder")) {
                    //add to folders
                    folders.add(child);
                } else {
                    //add to count
                    count++;
                }

                for (AcmCmisObject folder : folders) {
                    count += findDocumentCount(authentication, container, folder.getObjectId(), startRow, maxRows, sortBy, sortDirection);
                }
            }

            return count;

        } catch (AcmListObjectsFailedException | AcmObjectNotFoundException e) {
            if (log.isErrorEnabled()) {
                log.debug("Folder with id: " + folderId + " could not be found");
            }
        }
        return 0;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}