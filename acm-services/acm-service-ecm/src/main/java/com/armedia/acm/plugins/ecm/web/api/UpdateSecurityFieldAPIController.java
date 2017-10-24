package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//'api/v1/service/:objectType/:objectId/security-field/:securityFieldValue/'
@Controller
@RequestMapping({ "/api/v1/service/file", "/api/latest/service/file" })

public class UpdateSecurityFieldAPIController
{
    private EcmFileService ecmFileService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'write')")
    @RequestMapping(value = "/{fileId}/security-field/{securityFieldValue}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile updateSecurityField(@PathVariable("fileId") Long fileId, @PathVariable("securityFieldValue") String securityFieldValue)
            throws AcmObjectNotFoundException
    {
        return getEcmFileService().updateSecurityField(fileId, securityFieldValue);
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }
}