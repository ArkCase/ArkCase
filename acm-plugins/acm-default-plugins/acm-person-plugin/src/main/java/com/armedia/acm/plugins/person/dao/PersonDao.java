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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class PersonDao extends AcmAbstractDao<Person>
{

    private Logger LOG = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<Person> getPersistenceClass()
    {
        return Person.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return "PERSON";
    }

    @Transactional
    public void deletePersonById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT person " + "FROM Person person " +
                        "WHERE person.id = :personId");
        queryToDelete.setParameter("personId", id);

        Person personToBeDeleted = (Person) queryToDelete.getSingleResult();
        entityManager.remove(personToBeDeleted);

    }

    public List<Person> findByNameOrContactValue(String name, String contactValue)
    {
        List<Person> result = new ArrayList<>();

        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<Person> query = builder.createQuery(Person.class);
        Root<Person> person = query.from(Person.class);

        query.select(person);

        query.where(
                builder.or(
                        builder.like(builder.lower(person.<String> get("givenName")), "%" + name.toLowerCase() + "%"),
                        builder.like(builder.lower(person.<String> get("familyName")), "%" + name.toLowerCase() + "%")),
                builder.and(
                        builder.like(builder.lower(person.get("contactMethods").<String> get("value")),
                                "%" + contactValue.toLowerCase() + "%")),
                builder.and(
                        builder.equal(person.get("contactMethods").<String> get("status"), "ACTIVE")),
                builder.and(
                        builder.equal(person.<String> get("status"), "ACTIVE")));

        TypedQuery<Person> dbQuery = getEm().createQuery(query);

        try
        {
            result = dbQuery.getResultList();
        }
        catch (Exception e)
        {
            LOG.info("There is no any results.");
        }

        return result;
    }

    public Person findByPersonId(Long id)
    {
        return getEntityManager().find(Person.class, id);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

}
