package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping({"/api/v1/plugin/personAssociation",
        "/api/latest/plugin/personAssociation",
        "/api/v1/plugin/person-associations",
        "/api/latest/plugin/person-associations"
})
public class PersonAssociationAPIController
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
        log.trace("Got a personAssociation: {}; person association ID: '{}'", in, in.getId());
        log.trace("personAssociation parentType: {}", in.getParentType());

        return getPersonAssociationService().savePersonAssociation(in, auth);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String
    getChildObjects(Authentication auth,
                    @RequestParam(value = "person-id", required = true) Long personId,
                    @RequestParam(value = "parent-type", required = true) String parentType,
                    @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                    @RequestParam(value = "n", required = false, defaultValue = "10") int n,
                    @RequestParam(value = "sort", required = false, defaultValue = "id asc") String sort) throws AcmObjectNotFoundException
    {
        return personAssociationService.getPersonAssociations(personId, parentType, start, n, sort, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PersonAssociation
    getPersonAssociation(Authentication auth,
                         @PathVariable Long id) throws AcmObjectNotFoundException
    {
        return personAssociationService.getPersonAssociation(id, auth);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void
    deletePersonAssociation(Authentication auth,
                            @PathVariable Long id) throws AcmObjectNotFoundException
    {
        personAssociationService.deletePersonAssociation(id, auth);
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
