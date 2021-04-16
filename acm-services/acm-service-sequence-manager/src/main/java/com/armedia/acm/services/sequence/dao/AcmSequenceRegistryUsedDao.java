package com.armedia.acm.services.sequence.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistryUsed;

import javax.persistence.Query;

public class AcmSequenceRegistryUsedDao extends AcmAbstractDao<AcmSequenceRegistryUsed>
{

    @Override
    protected Class<AcmSequenceRegistryUsed> getPersistenceClass()
    {
        return AcmSequenceRegistryUsed.class;
    }

    public Integer removeUsedSequenceRegistry(String sequenceValue)
    {
        String queryText = "DELETE FROM " +
                "AcmSequenceRegistryUsed sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    public Integer removeUsedSequenceRegistry(String sequenceName, String sequencePartName)
    {
        String queryText = "DELETE FROM " +
                "AcmSequenceRegistryUsed sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceName = :sequenceName " +
                "AND sequenceRegistry.sequencePartName = :sequencePartName";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);
        return query.executeUpdate();
    }

    public AcmSequenceRegistryUsed getUsedSequenceRegistry(String sequenceValue)
    {
        AcmSequenceRegistryUsed acmSequenceRegistryUsed;
        String queryText = "SELECT sequenceRegistry " +
                "FROM AcmSequenceRegistryUsed sequenceRegistry "+
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);
        query.setParameter("sequenceValue", sequenceValue);

        acmSequenceRegistryUsed = (AcmSequenceRegistryUsed) query.getSingleResult();

        return acmSequenceRegistryUsed;
    }
}
