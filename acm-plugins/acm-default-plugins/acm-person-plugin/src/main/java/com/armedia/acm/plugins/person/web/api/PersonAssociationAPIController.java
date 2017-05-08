package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationEventPublisher;
import com.armedia.acm.plugins.person.service.PersonAssociationService;
import com.armedia.acm.plugins.person.service.SavePersonAssociationTransaction;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    private ExecuteSolrQuery executeSolrQuery;

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
                    @RequestParam(value = "person-id") Long personId,
                    @RequestParam(value = "parent-type", required = false) String parentType,
                    @RequestParam(value = "parent-id", required = false) Long parentId,
                    @RequestParam(value = "parent-objects-only", required = false, defaultValue = "false") boolean parentObjectsOnly,
                    @RequestParam(value = "start", required = false, defaultValue = "0") int start,
                    @RequestParam(value = "n", required = false, defaultValue = "10") int n) throws AcmObjectNotFoundException
    {
        StringBuilder query = new StringBuilder();

        if (parentObjectsOnly)
        {
            query.append("{!join from=parent_ref_s to=id}");
        }
        query.append(String.format("object_type_s:PERSON-ASSOCIATION AND child_id_s:%s", personId.toString()));
        if (StringUtils.isNotEmpty(parentType))
        {
            query.append(" AND parent_type_s:" + parentType);
        }
        if (parentId != null && parentId >= 0)
        {
            query.append(" AND parent_id_s:" + parentId);
        }
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query.toString(), start, n, "");
        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("PersonAssociation", null, String.format("Could not execute %s .", query.toString()), e);
        }
    }

    public PersonAssociationService getPersonAssociationService()
    {
        return personAssociationService;
    }

    public void setPersonAssociationService(PersonAssociationService personAssociationService)
    {
        this.personAssociationService = personAssociationService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
