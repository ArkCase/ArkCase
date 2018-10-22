package gov.foia.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.util.List;

import gov.foia.service.SaveFOIARequestService;

/**
 * @author sasko.tanaskoski
 *
 */

@Controller
@RequestMapping({ "/api/v1/plugin/foiarequest", "/api/latest/plugin/foiarequest" })
public class SaveFOIARequestAPIController
{

    private final Logger log = LoggerFactory.getLogger(getClass());
    private SaveFOIARequestService saveFOIARequestService;

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CASE_FILE', 'saveCase')")
    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFile saveFOIARequest(@RequestBody CaseFile in, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        return getSaveFOIARequestService().saveFOIARequest(in, null, session, auth);
    }

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CASE_FILE', 'saveCase')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public CaseFile saveFOIARequestMultipart(@RequestPart(name = "casefile") CaseFile in,
            @RequestPart(name = "files") List<MultipartFile> files, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        return getSaveFOIARequestService().saveFOIARequest(in, files, session, auth);
    }

    /**
     * @return the saveFOIARequestService
     */
    public SaveFOIARequestService getSaveFOIARequestService()
    {
        return saveFOIARequestService;
    }

    /**
     * @param saveFOIARequestService
     *            the saveFOIARequestService to set
     */
    public void setSaveFOIARequestService(SaveFOIARequestService saveFOIARequestService)
    {
        this.saveFOIARequestService = saveFOIARequestService;
    }

}
