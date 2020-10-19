package gov.foia.web.api;


import com.armedia.acm.services.exemption.exception.GetExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import gov.foia.service.FOIAExemptionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/exemption/statute", "/api/latest/service/exemption/statute" })
public class FOIAExemptionStatuteAPIController {

    private final Logger log = LogManager.getLogger(getClass());
    private FOIAExemptionService foiaExemptionService;

    @RequestMapping(value = "/{parentObjectId}/{parentObjectType}/tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ExemptionStatute> getExemptionStatutes(@PathVariable Long parentObjectId, @PathVariable String parentObjectType)
            throws GetExemptionStatuteException {
        return getFoiaExemptionService().getExemptionStatutes(parentObjectId, parentObjectType);
    }

    public FOIAExemptionService getFoiaExemptionService() {
        return foiaExemptionService;
    }

    public void setFoiaExemptionService(FOIAExemptionService foiaExemptionService) {
        this.foiaExemptionService = foiaExemptionService;
    }
}
