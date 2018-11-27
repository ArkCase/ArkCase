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

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNameDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.model.TimePeriod;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 4/4/14.
 */
@Transactional
public class ComplaintDao extends AcmAbstractDao<Complaint> implements AcmNotificationDao, AcmNameDao
{
    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Complaint save(Complaint toSave)
    {
        return super.save(toSave);
    }

    public List<ComplaintListView> listAllComplaints()
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<ComplaintListView> query = builder.createQuery(ComplaintListView.class);
        Root<ComplaintListView> clv = query.from(ComplaintListView.class);
        query.select(clv);

        // TODO: parameterized order by
        query.orderBy(builder.desc(clv.get("created")));

        TypedQuery<ComplaintListView> dbQuery = getEm().createQuery(query);

        List<ComplaintListView> results = dbQuery.getResultList();

        return results;

    }

    public List<ComplaintListView> listComplaintsByTimePeriod(TimePeriod timePeriod)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<ComplaintListView> query = builder.createQuery(ComplaintListView.class);
        Root<ComplaintListView> clv = query.from(ComplaintListView.class);
        query.select(clv).where(builder.greaterThanOrEqualTo(clv.<Date> get("created"), shiftDateFromToday(timePeriod.getNumOfDays())));

        // TODO: parameterized order by
        query.orderBy(builder.desc(clv.get("created")));
        TypedQuery<ComplaintListView> dbQuery = getEm().createQuery(query);
        List<ComplaintListView> results = dbQuery.getResultList();
        return results;
    }

    public List<ComplaintListView> listAllUserComplaints(String userId)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<ComplaintListView> query = builder.createQuery(ComplaintListView.class);
        Root<ComplaintListView> clv = query.from(ComplaintListView.class);
        query.select(clv);

        Subquery<AcmParticipant> assigneeQuery = query.subquery(AcmParticipant.class);
        Root<AcmParticipant> assigneeRoot = assigneeQuery.from(AcmParticipant.class);
        assigneeQuery.select(assigneeRoot);

        assigneeQuery.where(builder.and(builder.equal(assigneeRoot.get("objectType"), "COMPLAINT"),
                builder.equal(assigneeRoot.get("objectId"), clv.get("complaintId")),
                builder.equal(assigneeRoot.get("participantLdapId"), userId),
                builder.equal(assigneeRoot.get("participantType"), "assignee")));

        query.where(builder.and(builder.exists(assigneeQuery), builder.notEqual(clv.get("status"), "CLOSED")));

        // TODO: parameterized order by
        query.orderBy(builder.desc(clv.get("created")));
        TypedQuery<ComplaintListView> dbQuery = getEm().createQuery(query);
        List<ComplaintListView> results = dbQuery.getResultList();
        return results;
    }

    public Complaint quietFindByComplaintNumber(String complaintNumber)
    {
        Complaint complaint = null;
        try
        {
            complaint = findByComplaintNumber(complaintNumber);
        }
        catch (Exception e)
        {
            LOG.warn("No complaint has complaint number {}", complaintNumber);
        }

        return complaint;
    }

    public Complaint findByComplaintNumber(String complaintNumber)
    {

        String queryText = "SELECT c FROM Complaint c WHERE c.complaintNumber = :complaintNumber";

        TypedQuery<Complaint> findByNumberQuery = getEm().createQuery(queryText, Complaint.class);
        findByNumberQuery.setParameter("complaintNumber", complaintNumber);

        return findByNumberQuery.getSingleResult();
    }

    @Transactional
    public int updateComplaintStatus(Long complaintId, String newStatus, String modifier, Date date)
    {
        Query updateStatusQuery = getEm().createQuery("UPDATE Complaint " + "SET status = :newStatus, " + "modified = :modified, "
                + "modifier = :modifier " + "WHERE complaintId = :complaintId");
        updateStatusQuery.setParameter("newStatus", newStatus);
        updateStatusQuery.setParameter("modified", date);
        updateStatusQuery.setParameter("modifier", modifier);
        updateStatusQuery.setParameter("complaintId", complaintId);

        return updateStatusQuery.executeUpdate();
    }

    @Override
    protected Class<Complaint> getPersistenceClass()
    {
        return Complaint.class;
    }

    private Date shiftDateFromToday(int daysFromToday)
    {
        Date nextDate;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -daysFromToday);
        nextDate = cal.getTime();
        return nextDate;
    }

    @Override
    public String getSupportedObjectType()
    {
        return ComplaintConstants.OBJECT_TYPE;
    }

    @Override
    public AcmNotifiableEntity findEntity(Long id)
    {
        return find(id);
    }

    @Override
    public String getSupportedNotifiableObjectType()
    {
        return ComplaintConstants.OBJECT_TYPE;
    }

    @Override
    public AcmObject findByName(String name)
    {
        return findByComplaintNumber(name);
    }
}
