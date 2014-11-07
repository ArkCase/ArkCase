package com.armedia.acm.form.closecomplaint.model;

import com.armedia.acm.event.AcmEvent;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;

import java.util.Date;

/**
 * Created by armdev on 11/5/14.
 */
public class CloseComplaintFormEvent extends AcmEvent
{
    private CloseComplaintRequest request;
    private FrevvoUploadedFiles frevvoUploadedFiles;
    private String complaintNumber;
    private Long complaintId;
    private String mode;

    public CloseComplaintFormEvent(String complaintNumber, Long complaintId, CloseComplaintRequest source,
                                   FrevvoUploadedFiles files, String mode, String user, String ipAddress,
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
        setFrevvoUploadedFiles(files);

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

    public FrevvoUploadedFiles getFrevvoUploadedFiles()
    {
        return frevvoUploadedFiles;
    }

    public void setFrevvoUploadedFiles(FrevvoUploadedFiles frevvoUploadedFiles)
    {
        this.frevvoUploadedFiles = frevvoUploadedFiles;
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
}
