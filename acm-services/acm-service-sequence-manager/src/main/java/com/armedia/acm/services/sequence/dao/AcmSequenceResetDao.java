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
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

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
public class AcmSequenceResetDao extends AcmAbstractDao<AcmSequenceReset>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected Class<AcmSequenceReset> getPersistenceClass()
    {
        return AcmSequenceReset.class;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insertSequenceReset(AcmSequenceReset acmSequenceReset)
    {
        String insertSequenceSql = "INSERT INTO acm_sequence_reset " +
                "(cm_sequence_name, cm_sequence_part_name, cm_reset_date, " +
                "cm_reset_repeatable_flag, cm_reset_repeatable_period) " +
                "VALUES (?1,?2,?3,?4,?5)";
        Query insertSequenceQuery = getEm().createNativeQuery(insertSequenceSql);

        insertSequenceQuery.setParameter(1, acmSequenceReset.getSequenceName());
        insertSequenceQuery.setParameter(2, acmSequenceReset.getSequencePartName());
        insertSequenceQuery.setParameter(3, acmSequenceReset.getResetDate());
        insertSequenceQuery.setParameter(4, acmSequenceReset.getResetRepeatableFlag());
        insertSequenceQuery.setParameter(5, acmSequenceReset.getResetRepeatablePeriod());
        return insertSequenceQuery.executeUpdate();
    }

    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName, String resetExecutedFlag)
    {

        String queryText = "SELECT sequenceReset " +
                "FROM AcmSequenceReset sequenceReset " +
                "WHERE sequenceReset.sequenceName = :sequenceName " +
                "AND sequenceReset.sequencePartName = :sequencePartName " +
                "AND sequenceReset.resetExecutedFlag = :resetExecutedFlag";

        TypedQuery<AcmSequenceReset> query = getEm().createQuery(queryText, AcmSequenceReset.class);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);
        query.setParameter("resetExecutedFlag", resetExecutedFlag);

        List<AcmSequenceReset> sequenceResetList = query.getResultList();
        if (null == sequenceResetList)
        {
            sequenceResetList = new ArrayList<AcmSequenceReset>();
        }
        return sequenceResetList;
    }

    @Override
    public String getSupportedObjectType()
    {
        return "ACM_SEQUENCE_RESET";
    }

}
