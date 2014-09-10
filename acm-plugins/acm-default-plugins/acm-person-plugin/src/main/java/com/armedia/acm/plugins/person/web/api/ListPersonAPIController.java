package com.armedia.acm.plugins.person.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import java.util.List;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class ListPersonAPIController
{
    private PersonAssociationDao personAssociationDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/list/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> findPersonBYAssociation(            
            @PathVariable("parentType") String parentType,
            @PathVariable("parentId") Long parentId                     
            ) throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException
    {
        if ( log.isInfoEnabled() )
        {
            log.info("Finding person by parent id '" + parentId + "'" + "parent type '" + parentType+ "'" );
        }    
    
        if ( (parentType != null ) && ( parentId != null ) )
        {
            try 
            {             
                List<Person> personList = getPersonAssociationDao().findPersonByParentIdAndParentType(parentType, parentId);
                log.debug("personList size " + personList.size());
                
              return personList;
            }
            catch (PersistenceException e)
            {
                throw new AcmListObjectsFailedException("p", e.getMessage(), e);
            }
            
        }
       
        throw new AcmListObjectsFailedException("wrong input", "patenType or parentId are: ", null);  
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
