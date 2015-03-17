package com.armedia.acm.correspondence.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping( { "/api/v1/service/correspondence", "/api/latest/service/correspondence"} )
public class GenerateCorrespondenceAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private CorrespondenceService correspondenceService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile generateCorrespondence(
            @RequestParam("templateName") String templateName,
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestParam("targetCmisFolderId") String targetCmisFolderId,
            Authentication authentication) throws AcmCreateObjectFailedException
    {
        log.debug("User '" + authentication.getName() + "' is generating template '" + templateName + "'");

        try
        {
            EcmFile retval = getCorrespondenceService().generate(authentication, templateName, parentObjectType, parentObjectId,
                    targetCmisFolderId);
            return retval;
        }
        catch ( AcmCreateObjectFailedException e )
        {
            log.error("Could not add correspondence: " + e.getMessage(), e);
            throw e;
        }
        catch ( IOException e)
        {
            log.error("Could not add correspondence: " + e.getMessage(), e);
            throw new AcmCreateObjectFailedException("correspondence", e.getMessage(), e);
        }
    }

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }
}
