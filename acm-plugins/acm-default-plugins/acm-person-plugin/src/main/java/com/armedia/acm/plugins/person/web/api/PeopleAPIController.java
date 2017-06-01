package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.UploadImageRequest;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import com.armedia.acm.plugins.person.service.PersonService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = { "/api/v1/plugin/people", "/api/latest/plugin/people" })
public class PeopleAPIController
{

    private Logger log = LoggerFactory.getLogger(getClass());

    private PersonService personService;
    private ExecuteSolrQuery executeSolrQuery;
    private PersonEventPublisher personEventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person upsertPerson(@RequestBody Person in, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        log.debug("Persist a Person: [{}];", in);

        boolean isNew = in.getId() == null;

        Person person = personService.createPerson(in, auth);
        getPersonEventPublisher().publishPersonUpsertEvent(person, isNew, true);
        return person;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Person insertPersonMultipart(@RequestPart(name = "person") Person in,
            @RequestPart(name = "pictures") List<MultipartFile> pictures, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        log.debug("Persist a Person: [{}];", in);

        boolean isNew = in.getId() == null;
        Person person = personService.createPerson(in, pictures, auth);
        getPersonEventPublisher().publishPersonUpsertEvent(person, isNew, true);
        return person;

    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPeople(Authentication auth, @RequestParam(value = "start", required = false, defaultValue = "0") int start,
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

    @RequestMapping(value = "/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person getPerson(Authentication auth, @PathVariable("personId") Long personId) throws AcmObjectNotFoundException
    {
        try
        {
            Person person = personService.get(personId);
            getPersonEventPublisher().publishPersonViewedEvent(person, true);
            return person;
        } catch (Exception e)
        {
            log.error("Error while retrieving Person with id: [{}]", personId, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve person.", e);
        }

    }

    @RequestMapping(value = "/{personId}/images", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getImagesForPerson(Authentication auth, @PathVariable("personId") Long personId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        log.debug("Get images for Person: [{}];", personId);

        String query = String.format("object_type_s:FILE AND parent_ref_s:%s-PERSON&sort=title_parseable %s", personId.toString(), s);
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");

        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve people.", e);
        }

    }

    @RequestMapping(value = "/{personId}/images", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity uploadImage(@PathVariable("personId") Long personId, @RequestPart("data") UploadImageRequest data,
            @RequestPart(value = "file", required = false) MultipartFile image, Authentication auth)
            throws AcmCreateObjectFailedException, IOException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        log.debug("Insert Image for a Person: [{}];", personId);

        personService.insertImageForPerson(personId, image, data.isDefault(), data.getDescription(), auth);
        getPersonEventPublisher().publishPersonImageEvent(personService.get(personId), true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{personId}/images", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity saveImage(@PathVariable("personId") Long personId, @RequestPart("data") UploadImageRequest data,
            @RequestPart(value = "file", required = false) MultipartFile image, Authentication auth)
            throws AcmCreateObjectFailedException, IOException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        log.debug("Save Image for a Person: [{}];", personId);

        personService.saveImageForPerson(personId, image, data.isDefault(), data.getEcmFile(), auth);
        getPersonEventPublisher().publishPersonImageEvent(personService.get(personId), true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{personId}/images/{imageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteImage(@PathVariable("personId") Long personId, @PathVariable("imageId") Long imageId, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {

        log.debug("Delete Image for a Person: [{}];", personId);

        personService.deleteImageForPerson(personId, imageId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{personId}/associations/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getChildObjects(Authentication auth, @PathVariable("personId") Long personId,
            @PathVariable("objectType") String objectType, @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n) throws AcmObjectNotFoundException
    {
        String query = String.format(
                "{!join from=parent_ref_s to=id}object_type_s:PERSON-ASSOCIATION AND parent_type_s:%s AND child_id_s:%s", objectType,
                personId.toString());
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null,
                    String.format("Could not retrieve %s for person id[%s]", objectType, personId).toString(), e);
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

    /**
     * @return the personEventPublisher
     */
    public PersonEventPublisher getPersonEventPublisher()
    {
        return personEventPublisher;
    }

    /**
     * @param personEventPublisher
     *            the personEventPublisher to set
     */
    public void setPersonEventPublisher(PersonEventPublisher personEventPublisher)
    {
        this.personEventPublisher = personEventPublisher;
    }
}
