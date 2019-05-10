package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;

/**
 * @author aleksandar.bujaroski
 */
public class GetComplaintServiceImpl implements GetComplaintService {

    private ComplaintDao complaintDao;

    @Override
    public Complaint getComplaintById(long id) {
        return getComplaintDao().find(id);
    }

    public ComplaintDao getComplaintDao() {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao) {
        this.complaintDao = complaintDao;
    }
}
