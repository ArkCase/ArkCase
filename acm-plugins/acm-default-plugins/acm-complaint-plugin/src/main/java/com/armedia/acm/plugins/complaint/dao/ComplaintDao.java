package com.armedia.acm.plugins.complaint.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintListView;
import com.armedia.acm.plugins.complaint.model.TimePeriod;
import com.armedia.acm.services.users.model.AcmParticipant;
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
public class ComplaintDao extends AcmAbstractDao<Complaint>
{
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

    public List<ComplaintListView> listComplaintsByTimePeriod(TimePeriod timePeriod) {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<ComplaintListView> query = builder.createQuery(ComplaintListView.class);
        Root<ComplaintListView> clv = query.from(ComplaintListView.class);
        query.select(clv).where(builder.greaterThanOrEqualTo(clv.<Date>get("created"), shiftDateFromToday(timePeriod.getNumOfDays())));

        // TODO: parameterized order by
        query.orderBy(builder.desc(clv.get("created")));
        TypedQuery<ComplaintListView> dbQuery = getEm().createQuery(query);
        List<ComplaintListView> results = dbQuery.getResultList();
        return results;
    }

    public List<ComplaintListView> listAllUserComplaints(String userId) {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<ComplaintListView> query = builder.createQuery(ComplaintListView.class);
        Root<ComplaintListView> clv = query.from(ComplaintListView.class);
        query.select(clv);

        Subquery<AcmParticipant> assigneeQuery = query.subquery(AcmParticipant.class);
        Root<AcmParticipant> assigneeRoot = assigneeQuery.from(AcmParticipant.class);
        assigneeQuery.select(assigneeRoot);

        assigneeQuery.where(builder.and(
                builder.equal(assigneeRoot.get("objectType"), "COMPLAINT"),
                builder.equal(assigneeRoot.get("objectId"), clv.get("complaintId")),
                builder.equal(assigneeRoot.get("participantLdapId"), userId),
                builder.equal(assigneeRoot.get("participantType"), "assignee")));

        query.where(builder.exists(assigneeQuery));

        // TODO: parameterized order by
        query.orderBy(builder.desc(clv.get("created")));
        TypedQuery<ComplaintListView> dbQuery = getEm().createQuery(query);
        List<ComplaintListView> results = dbQuery.getResultList();
        return results;
    }

    @Transactional
    public int updateComplaintStatus(Long complaintId, String newStatus, String modifier)
    {
        Query updateStatusQuery = getEm().createQuery(
                "UPDATE Complaint " +
                        "SET status = :newStatus, " +
                        "modified = :modified, " +
                        "modifier = :modifier " +
                        "WHERE complaintId = :complaintId");
        updateStatusQuery.setParameter("newStatus", newStatus);
        updateStatusQuery.setParameter("modified", new Date());
        updateStatusQuery.setParameter("modifier", modifier);
        updateStatusQuery.setParameter("complaintId", complaintId);

        return updateStatusQuery.executeUpdate();
    }

    @Override
    protected Class<Complaint> getPersistenceClass()
    {
        return Complaint.class;
    }

    private Date shiftDateFromToday(int daysFromToday){
        Date nextDate;
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE,-daysFromToday);
        nextDate = cal.getTime();
        return nextDate;
    }
}
