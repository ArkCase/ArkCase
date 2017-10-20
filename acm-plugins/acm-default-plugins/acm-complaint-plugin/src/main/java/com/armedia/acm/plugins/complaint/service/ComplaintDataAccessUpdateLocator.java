package com.armedia.acm.plugins.complaint.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.dataaccess.service.AcmObjectDataAccessBatchUpdateLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by armdev on 2/17/15.
 */
public class ComplaintDataAccessUpdateLocator implements AcmObjectDataAccessBatchUpdateLocator<Complaint>
{
    private ComplaintDao complaintDao;
    private EcmFileParticipantService fileParticipantService;

    @Override
    public List<Complaint> getObjectsModifiedSince(Date lastUpdate, int start, int pageSize)
    {
        return getComplaintDao().findModifiedSince(lastUpdate, start, pageSize);
    }

    @Override
    public void save(Complaint assignedObject) throws AcmAccessControlException
    {
        Complaint originalComplaint = getComplaintDao().find(assignedObject.getId());
        getComplaintDao().save(assignedObject);
        getFileParticipantService().inheritParticipantsFromAssignedObject(assignedObject.getParticipants(),
                originalComplaint.getParticipants(), assignedObject.getContainer().getFolder());
        getFileParticipantService().inheritParticipantsFromAssignedObject(assignedObject.getParticipants(),
                originalComplaint.getParticipants(), assignedObject.getContainer().getAttachmentFolder());
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
