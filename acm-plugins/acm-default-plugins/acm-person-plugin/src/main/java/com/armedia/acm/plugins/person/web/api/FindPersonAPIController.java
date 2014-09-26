package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class FindPersonAPIController
{
    private PersonAssociationDao personAssociationDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/find", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_XML_VALUE
    })
    @ResponseBody
    public Person findPerson(
            // we intend to support other query fields in the future.  Hence we have "required = false"
            // even though assocId is the only supported query field right now.
            @RequestParam(value = "assocId", required = false) Long personAssociationId,
            Authentication authentication
    ) throws AcmObjectNotFoundException, AcmListObjectsFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding person: person association id = " + personAssociationId);
        }

        // when other query fields are added, include them in this call, to verify a valid combination of query
        // fields was passed.
        checkQueryFields(personAssociationId);

        if ( personAssociationId != null )
        {
            try
            {
                return findPersonByPersonAssociationId(personAssociationId, authentication);
            }
            catch (PersistenceException pe)
            {
                throw new AcmObjectNotFoundException("person", personAssociationId, pe.getMessage(), pe);
            }
        }

        throw new AcmListObjectsFailedException("person", "a query must be specified", null);

    }

    private Person findPersonByPersonAssociationId(Long personAssociationId, Authentication authentication)
    {
        return getPersonAssociationDao().findPersonByPersonAssociationId(personAssociationId);
    }

    private void checkQueryFields(Long personAssociationId) throws AcmListObjectsFailedException
    {
        if ( personAssociationId == null )
        {
            throw new AcmListObjectsFailedException("person", "association id must be specified", null);
        }
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }
}
