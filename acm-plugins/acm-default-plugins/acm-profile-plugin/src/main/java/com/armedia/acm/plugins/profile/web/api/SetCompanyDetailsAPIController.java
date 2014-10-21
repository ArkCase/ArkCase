package com.armedia.acm.plugins.profile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.profile.model.OrganizationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/profile/companyDetails/set","/api/latest/plugin/profile/companyDetails/set"})
public class SetCompanyDetailsAPIController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public OrganizationDetails setCompanyDetails(
            @RequestBody OrganizationDetails in,
            Authentication auth
    ) throws AcmCreateObjectFailedException {


        return null;
    }

}
