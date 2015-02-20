package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class ComplaintDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<Complaint>
{
    private ComplaintDao complaintDao;

    @Override
    public List<Complaint> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getComplaintDao().findModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(Complaint assignedObject)
    {
        getComplaintDao().save(assignedObject);
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }
}
