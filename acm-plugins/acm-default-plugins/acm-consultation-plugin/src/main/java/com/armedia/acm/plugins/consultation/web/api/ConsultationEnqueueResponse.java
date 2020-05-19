package com.armedia.acm.plugins.consultation.web.api;

import com.armedia.acm.plugins.consultation.model.Consultation;

import java.util.ArrayList;
import java.util.List;

public class ConsultationEnqueueResponse
{

    private final boolean success;
    private final ErrorReason reason;
    private final List<String> errors;
    private final String requestedQueue;

    private Consultation consultation;

    public ConsultationEnqueueResponse(ErrorReason reason, String requestedQueue, Consultation consultation)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        errors = new ArrayList<>();
        this.requestedQueue = requestedQueue;
        this.consultation = consultation;
    }

    public ConsultationEnqueueResponse(ErrorReason reason, List<String> errors, String requestedQueue, Consultation consultation)
    {
        success = ErrorReason.NO_ERROR.equals(reason);
        this.reason = reason;
        this.errors = errors;
        this.requestedQueue = requestedQueue;
        this.consultation = consultation;
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

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public enum ErrorReason
    {
        NO_ERROR, LEAVE, NEXT_POSSIBLE, ENTER, ON_LEAVE, ON_ENTER
    }
}
