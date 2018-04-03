package com.armedia.acm.services.dataupdate.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class ActivitiTaskOwnerUpdateDao
{

    @PersistenceContext
    private EntityManager em;

    public int updateOwnerActivitiTask(String taskOwner, String newTaskOwner)
    {
        Query query = em.createNativeQuery("UPDATE ACT_RU_TASK "
                + "SET OWNER_ = ? "
                + "WHERE OWNER_ = ?");
        query.setParameter(1, newTaskOwner);
        query.setParameter(2, taskOwner);
        return query.executeUpdate();
    }

}
