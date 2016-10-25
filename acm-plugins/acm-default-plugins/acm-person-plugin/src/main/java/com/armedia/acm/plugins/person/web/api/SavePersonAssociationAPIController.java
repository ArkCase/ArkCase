package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationEventPublisher;
import com.armedia.acm.plugins.person.service.PersonAssociationService;
import com.armedia.acm.plugins.person.service.SavePersonAssociationTransaction;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/api/v1/plugin/personAssociation", "/api/latest/plugin/personAssociation"})
public class SavePersonAssociationAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private PersonAssociationService personAssociationService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonAssociation addPersonAssociation(
            @RequestBody PersonAssociation in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {
        log.trace("Got a personAssociation: {}; person ID: '{}'", in, in.getId());
        log.trace("personAssociation parentType: {}", in.getParentType());

        return getPersonAssociationService().savePersonAssociation(in, auth);

    }

    public PersonAssociationService getPersonAssociationService()
    {
        return personAssociationService;
    }

    public void setPersonAssociationService(PersonAssociationService personAssociationService)
    {
        this.personAssociationService = personAssociationService;
    }
}
