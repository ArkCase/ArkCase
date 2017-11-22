package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

/**
 * Created by manoj.dhungana on 7/23/2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class ListFileFolderByCategoryAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private ArkPermissionEvaluator arkPermissionEvaluator;

    @RequestMapping(value = "/bycategory/{parentObjectType}/{parentObjectId}", method = RequestMethod.GET)
    @ResponseBody
    public AcmCmisObjectList listFolderContents(Authentication auth, @PathVariable("parentObjectType") String parentObjectType,
            @PathVariable("parentObjectId") Long parentObjectId,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = true) String category)
            throws AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException, AcmAccessControlException
    {
        AcmContainer container = getEcmFileService().getOrCreateContainer(parentObjectType, parentObjectId);
        log.info("Cotainer ID : " + container.getId());
        AcmCmisObjectList retval;
        if (container.getFolder() == null)
        {
            // not really possible since the cm_folder_id is not nullable. But we'll account for it anyway
            throw new IllegalStateException("Container '" + container.getId() + "' does not have a folder!");
        }

        if (!getArkPermissionEvaluator().hasPermission(auth, container.getFolder().getId(), "FOLDER", "read|group-read|write|group-write"))
        {
            throw new AcmAccessControlException(Arrays.asList(""),
                    "The user {" + auth.getName() + "} is not allowed to read from folder with id=" + container.getFolder().getId());
        }

        if (category != null && !category.isEmpty())
        {
            log.info("Category provide is: " + category);
            retval = getEcmFileService().listFileFolderByCategory(auth, container, sortBy, sortDirection, startRow, maxRows, category);
        }
        else
        {
            throw new AcmUserActionFailedException("list objects by category for", parentObjectType, parentObjectId,
                    "Must provide category as request parameter (?category='...')", null);
        }
        return retval;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public ArkPermissionEvaluator getArkPermissionEvaluator()
    {
        return arkPermissionEvaluator;
    }

    public void setArkPermissionEvaluator(ArkPermissionEvaluator arkPermissionEvaluator)
    {
        this.arkPermissionEvaluator = arkPermissionEvaluator;
    }

}
