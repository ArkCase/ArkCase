package com.armedia.acm.plugins.person.dao;

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


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

public class PersonAssociationDao extends AcmAbstractDao<PersonAssociation>
{
    private transient final Logger LOG = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<PersonAssociation> getPersistenceClass()
    {
        return PersonAssociation.class;
    }

    public List<Person> findPersonByParentIdAndParentType(String parentType, Long parentId)
    {

        Query personInAssociation = getEntityManager().createQuery(
                "SELECT person " + "FROM PersonAssociation personAssociation, " + "Person person " +
                        "WHERE personAssociation.parentType = :parentType " +
                        "AND personAssociation.parentId = :parentId " +
                        "AND personAssociation.person.id = person.id");

        personInAssociation.setParameter("parentType", parentType.toUpperCase());
        personInAssociation.setParameter("parentId", parentId);

        List<Person> retrival = personInAssociation.getResultList();

        return retrival;

    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public Person findPersonByPersonAssociationId(Long personAssociationId)
    {
        Query personInAssociation = getEntityManager().createQuery(
                "SELECT person " +
                        "FROM  PersonAssociation personAssociation, " +
                        "      Person person " +
                        "WHERE personAssociation.id = :personAssociationId " +
                        "AND   personAssociation.person.id = person.id");

        personInAssociation.setParameter("personAssociationId", personAssociationId);

        Person found = (Person) personInAssociation.getSingleResult();

        return found;
    }

    public PersonAssociation findByPersonIdPersonTypeParentIdParentTypeSilent(Long personId, String personType, Long parentId,
            String parentType)
    {
        Query select = getEntityManager().createQuery(
                "SELECT personAssociation " +
                        "FROM  PersonAssociation personAssociation " +
                        "WHERE personAssociation.person.id = :personId " +
                        "AND personAssociation.personType = :personType " +
                        "AND personAssociation.parentId = :parentId " +
                        "AND personAssociation.parentType = :parentType");

        select.setParameter("personId", personId);
        select.setParameter("personType", personType);
        select.setParameter("parentId", parentId);
        select.setParameter("parentType", parentType);

        PersonAssociation retval = null;

        try
        {
            retval = (PersonAssociation) select.getSingleResult();
        }
        catch (NoResultException e1)
        {
            LOG.debug("There is no any PersonAssociation result for personId=" + personId + ", personType=" + personType + ", parentId="
                    + parentId + ", parentType=" + parentType);
        }
        catch (Exception e2)
        {
            LOG.debug("Cannot take PersonAssociation result for personId=" + personId + ", personType=" + personType + ", parentId="
                    + parentId + ", parentType=" + parentType);
        }

        return retval;
    }

    @Transactional
    public void deletePersonAssociationById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT personAssociation " + "FROM  PersonAssociation personAssociation " +
                        "WHERE personAssociation.id = :personAssociationId ");

        queryToDelete.setParameter("personAssociationId", id);

        PersonAssociation personAssociationToBeDeleted = (PersonAssociation) queryToDelete.getSingleResult();
        entityManager.remove(personAssociationToBeDeleted);

    }

    public List<Long> findParentIdByPersonId(String parentType, Long personId)
    {

        Query personInAssociation = getEntityManager().createQuery(
                "SELECT personAssociation.parentId " + "FROM PersonAssociation personAssociation " +
                        "WHERE personAssociation.parentType = :parentType " +
                        "AND personAssociation.person.id = :personId");

        personInAssociation.setParameter("parentType", parentType.toUpperCase());
        personInAssociation.setParameter("personId", personId);

        List<Long> retrival = personInAssociation.getResultList();

        return retrival;

    }
}
