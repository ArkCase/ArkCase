package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonIdentification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by marjan.stefanoski on 09.12.2014.
 */
public class PersonIdentificationDao extends AcmAbstractDao<PersonIdentification> {


    @PersistenceContext
    private EntityManager entityManager;


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected Class<PersonIdentification> getPersistenceClass() {
        return PersonIdentification.class;
    }

}
