package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.model.CaseFile;

import java.util.ArrayList;
import java.util.List;

public class CaseFileEnqueueResponse
{

    private final boolean success;
    private final ErrorReason reason;
    private final List<String> errors;
    private final String requestedQueue;

    // We have the caseId from the CaseFile instance.
    // private final Long caseId;
    private CaseFile caseFile;

    public CaseFileEnqueueResponse(ErrorReason reason, String requestedQueue, CaseFile caseFile)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        errors = new ArrayList<>();
        this.requestedQueue = requestedQueue;
        this.caseFile = caseFile;
    }

    public CaseFileEnqueueResponse(ErrorReason reason, List<String> errors, String requestedQueue, CaseFile caseFile)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        this.errors = errors;
        this.requestedQueue = requestedQueue;
        this.caseFile = caseFile;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public ErrorReason getReason()
    {
        return reason;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public String getRequestedQueue()
    {
        return requestedQueue;
    }

    // public Long getCaseId()
    // {
    // return caseId;
    // }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    protected void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }

    public enum ErrorReason
    {
        NO_ERROR, LEAVE, NEXT_POSSIBLE, ENTER, ON_LEAVE, ON_ENTER
    }
}
