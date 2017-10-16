package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.service.PersonAssociationEventPublisher;

import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@Controller
@RequestMapping({"/api/v1/plugin/personAssociation", "/api/latest/plugin/personAssociation"})
public class DeletePersonAssocByIdAPIController
{
    private PersonAssociationDao personAssociationDao;
    private PersonAssociationEventPublisher personAssociationEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/delete/{personAssocId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deletePersonById(
            @PathVariable("personAssocId") Long personAssocId
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        log.info("Finding person association by id:'{}'", personAssocId);

        if (personAssocId != null)
        {
            try
            {
                JSONObject objectToReturnJSON = new JSONObject();
                PersonAssociation source = getPersonAssociationDao().find(personAssocId);
                getPersonAssociationDao().deletePersonAssociationById(personAssocId);
                log.info("Deleting person association by id:'{}'", personAssocId);

                getPersonAssociationEventPublisher().publishPersonAssociationDeletedEvent(source);

                objectToReturnJSON.put("deletedPersonAssociationId", personAssocId);
                return objectToReturnJSON.toString();
            } catch (PersistenceException e)
            {
                throw new AcmUserActionFailedException("Delete", "personAssoc", personAssocId, e.getMessage(), e);
            }
        }

        throw new AcmObjectNotFoundException("couldn't find", personAssocId, "person association with this id", null);
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public PersonAssociationEventPublisher getPersonAssociationEventPublisher()
    {
        return personAssociationEventPublisher;
    }

    public void setPersonAssociationEventPublisher(PersonAssociationEventPublisher personAssociationEventPublisher)
    {
        this.personAssociationEventPublisher = personAssociationEventPublisher;
    }

}
