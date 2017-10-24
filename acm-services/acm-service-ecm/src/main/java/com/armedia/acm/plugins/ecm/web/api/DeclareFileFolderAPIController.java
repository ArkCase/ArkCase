package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.FileFolderDeclareDTO;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.dataaccess.service.impl.ArkPermissionEvaluator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({ "/api/latest/service/ecm", "/api/v1/service/ecm" })

public class DeclareFileFolderAPIController implements ApplicationEventPublisherAware
{

    private EcmFileService ecmFileService;
    private Logger log = LoggerFactory.getLogger(getClass());

    private ApplicationEventPublisher applicationEventPublisher;
    private ArkPermissionEvaluator arkPermissionEvaluator;

    @RequestMapping(value = "/declare/{parentObjectType}/{parentObjectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody

    public List<FileFolderDeclareDTO> declareFileFolder(@PathVariable("parentObjectType") String parentObjectType,
            @PathVariable("parentObjectId") Long parentObjectId, @RequestBody List<FileFolderDeclareDTO> in, Authentication authentication)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmListObjectsFailedException,
            AcmAccessControlException
    {

        if (log.isInfoEnabled())
        {
            log.info("Attempting to declare file or folder..");
        }
        for (FileFolderDeclareDTO fileFolderDeclareDTO : in)
        {
            String type = fileFolderDeclareDTO.getType();
            if (null != type && !type.isEmpty())
            {
                switch (type)
                {
                case "FILE":
                    Long fileId = fileFolderDeclareDTO.getId();
                    if (!getArkPermissionEvaluator().hasPermission(authentication, fileId, "FILE", "read"))
                    {
                        throw new AcmAccessControlException(Arrays.asList(""),
                                "The user {" + authentication.getName() + "} is not allowed to read from file with id=" + fileId);
                    }
                    getEcmFileService().declareFileAsRecord(fileId, authentication);
                    break;
                case "FOLDER":
                    Long folderId = fileFolderDeclareDTO.getId();
                    if (!getArkPermissionEvaluator().hasPermission(authentication, folderId, "FOLDER", "read"))
                    {
                        throw new AcmAccessControlException(Arrays.asList(""),
                                "The user {" + authentication.getName() + "} is not allowed to read from folder with id=" + folderId);
                    }
                    getEcmFileService().declareFolderAsRecord(folderId, authentication, parentObjectType, parentObjectId);
                    break;
                default:
                    break;
                }
            }
        }
        return in;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
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