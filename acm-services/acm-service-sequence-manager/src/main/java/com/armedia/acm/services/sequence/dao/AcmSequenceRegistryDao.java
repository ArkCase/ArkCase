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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected Class<AcmSequenceRegistry> getPersistenceClass()
    {
        return AcmSequenceRegistry.class;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int removeSequence(String sequenceValue)
    {
        String queryText = "DELETE FROM " +
                "AcmSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int removeBySequenceAndPartName(String sequenceName, String sequencePartName)
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateSequenceAsUnused(String sequenceValue)
    {
        String queryText = "UPDATE AcmSequenceRegistry sequenceRegistry " +
                "SET sequenceRegistry.sequencePartValueUsedFlag = 'false' " +
                "WHERE sequenceRegistry.sequenceValue = :sequenceValue";

        Query query = getEm().createQuery(queryText);

        query.setParameter("sequenceValue", sequenceValue);
        return query.executeUpdate();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insertSequence(AcmSequenceRegistry acmSequenceRegistry)
    {
        String insertSequenceSql = "INSERT INTO acm_sequence_registry " +
                "(cm_sequence_value, cm_sequence_name, cm_sequence_part_name, cm_sequence_part_value) " +
                "VALUES (?1,?2,?3,?4)";
        Query insertSequenceQuery = getEm().createNativeQuery(insertSequenceSql);

        insertSequenceQuery.setParameter(1, acmSequenceRegistry.getSequenceValue());
        insertSequenceQuery.setParameter(2, acmSequenceRegistry.getSequenceName());
        insertSequenceQuery.setParameter(3, acmSequenceRegistry.getSequencePartName());
        insertSequenceQuery.setParameter(4, acmSequenceRegistry.getSequencePartValue());
        return insertSequenceQuery.executeUpdate();
    }

    public List<AcmSequenceRegistry> getSequenceRegistryListBySequenceAndPartName(String sequenceName, String sequencePartName)
    {

        String queryText = "SELECT sequenceRegistry " +
                "FROM AcmSequenceRegistry sequenceRegistry " +
                "WHERE sequenceRegistry.sequenceName = :sequenceName " +
                "AND sequenceRegistry.sequencePartName = :sequencePartName " +
                "AND sequenceRegistry.sequencePartValueUsedFlag = 'false' " +
                "ORDER BY sequenceRegistry.sequencePartValue";

        TypedQuery<AcmSequenceRegistry> query = getEm().createQuery(queryText, AcmSequenceRegistry.class);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);

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
