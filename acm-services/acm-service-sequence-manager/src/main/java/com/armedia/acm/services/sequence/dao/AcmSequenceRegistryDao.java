package com.armedia.acm.services.sequence.dao;

/*-
 * #%L
 * ACM Service: Sequence Manager
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceRegistryDao extends AcmAbstractDao<AcmSequenceRegistry>
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    protected Class<AcmSequenceRegistry> getPersistenceClass()
    {
        return AcmSequenceRegistry.class;
    }

    public Integer removeSequenceRegistry(String sequenceValue)
    {
        String queryText = "DELETE FROM " +
                "AcmSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    public Integer removeSequenceRegistry(String sequenceName, String sequencePartName)
    {
        String queryText = "DELETE FROM " +
                "AcmSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceName = :sequenceName " +
                "AND sequenceRegistry.sequencePartName = :sequencePartName";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);
        return query.executeUpdate();
    }

    public Integer updateSequenceRegistryAsUnused(String sequenceValue)
    {
        String queryText = "UPDATE AcmSequenceRegistry sequenceRegistry " +
                "SET sequenceRegistry.sequencePartValueUsedFlag = false " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    public List<AcmSequenceRegistry> getSequenceRegistryList(String sequenceName, String sequencePartName,
            Boolean sequencePartValueUsedFlag, FlushModeType flushModeType)
    {

        String queryText = "SELECT sequenceRegistry " +
                "FROM AcmSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceName = :sequenceName " +
                "AND sequenceRegistry.sequencePartName = :sequencePartName " +
                "AND sequenceRegistry.sequencePartValueUsedFlag = :sequencePartValueUsedFlag " +
                "ORDER BY sequenceRegistry.sequencePartValue";

        TypedQuery<AcmSequenceRegistry> query = getEm().createQuery(queryText, AcmSequenceRegistry.class);
        query.setFlushMode(flushModeType);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);
        query.setParameter("sequencePartValueUsedFlag", sequencePartValueUsedFlag);

        List<AcmSequenceRegistry> sequenceRegistryList = query.getResultList();
        if (null == sequenceRegistryList)
        {
            sequenceRegistryList = new ArrayList<AcmSequenceRegistry>();
        }
        return sequenceRegistryList;
    }

    public List<AcmSequenceRegistry> getSequenceRegistryList()
    {
        String queryText = "SELECT sequenceRegistry " +
                "FROM AcmSequenceRegistry sequenceRegistry";

        TypedQuery<AcmSequenceRegistry> query = getEm().createQuery(queryText, AcmSequenceRegistry.class);

        List<AcmSequenceRegistry> sequenceRegistryList = query.getResultList();
        if (null == sequenceRegistryList)
        {
            sequenceRegistryList = new ArrayList<AcmSequenceRegistry>();
        }
        return sequenceRegistryList;
    }

    @Override
    public String getSupportedObjectType()
    {
        return "ACM_SEQUENCE_REGISTRY";
    }

}
