package com.armedia.acm.services.dataupdate.dao;

import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.List;

public class GroupUUIDUpdateDao
{
    private final Logger log = LoggerFactory.getLogger(GroupUUIDUpdateDao.class);

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

    @Transactional
    public void deleteGroups(List<AcmGroup> groups)
    {
        groups.forEach(group -> {
            log.debug("Deleting AcmGroup [{}]", group.getName());
            Query deleteQuery = em.createQuery("DELETE FROM AcmGroup g WHERE g.name = :name");
            deleteQuery.setParameter("name", group.getName());
            deleteQuery.setFlushMode(FlushModeType.AUTO);
            deleteQuery.executeUpdate();
            em.detach(group);
        });
    }
}
