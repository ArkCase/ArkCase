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
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequenceEntityId;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceDao extends AcmAbstractDao<AcmSequenceEntity>
{
    private final Logger log = LogManager.getLogger(getClass());

    @Override
    protected Class<AcmSequenceEntity> getPersistenceClass()
    {
        return AcmSequenceEntity.class;
    }

    public AcmSequenceEntity find(AcmSequenceEntityId id)
    {
        AcmSequenceEntity found = getEm().find(AcmSequenceEntity.class, id);
        return found;
    }

    public AcmSequenceEntity getSequenceEntity(String sequenceName, String sequencePartName, FlushModeType flushModeType)
    {
        AcmSequenceEntity sequenceEntity = null;
        String queryText = "SELECT sequenceEntity " +
                "FROM AcmSequenceEntity sequenceEntity " +
                "WHERE sequenceEntity.sequenceName = :sequenceName " +
                "AND sequenceEntity.sequencePartName = :sequencePartName";

        TypedQuery<AcmSequenceEntity> query = getEm().createQuery(queryText, AcmSequenceEntity.class);
        query.setFlushMode(flushModeType);

        query.setParameter("sequenceName", sequenceName);
        query.setParameter("sequencePartName", sequencePartName);

        try
        {
            sequenceEntity = query.getSingleResult();
        }
        catch (NoResultException nre)
        {
            log.warn("No Sequence with sequence name [{}], sequence part name [{}]", sequenceName,
                    sequencePartName);
        }

        return sequenceEntity;
    }

    @Override
    public String getSupportedObjectType()
    {
        return "ACM_SEQUENCE";
    }

}
