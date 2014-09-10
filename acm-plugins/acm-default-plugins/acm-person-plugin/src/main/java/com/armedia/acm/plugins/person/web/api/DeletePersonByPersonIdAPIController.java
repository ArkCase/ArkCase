package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class DeletePersonByPersonIdAPIController
{
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
   
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/delete/{personId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deletePersonById(
            @PathVariable("personId") Long personId,
            Authentication authentication           
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding person by id '" + personId + "'");
        }
        
        if ( personId != null )
        {
            try
            {   
                getPersonDao().deletePersonById(personId);
                
                return "Deleting a person is successful";
            }
            catch (PersistenceException e)
            {
                throw new AcmUserActionFailedException("Delete", "person", personId, e.getMessage(), e);
            } 
        }
        
       throw new AcmObjectNotFoundException("couldn't find", personId, "person with this id", null);
    }
    
    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao) 
    {
        this.personDao = personDao;
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
