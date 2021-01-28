package com.armedia.acm.plugins.person.service;

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
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.springframework.security.core.Authentication;

import java.util.List;

public interface PersonAssociationService
{
    PersonAssociation savePersonAssociation(PersonAssociation personAssociation, Authentication authentication)
            throws AcmCreateObjectFailedException;

    List<Person> getPersonsInAssociatonsByPersonType(String parentType, Long parentId, String personType);

    /**
     * get associations for person including solr documents for parent
     *
     * @param personId
     *            Person id
     * @param parentType
     *            Parent type
     * @param start
     *            used for paging, from which row to start
     * @param limit
     *            used for paging, how many rows to return
     * @param sort
     *            for which field sorting should be done, default is id
     * @param auth
     *            Authentication
     * @return JSON String of solr response
     */
    String getPersonAssociations(Long personId, String parentType, int start, int limit, String sort, Authentication auth)
            throws AcmObjectNotFoundException;

    /**
     * Get Person association
     *
     * @param id
     *            person association id
     * @param auth
     *            Authentication
     * @return PersonAssociation
     */
    PersonAssociation getPersonAssociation(Long id, Authentication auth);

    /**
     * Delete Person association
     *
     * @param id
     *            person association id
     * @param auth
     *            Authentication
     */
    void deletePersonAssociation(Long id, Authentication auth);
}
