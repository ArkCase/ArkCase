package com.armedia.acm.services.sequence.dao;

/*-
 * #%L
 * ACM Service: Sequence Manager
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistryUsed;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import javax.persistence.Query;

public class AcmSequenceRegistryUsedDao extends AcmAbstractDao<AcmSequenceRegistryUsed>
{

    @Override
    protected Class<AcmSequenceRegistryUsed> getPersistenceClass()
    {
        return AcmSequenceRegistryUsed.class;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer removeUsedSequenceRegistry(String sequenceValue)
    {
        String queryText = "DELETE FROM " +
                "AcmSequenceRegistryUsed sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceRegistryUsed getUsedSequenceRegistry(String sequenceValue)
    {
        AcmSequenceRegistryUsed acmSequenceRegistryUsed = null;
        String queryText = "SELECT sequenceRegistry " +
                "FROM AcmSequenceRegistryUsed sequenceRegistry "+
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);
        query.setParameter("sequenceValue", sequenceValue);

        try{
            acmSequenceRegistryUsed = (AcmSequenceRegistryUsed) query.getSingleResult();
        }
        catch (NoResultException e){
            return acmSequenceRegistryUsed;
        }
        return acmSequenceRegistryUsed;
    }
}
