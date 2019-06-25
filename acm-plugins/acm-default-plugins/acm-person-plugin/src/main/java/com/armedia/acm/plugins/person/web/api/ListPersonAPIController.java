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


import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/person", "/api/latest/plugin/person" })
public class ListPersonAPIController
{
    private PersonAssociationDao personAssociationDao;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/list/{parentType}/{parentId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Person> findPersonBYAssociation(
            @PathVariable("parentType") String parentType,
            @PathVariable("parentId") Long parentId)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, AcmListObjectsFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Finding person by parent id '" + parentId + "'" + "parent type '" + parentType + "'");
        }

        if ((parentType != null) && (parentId != null))
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
