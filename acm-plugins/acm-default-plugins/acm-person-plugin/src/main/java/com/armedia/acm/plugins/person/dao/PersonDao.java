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
import com.armedia.acm.services.config.lookups.service.LookupDao;
import com.armedia.acm.services.labels.service.TranslationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDao extends AcmAbstractDao<Person>
{

    private Logger LOG = LogManager.getLogger(getClass());
    @Autowired
    private TranslationService translationService;
    @Autowired
    private LookupDao lookupDao;

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

    @Transactional
    public void updatePersonClass(Long id, String className)
    {
        Query queryToUpdate = getEntityManager().createQuery(
                "UPDATE Person " +
                        "SET className = :newClassName WHERE id = :personId");
        queryToUpdate.setParameter("personId", id);
        queryToUpdate.setParameter("newClassName", className);

        queryToUpdate.executeUpdate();
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

    public Optional<Person> findByEmail(String emailAddress)
    {
        List<Person> resultList = getEntityManager().createQuery("SELECT p FROM Person p WHERE p.defaultEmail.value = :emailAddress")
                .setParameter("emailAddress", emailAddress).getResultList();
        if (resultList.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(resultList.get(0));
        }
    }

    public Person findAnonymousPerson()
    {
        Query query = getEntityManager().createQuery(
                "SELECT p From Person p " +
                        "WHERE p.givenName like 'Anonymous' " +
                        "AND p.familyName like 'Anonymous' " +
                        "AND p.anonymousFlag = true ");
        List<Person> result = query.getResultList();
        if(result.isEmpty()){
            return null;
        }
        else {
            return result.get(0);
        }
    }

    public Person findByLdapUserId(String ldapUserId)
    {
        Query query = getEntityManager().createQuery(
                "SELECT p From Person p WHERE p.ldapUserId = :ldapUserId");

        query.setParameter("ldapUserId", ldapUserId);

        try
        {
            return (Person) query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.debug("Person with ldapUserId: [{}] not found.", ldapUserId);
            return null;
        }
    }

    @PostConstruct
    public void postConstruct()
    {
        Person.setLookupDao(lookupDao);
        Person.setTranslationService(translationService);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

}
