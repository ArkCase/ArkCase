package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.model.Complaint;

/**
 * @author aleksandar.bujaroski
 */
public interface GetComplaintService {
    Complaint getComplaintById(long id);
}
