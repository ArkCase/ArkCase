package com.armedia.acm.plugins.consultation.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.frevvo.model.UploadedFiles;

import java.util.Date;

public class ChangeConsultationStatusEvent extends AcmEvent
{
    private static final long serialVersionUID = 9214955996048509545L;

    private ChangeConsultationStatus request;
    private UploadedFiles uploadedFiles;
    private String consultationNumber;
    private Long consultationId;
    private String mode;

    public ChangeConsultationStatusEvent(String consultationNumber, Long consultationId, ChangeConsultationStatus source,
                                         UploadedFiles files, String mode, String userId, String ipAddress,
                                         boolean succeeded)
    {
        super(source);

        setMode(mode);
        setUserId(userId);
        setEventDate(new Date());

        String event = "edit".equals(mode) ? "updated" : "created";
        setEventType("com.armedia.acm.changeConsultationStatus." + event);

        setIpAddress(ipAddress);
        setObjectId(source.getId());
        setObjectType("CHANGE_CONSULTATION_STATUS");

        setSucceeded(succeeded);

        setRequest(source);
        setUploadedFiles(files);

        setConsultationNumber(consultationNumber);
        setConsultationId(consultationId);
    }

    public ChangeConsultationStatus getRequest()
    {
        return request;
    }

    public void setRequest(ChangeConsultationStatus request)
    {
        this.request = request;
    }

    public UploadedFiles getUploadedFiles()
    {
        return uploadedFiles;
    }

    public void setUploadedFiles(UploadedFiles uploadedFiles)
    {
        this.uploadedFiles = uploadedFiles;
    }

    public String getConsultationNumber() {
        return consultationNumber;
    }

    public void setConsultationNumber(String consultationNumber) {
        this.consultationNumber = consultationNumber;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
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
