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
import com.armedia.acm.services.sequence.model.AcmSequenceResetId;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceResetDao extends AcmAbstractDao<AcmSequenceReset>
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    protected Class<AcmSequenceReset> getPersistenceClass()
    {
        return AcmSequenceReset.class;
    }

    public AcmSequenceReset find(AcmSequenceResetId id)
    {
        AcmSequenceReset found = getEm().find(AcmSequenceReset.class, id);
        return found;
    }

    public void remove(AcmSequenceReset sequenceReset)
    {
        getEm().remove(sequenceReset);
    }

    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName, Boolean resetExecutedFlag,
            FlushModeType flushModeType)
    {

        String queryText = "SELECT sequenceReset " +
                "FROM AcmSequenceReset sequenceReset " +
                "WHERE sequenceReset.sequenceName = :sequenceName " +
                "AND sequenceReset.sequencePartName = :sequencePartName";
        if (resetExecutedFlag != null)
        {
            queryText += " AND sequenceReset.resetExecutedFlag = :resetExecutedFlag";
        }

        TypedQuery<AcmSequenceReset> query = getEm().createQuery(queryText, AcmSequenceReset.class);
        if (flushModeType != null)
        {
            query.setFlushMode(flushModeType);
        }

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);
        if (resetExecutedFlag != null)
        {
            query.setParameter("resetExecutedFlag", resetExecutedFlag);
        }

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
