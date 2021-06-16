package com.armedia.acm.tool.zylab.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.tool.zylab.model.ZylabMatterCreationStatus;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.Optional;

public class ZylabMatterCreationDao extends AcmAbstractDao<ZylabMatterCreationStatus>
{

    @Override
    protected Class<ZylabMatterCreationStatus> getPersistenceClass()
    {
        return ZylabMatterCreationStatus.class;
    }

    public Optional<ZylabMatterCreationStatus> findByMatterName(String matterName)
    {
        String jpql = "SELECT e FROM ZylabMatterCreationStatus e WHERE e.matterName=:matterName";

        TypedQuery<ZylabMatterCreationStatus> query = getEm().createQuery(jpql, getPersistenceClass());
        query.setParameter("matterName", matterName);

        try
        {
            return Optional.of(query.getSingleResult());
        }
        catch (NoResultException e)
        {
            return Optional.empty();
        }
    }
}