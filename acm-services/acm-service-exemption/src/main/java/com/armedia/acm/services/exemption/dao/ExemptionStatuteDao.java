package com.armedia.acm.services.exemption.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.exemption.model.ExemptionStatute;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ExemptionStatuteDao extends AcmAbstractDao<ExemptionStatute> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<ExemptionStatute> getPersistenceClass()
    {
        return ExemptionStatute.class;
    }
}
