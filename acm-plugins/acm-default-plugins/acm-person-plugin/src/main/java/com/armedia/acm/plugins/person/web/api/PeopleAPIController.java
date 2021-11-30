package com.armedia.acm.plugins.person.web.api;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.UploadImageRequest;
import com.armedia.acm.plugins.person.service.PersonService;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import javax.persistence.PersistenceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = { "/api/v1/plugin/people", "/api/latest/plugin/people" })
public class PeopleAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private PersonService personService;
    private ExecuteSolrQuery executeSolrQuery;

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'PERSON', 'editPerson')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Person upsertPerson(@RequestBody Person in, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        try
        {
            log.debug("Persist a Person: [{}];", in);
            return personService.savePerson(in, auth);
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            log.error("Error while saving Person: [{}]", in, e);
            throw new AcmCreateObjectFailedException("Person", e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Person insertPersonMultipart(@RequestPart(name = "person") Person in,
            @RequestPart(name = "pictures", required = false) List<MultipartFile> pictures, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        if (pictures == null)
        {
            pictures = new ArrayList<>();
        }
        try
        {
            log.debug("Persist a Person: [{}];", in);
            return personService.savePerson(in, pictures, auth);
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            log.error("Error while saving Person: [{}]", in, e);
            throw new AcmCreateObjectFailedException("Person", e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/queryByIds", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
        public String findPeopleByIds(Authentication auth,
              @RequestParam(value = "queryIds", required = false, defaultValue = "0") String personIds,
              @RequestParam(value = "start", required = false, defaultValue = "0") int start,
              @RequestParam(value = "n", required = false, defaultValue = "10") int n,
              @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws SolrException, AcmObjectNotFoundException {

            return personService.getPeopleByIds(auth,personIds,start,n,s);
        };

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

        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve people.", e);
        }
    }

    @RequestMapping(value = "/queryByEmail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getPeopleByEmail(Authentication auth,
            @RequestParam(value = "emailAddress") String emailAddress,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "100") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s)
            throws AcmObjectNotFoundException
    {
        String contactMethodJoin = "{!join from=id to=contact_method_ss}value_parseable:\"" + emailAddress + "\"";

        String query = String.format("object_type_s:PERSON AND %s", contactMethodJoin);
        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve people.", e);
        }
    }

    @PreAuthorize("hasPermission(#personId, 'PERSON', 'viewPersonPage')")
    @RequestMapping(value = "/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public Person getPerson(Authentication auth, @PathVariable("personId") Long personId) throws AcmObjectNotFoundException
    {
        try
        {
            Person person = personService.get(personId);
            return person;
        }
        catch (Exception e)
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

        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve people.", e);
        }
    }

    @PreAuthorize("hasPermission(#personId, 'PERSON', 'editPerson')")
    @RequestMapping(value = "/{personId}/images", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity uploadImage(@PathVariable("personId") Long personId, @RequestPart("data") UploadImageRequest data,
            @RequestPart(value = "file", required = false) MultipartFile image, Authentication auth) throws AcmCreateObjectFailedException,
            AcmUpdateObjectFailedException, IOException, AcmUserActionFailedException, AcmObjectNotFoundException, AcmFileTypesException
    {
        Person person = personService.get(personId);
        try
        {
            log.debug("Insert Image for a Person: [{}];", personId);

            EcmFile uploadedFile = personService.insertImageForPerson(person, image, data.isDefault(), data.getDescription(), auth);

            return new ResponseEntity<>(uploadedFile, HttpStatus.OK);
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            log.error("Error while saving Person: [{}]", person, e);
            throw new AcmCreateObjectFailedException("Person", e.getMessage(), e);
        }
    }

    @PreAuthorize("hasPermission(#personId, 'PERSON', 'editPerson')")
    @RequestMapping(value = "/{personId}/images", method = RequestMethod.PUT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity saveImage(@PathVariable("personId") Long personId, @RequestPart("data") UploadImageRequest data,
            @RequestPart(value = "file", required = false) MultipartFile image, Authentication auth) throws AcmCreateObjectFailedException,
            AcmUpdateObjectFailedException, IOException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        try
        {
            log.debug("Save Image for a Person: [{}];", personId);

            personService.saveImageForPerson(personId, image, data.isDefault(), data.getEcmFile(), auth);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (PipelineProcessException | PersistenceException | AcmFileTypesException e)
        {
            log.error("Error while saving Person with id: [{}]", personId, e);
            throw new AcmCreateObjectFailedException("Person", e.getMessage(), e);
        }
    }

    @PreAuthorize("hasPermission(#personId, 'PERSON', 'editObject')")
    @RequestMapping(value = "/{personId}/images/changeImageDescription/{imageId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile changeImageDescription(@PathVariable("personId") Long personId, @PathVariable("imageId") Long imageId,
            @RequestBody UploadImageRequest data, Authentication auth)
            throws AcmUserActionFailedException, AcmObjectNotFoundException, AcmUpdateObjectFailedException, PipelineProcessException,
            AcmCreateObjectFailedException
    {
        log.debug("Changing description for Image with id: [{}];", imageId);
        return personService.changeDescriptionForImage(personId, imageId, data.isDefault(), data.getDescription(), auth);
    }

    @PreAuthorize("hasPermission(#personId, 'PERSON', 'editPerson')")
    @RequestMapping(value = "/{personId}/images/{imageId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity deleteImage(@PathVariable("personId") Long personId, @PathVariable("imageId") Long imageId, Authentication auth)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
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
        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null,
                    String.format("Could not retrieve %s for person id[%s]", objectType, personId), e);
        }
    }

    public PersonService getPersonService(){
        return personService;
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
