package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintClosedEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by armdev on 11/13/14.
 */
public class CloseCompaintRequestService
{
    private ComplaintDao complaintDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;
    private ComplaintEventPublisher complaintEventPublisher;

    @Transactional
    public void handleCloseComplaintRequestApproved(
            Long complaintId,
            Long closeComplaintRequestId,
            String user,
            Date approvalDate)
    {
        updateComplaintStatus(complaintId, user, approvalDate);

        updateCloseComplaintRequestStatus(closeComplaintRequestId);
    }

    private void updateCloseComplaintRequestStatus(Long closeComplaintRequestId)
    {
        CloseComplaintRequest ccr = getCloseComplaintRequestDao().find(closeComplaintRequestId);
        ccr.setStatus("APPROVED");
        getCloseComplaintRequestDao().save(ccr);
    }

    private void updateComplaintStatus(Long complaintId, String user, Date approvalDate)
    {
        Complaint c = getComplaintDao().find(complaintId);
        c.setStatus("CLOSED");
        c = getComplaintDao().save(c);

        getComplaintEventPublisher().publishComplaintClosedEvent(c, user, true, approvalDate);
    }


    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public CloseComplaintRequestDao getCloseComplaintRequestDao()
    {
        return closeComplaintRequestDao;
    }

    public void setCloseComplaintRequestDao(CloseComplaintRequestDao closeComplaintRequestDao)
    {
        this.closeComplaintRequestDao = closeComplaintRequestDao;
    }

    public ComplaintEventPublisher getComplaintEventPublisher()
    {
        return complaintEventPublisher;
    }

    public void setComplaintEventPublisher(ComplaintEventPublisher complaintEventPublisher)
    {
        this.complaintEventPublisher = complaintEventPublisher;
    }
}
