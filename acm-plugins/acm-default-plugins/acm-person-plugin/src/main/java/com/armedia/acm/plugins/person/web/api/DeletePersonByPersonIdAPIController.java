package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class DeletePersonByPersonIdAPIController
{
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
    private PersonEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/delete/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Integer deletePersonById(
            @PathVariable("personId") Long personId,
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding person by id '" + personId + "'");
        }

        try
        {

            
            Integer deletedPerson = getPersonDao().deletePersonById(personId);
                 log.info("delete person by id count", deletedPerson);
            
                  if ( deletedPerson < 1)
                  {
                    throw new AcmObjectNotFoundException("Person", personId, "Object Not Found", null);  
                  }
                  
           return deletedPerson;
        }
        catch (PersistenceException e)
        {
           throw new AcmUserActionFailedException("Delete", "person", personId, e.getMessage(), e);
        }
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    public PersonAssociationDao getPersonAssociationDao() {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao) {
        this.personAssociationDao = personAssociationDao;
    }
}
