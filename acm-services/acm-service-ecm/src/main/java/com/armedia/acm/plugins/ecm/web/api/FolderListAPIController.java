package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerFolderDao;
import com.armedia.acm.plugins.ecm.model.AcmCmisFolder;
import com.armedia.acm.plugins.ecm.model.AcmCmisObject;
import com.armedia.acm.plugins.ecm.model.AcmContainerFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class FolderListAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileService ecmFileService;
    private AcmContainerFolderDao containerFolderDao;

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read')")
    @RequestMapping(value = "/folder/{objectType}/{objectId}", method = RequestMethod.GET)
    @ResponseBody
    public AcmCmisFolder listFolderContents(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection
    ) throws AcmListObjectsFailedException, AcmCreateObjectFailedException
    {
        AcmContainerFolder containerFolder = getContainerFolderDao().getOrCreateContainerFolder(objectType, objectId);

        List<AcmCmisObject> children = getEcmFileService().listFolderContents(
                containerFolder.getCmisFolderId(),
                sortBy,
                sortDirection);

        AcmCmisFolder retval = new AcmCmisFolder();
        retval.setContainerObjectId(objectId);
        retval.setContainerObjectType(objectType);
        retval.setCmisFolderId(containerFolder.getCmisFolderId());
        retval.setChildren(children);

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

    public AcmContainerFolderDao getContainerFolderDao()
    {
        return containerFolderDao;
    }

    public void setContainerFolderDao(AcmContainerFolderDao containerFolderDao)
    {
        this.containerFolderDao = containerFolderDao;
    }
}
