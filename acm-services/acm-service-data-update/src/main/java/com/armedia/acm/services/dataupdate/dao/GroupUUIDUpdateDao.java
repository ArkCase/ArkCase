package com.armedia.acm.services.dataupdate.dao;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class GroupUUIDUpdateDao
{
    @PersistenceContext
    private EntityManager em;

    public List<AcmGroup> findAdHocGroupsWithUUIDByStatus(AcmGroupStatus status)
    {
        TypedQuery<AcmGroup> findQuery = em.createQuery("SELECT ag "
                + "FROM AcmGroup ag "
                + "WHERE ag.type = com.armedia.acm.services.users.model.group.AcmGroupType.ADHOC_GROUP "
                + "AND ag.status = :status "
                + "AND ag.name LIKE '%-UUID-%' "
                + "AND LENGTH(ag.name) > 42", AcmGroup.class);
        findQuery.setParameter("status", status);
        return findQuery.getResultList();
    }

}
