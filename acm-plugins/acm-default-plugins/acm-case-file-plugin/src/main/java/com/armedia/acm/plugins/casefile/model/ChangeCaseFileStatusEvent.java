package com.armedia.acm.plugins.casefile.model;

import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;

import java.util.Date;

public class ChangeCaseFileStatusEvent extends AcmEvent
{
    private static final long serialVersionUID = 9214955996048509545L;

    private ChangeCaseStatus request;
    private FrevvoUploadedFiles frevvoUploadedFiles;
    private String caseNumber;
    private Long caseId;
    private String mode;

    public ChangeCaseFileStatusEvent(String caseNumber, Long caseId, ChangeCaseStatus source,
            FrevvoUploadedFiles files, String mode, String userId, String ipAddress,
            boolean succeeded)
    {
        super(source);

        setMode(mode);
        setUserId(userId);
        setEventDate(new Date());

        String event = "edit".equals(mode) ? "updated" : "created";
        setEventType("com.armedia.acm.changeCaseStatus." + event);

        setIpAddress(ipAddress);
        setObjectId(source.getId());
        setObjectType("CHANGE_CASE_STATUS");

        setSucceeded(succeeded);

        setRequest(source);
        setFrevvoUploadedFiles(files);

        setCaseNumber(caseNumber);
        setCaseId(caseId);
    }

    public ChangeCaseStatus getRequest()
    {
        return request;
    }

    public void setRequest(ChangeCaseStatus request)
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

    public String getCaseNumber()
    {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber)
    {
        this.caseNumber = caseNumber;
    }

    public Long getCaseId()
    {
        return caseId;
    }

    public void setCaseId(Long caseId)
    {
        this.caseId = caseId;
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
