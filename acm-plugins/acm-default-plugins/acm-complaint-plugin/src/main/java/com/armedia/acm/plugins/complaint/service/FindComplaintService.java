package com.armedia.acm.plugins.complaint.service;

import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.armedia.acm.plugins.complaint.model.Complaint;

/**
 * Created by jwu on 5/6/14.
 */
public class FindComplaintService
{
    @PersistenceContext
    private EntityManager entityManager;

    public List<Complaint> listComplaint() {
        TypedQuery<Complaint> query = getEntityManager().createQuery("SELECT c FROM Complaint c", Complaint.class);
        List<Complaint> results = query.getResultList();
        return results;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
