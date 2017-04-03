package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Controller
@RequestMapping(value = {"/api/v1/plugin/people", "/api/latest/plugin/people"})
public class PeopleAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private PersonService personService;
    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person upsertPerson(
            @RequestBody Person in,
            Authentication auth
    ) throws AcmCreateObjectFailedException
    {

        log.debug("Persist a Person: [{}];", in);

        throw new NotImplementedException();

    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String
    getPeople(Authentication auth,
              @RequestParam(value = "start", required = false, defaultValue = "0") int start,
              @RequestParam(value = "n", required = false, defaultValue = "10") int n,
              @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:PERSON AND -parent_id_s:*&sort=title_parseable %s", s);
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");

        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve people.", e);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person
    getPerson(Authentication auth,
              @PathVariable("id") Long personId) throws AcmObjectNotFoundException
    {
        try
        {
            return personService.get(personId);
        } catch (Exception e)
        {
            log.error("Error while retrieving Person with id: [{}]", personId, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve person.", e);
        }

    }

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
