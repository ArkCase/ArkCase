package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.service.PersonEventPublisher;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class ListPersonAPIController
{
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
    private PersonEventPublisher eventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/list/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> findPersonBYAssociation(
            @PathVariable("parentId") Long parentId,
            @PathVariable("parentType") String parentType,
            Authentication authentication,
            HttpSession session,
            HttpServletResponse response
    ) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding person by parent id '" + parentId + "'" + "parent type '" + parentType+ "'" );
        }
        try 
        {
            List <Person> listOfPerson = getPersonAssociationDao().findPersonByParentIdAndParentType(parentType, parentId);
            
            if ( listOfPerson == null)
            {
                throw new AcmListObjectsFailedException("no Person found in associaton", "Object Not Found", null);
            }
            
            for ( Person pers :listOfPerson)
            {
            raiseEvent(authentication, session, pers, true);
            }
            return listOfPerson;
            
           
        }catch (PersistenceException e)
        {
            log.error("Could not list Persons: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("p", e.getMessage(), e);
        }

    }

    protected void raiseEvent(Authentication authentication, HttpSession session,Person person, boolean succeeded)
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getEventPublisher().publishfindPersonByParentIdAndParentType(person, authentication, ipAddress, succeeded);
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

    public PersonEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(PersonEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    
}
