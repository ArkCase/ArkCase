/**
 * 
 */
package com.armedia.acm.plugins.casefile.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class ChangeCaseStatusDao extends AcmAbstractDao<ChangeCaseStatus>
{
    private Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<ChangeCaseStatus> getPersistenceClass()
    {
        return ChangeCaseStatus.class;
    }

    @Transactional
    public int delete(List<AcmParticipant> participants)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();

        CriteriaDelete<AcmParticipant> delete = builder.createCriteriaDelete(AcmParticipant.class);
        Root<AcmParticipant> acmParticipant = delete.from(AcmParticipant.class);

        List<Long> ids = new ArrayList<>();
        if (null != participants && participants.size() > 0)
        {

            for (AcmParticipant participant : participants)
            {
                ids.add(participant.getId());
            }

        }

        delete.where(
                acmParticipant.<Long> get("id").in(ids));

        Query query = getEm().createQuery(delete);

        return query.executeUpdate();
    }

    public ChangeCaseStatus findByCaseId(Long caseId)
    {
        ChangeCaseStatus result = null;

        CriteriaBuilder builder = getEm().getCriteriaBuilder();

        CriteriaQuery<ChangeCaseStatus> query = builder.createQuery(ChangeCaseStatus.class);
        Root<ChangeCaseStatus> changeCaseStatus = query.from(ChangeCaseStatus.class);

        query.select(changeCaseStatus);

        query.where(
                builder.and(
                        builder.equal(changeCaseStatus.<Long> get("caseId"), caseId)),
                builder.and(
                        builder.equal(changeCaseStatus.<String> get("status"), "ACTIVE")));

        TypedQuery<ChangeCaseStatus> dbQuery = getEm().createQuery(query);

        try
        {
            result = dbQuery.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.info("There is no any results.");
        }

        return result;
    }
}
