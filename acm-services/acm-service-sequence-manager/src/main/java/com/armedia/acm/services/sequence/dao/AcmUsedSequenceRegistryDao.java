package com.armedia.acm.services.sequence.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.sequence.model.AcmUsedSequenceRegistry;

import javax.persistence.Query;

public class AcmUsedSequenceRegistryDao extends AcmAbstractDao<AcmUsedSequenceRegistry>
{

    @Override
    protected Class<AcmUsedSequenceRegistry> getPersistenceClass()
    {
        return AcmUsedSequenceRegistry.class;
    }

    public Integer removeUsedSequenceRegistry(String sequenceValue)
    {
        String queryText = "DELETE FROM " +
                "AcmUsedSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    public Integer removeUsedSequenceRegistry(String sequenceName, String sequencePartName)
    {
        String queryText = "DELETE FROM " +
                "AcmUsedSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceName = :sequenceName " +
                "AND sequenceRegistry.sequencePartName = :sequencePartName";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);
        return query.executeUpdate();
    }

    public AcmUsedSequenceRegistry getUsedSequenceRegistry(String sequenceValue)
    {
        AcmUsedSequenceRegistry acmUsedSequenceRegistry = null;
        String queryText = "SELECT sequenceRegistry " +
                "FROM AcmUsedSequenceRegistry sequenceRegistry "+
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);
        query.setParameter("sequenceValue", sequenceValue);

        acmUsedSequenceRegistry = (AcmUsedSequenceRegistry) query.getSingleResult();

        return acmUsedSequenceRegistry;
    }
}
