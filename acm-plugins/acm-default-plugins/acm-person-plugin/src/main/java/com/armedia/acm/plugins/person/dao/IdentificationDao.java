package com.armedia.acm.plugins.person.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by marjan.stefanoski on 09.12.2014.
 */
public class IdentificationDao extends AcmAbstractDao<Identification> {


    @PersistenceContext
    private EntityManager entityManager;


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    protected Class<Identification> getPersistenceClass() {
        return Identification.class;
    }

}
