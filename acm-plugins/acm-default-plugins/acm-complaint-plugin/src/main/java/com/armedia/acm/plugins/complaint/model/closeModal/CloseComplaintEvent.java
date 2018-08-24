package com.armedia.acm.plugins.complaint.model.closeModal;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import java.util.Date;

public class CloseComplaintEvent extends AcmEvent
{
    private CloseComplaintRequest request;
    private String complaintNumber;
    private UploadedFiles uploadedFiles;
    private Long complaintId;
    private String mode;

    public CloseComplaintEvent(String complaintNumber, Long complaintId, CloseComplaintRequest source,
            UploadedFiles files, String mode, String user, String ipAddress,
            boolean succeeded)
    {
        super(source);
        setMode(mode);

        setUserId(user);
        setEventDate(new Date());

        String event = "edit".equals(mode) ? "updated" : "created";
        setEventType("com.armedia.acm.closeComplaintRequest." + event);

        setIpAddress(ipAddress);
        setObjectId(source.getId());
        setObjectType("CLOSE_COMPLAINT_REQUEST");

        setSucceeded(succeeded);

        setRequest(source);
        setUploadedFiles(files);

        setComplaintNumber(complaintNumber);
        setComplaintId(complaintId);
    }

    public CloseComplaintRequest getRequest()
    {
        return request;
    }

    public void setRequest(CloseComplaintRequest request)
    {
        this.request = request;
    }

    public String getComplaintNumber()
    {
        return complaintNumber;
    }

    public void setComplaintNumber(String complaintNumber)
    {
        this.complaintNumber = complaintNumber;
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public UploadedFiles getUploadedFiles()
    {
        return uploadedFiles;
    }

    public void setUploadedFiles(UploadedFiles uploadedFiles)
    {
        this.uploadedFiles = uploadedFiles;
    }
}
