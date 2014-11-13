package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.CloseComplaintRequestDao;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by armdev on 11/13/14.
 */
public class CloseCompaintRequestService
{
    private ComplaintDao complaintDao;
    private CloseComplaintRequestDao closeComplaintRequestDao;

    @Transactional
    public void handleCloseComplaintRequestApproved(
            Long complaintId,
            Long closeComplaintRequestId,
            String user,
            Date approvalDate)
    {
        Complaint c = getComplaintDao().find(complaintId);
        c.setStatus("CLOSED");
        c = getComplaintDao().save(c);

        CloseComplaintRequest ccr = getCloseComplaintRequestDao().find(closeComplaintRequestId);
        ccr.setStatus("APPROVED");
        getCloseComplaintRequestDao().save(ccr);
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
}
