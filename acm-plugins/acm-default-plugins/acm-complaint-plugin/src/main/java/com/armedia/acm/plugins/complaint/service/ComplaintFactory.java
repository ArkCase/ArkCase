package com.armedia.acm.plugins.complaint.service;


import com.armedia.acm.plugins.complaint.model.Complaint;

public class ComplaintFactory
{
    public Complaint asAcmComplaint(com.armedia.acm.plugins.complaint.model.complaint.Complaint formComplaint)
    {
        Complaint retval = new Complaint();

        retval.setDetails(formComplaint.getComplaintDescription());
        retval.setIncidentDate(formComplaint.getDate());
        retval.setPriority(formComplaint.getPriority());

        return retval;
    }
}
