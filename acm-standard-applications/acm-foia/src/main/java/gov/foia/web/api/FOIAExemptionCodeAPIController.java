package gov.foia.web.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.services.exemption.exception.GetExemptionCodeException;
import com.armedia.acm.services.exemption.model.ExemptionCode;

import gov.foia.service.FOIAExemptionService;

/**
 * Created by ana.serafimoska
 */

@Controller
@RequestMapping({ "/api/v1/service/exemption", "/api/latest/service/exemption" })
public class FOIAExemptionCodeAPIController
{

    private FOIAExemptionService foiaExemptionService;

    @RequestMapping(value = "/{parentObjectId}/{parentObjectType}/tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ExemptionCode> getExemptionCodes(@PathVariable Long parentObjectId, @PathVariable String parentObjectType)
            throws GetExemptionCodeException
    {
        return foiaExemptionService.getExemptionCodes(parentObjectId, parentObjectType);
    }

    @RequestMapping(value = "/{parentObjectId}/{parentObjectType}/exemptions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean hasExemptionOnAnyDocumentsOnRequest(@PathVariable Long parentObjectId, @PathVariable String parentObjectType)
    {
        return  foiaExemptionService.hasExemptionOnAnyDocumentsOnRequest(parentObjectId, parentObjectType);
    }

    public FOIAExemptionService getFoiaExemptionService()
    {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService)
    {
        this.foiaExemptionService = foiaExemptionService;
    }
}
