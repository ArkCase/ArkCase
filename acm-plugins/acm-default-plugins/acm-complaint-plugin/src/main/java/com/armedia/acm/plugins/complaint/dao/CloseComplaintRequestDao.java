package com.armedia.acm.plugins.complaint.dao;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import com.armedia.acm.plugins.complaint.model.CloseComplaintConstants;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
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
 * Created by armdev on 10/17/14.
 */
public class CloseComplaintRequestDao extends AcmAbstractDao<CloseComplaintRequest>
{
    private Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<CloseComplaintRequest> getPersistenceClass()
    {
        return CloseComplaintRequest.class;
    }

    public CloseComplaintRequest findByComplaintId(Long complaintId)
    {

        CloseComplaintRequest result = null;

        CriteriaBuilder builder = getEm().getCriteriaBuilder();

        CriteriaQuery<CloseComplaintRequest> query = builder.createQuery(CloseComplaintRequest.class);
        Root<CloseComplaintRequest> closeComplaintRequest = query.from(CloseComplaintRequest.class);

        query.select(closeComplaintRequest);

        query.where(
                builder.and(
                        builder.equal(closeComplaintRequest.<Long> get("complaintId"), complaintId)));

        TypedQuery<CloseComplaintRequest> dbQuery = getEm().createQuery(query);

        try
        {
            result = dbQuery.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.info("There is no any CloseComplaintRequest connected with complaint id " + complaintId);
        }

        return result;
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

    @Override
    public String getSupportedObjectType()
    {
        return CloseComplaintConstants.CLOSE_COMPLAINT_REQUEST;
    }
}
