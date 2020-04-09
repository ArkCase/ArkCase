package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.exception.EcmFileLinkException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.LinkTargetFileDTO;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ana.serafimoska
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class GetLinkTargetEcmFileAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService fileService;

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/file/{fileId}/targetLink", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public LinkTargetFileDTO getLinkTargetEcmFile(@PathVariable("fileId") Long fileId)
            throws EcmFileLinkException, AcmObjectNotFoundException
    {
        EcmFile ecmFile = getFileService().findById(fileId);
        LinkTargetFileDTO linkTargetFileDTO = new LinkTargetFileDTO();
        if (ecmFile == null)
        {
            throw new AcmObjectNotFoundException(EcmFileConstants.OBJECT_FILE_TYPE, fileId, "File not found", null);
        }

        linkTargetFileDTO = getFileService().getLinkTargetFileInfo(ecmFile);

        return linkTargetFileDTO;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }
}