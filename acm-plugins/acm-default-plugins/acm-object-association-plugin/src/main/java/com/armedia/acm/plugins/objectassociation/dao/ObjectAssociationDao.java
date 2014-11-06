package com.armedia.acm.plugins.objectassociation.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by armdev on 11/6/14.
 */
public class ObjectAssociationDao extends AcmAbstractDao<ObjectAssociation>
{
    @Override
    protected Class<ObjectAssociation> getPersistenceClass()
    {
        return ObjectAssociation.class;
    }

    @Transactional
    public List<ObjectAssociation> findByParentTypeAndId(String parentType, Long parentId)
    {
        Query findByParentTypeAndId = getEm().createQuery(
                "SELECT e " +
                "FROM ObjectAssociation e " +
                "WHERE e.parentType = :parentType " +
                "AND e.parentId = :parentId " +
                "ORDER BY e.targetName");

        findByParentTypeAndId.setParameter("parentId", parentId);
        findByParentTypeAndId.setParameter("parentType", parentType);

        List<ObjectAssociation> retval = findByParentTypeAndId.getResultList();

        return retval;


    }
}
