package gov.foia.web.api;

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import gov.foia.service.RequestResponseFolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class RequestResponseFolderAPIController
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private RequestResponseFolderService requestResponseFolderService;

    @RequestMapping(value = "/{caseId}/compressAndSendResponseFolder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void compressAndSendResponseFolderToPortal(@PathVariable("caseId") Long requestId, Authentication authentication) throws AcmObjectNotFoundException, AcmUserActionFailedException, ConversionException, AcmFolderException {
        try
        {
            log.debug("Trying to Compress and Send the Response folder for the request [{}] to Portal", requestId);
            getRequestResponseFolderService().compressAndSendResponseFolderToPortal(requestId, authentication.getName());
        }
        catch (ConversionException | AcmUserActionFailedException | AcmFolderException | AcmObjectNotFoundException e)
        {
            log.error("Failed to Compress and Send the Response folder for the request [{}] to Portal", requestId, e);
            throw e;
        }
    }

    public RequestResponseFolderService getRequestResponseFolderService()
    {
        return requestResponseFolderService;
    }

    public void setRequestResponseFolderService(RequestResponseFolderService requestResponseFolderService)
    {
        this.requestResponseFolderService = requestResponseFolderService;
    }
}
