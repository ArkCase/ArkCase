package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.PersonContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


public class PersonContactDao extends AcmAbstractDao<PersonContact> {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    protected Class<PersonContact> getPersistenceClass() {
        return PersonContact.class;
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

}
