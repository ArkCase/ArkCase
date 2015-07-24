package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by manoj.dhungana on 7/23/2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class ListFileFolderByCategoryAPIController {
    private Logger log = LoggerFactory.getLogger(getClass());



    private EcmFileService ecmFileService;
    @RequestMapping(value = "/bycategory/{parentObjectType}/{parentObjectId}", method = RequestMethod.GET)
    @ResponseBody
    public AcmCmisObjectList listFolderContents(
            Authentication auth,
            @PathVariable("parentObjectType") String parentObjectType,
            @PathVariable("parentObjectId") Long parentObjectId,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = true) String category
    ) throws AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmContainer container = getEcmFileService().getOrCreateContainer(parentObjectType, parentObjectId);
        log.info("Cotainer ID : " + container.getId());
        AcmCmisObjectList retval;
        if ( container.getFolder() == null )
        {
            // not really possible since the cm_folder_id is not nullable.  But we'll account for it anyway
            throw new IllegalStateException("Container '" + container.getId() + "' does not have a folder!");
        }

        if ( category != null && !category.isEmpty() )
        {
            log.info("Category provide is: " + category);
            retval = getEcmFileService().listFileFolderByCategory(
                    auth,
                    container,
                    sortBy,
                    sortDirection,
                    startRow,
                    maxRows,
                    category);
        }
        else{
            throw new AcmUserActionFailedException("list objects by category for", parentObjectType, parentObjectId, "Must provide category as request parameter (?category='...')", null);
        }
        return retval;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

}
