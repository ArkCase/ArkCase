package com.armedia.acm.plugins.consultation.pipeline;

import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

/**
 * Store all the consultation saving-related references in this context.
 */
public class ConsultationPipelineContext extends AbstractPipelineContext
{
    /**
     * Flag showing whether new consultation is created.
     */
    private boolean newConsultation;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * The queue that consultation is already in.
     */
    private String queueName;

    /**
     * The queue the consultation will be moved to; used in the queue pipeline.
     */
    private String enqueueName;

    /**
     * IP Address.
     */
    private String ipAddress;

    /*
     * Consultation
     */
    private Consultation consultation;

    /*
     * Change Consultation Status
     */
    private ChangeConsultationStatus changeConsultationStatus;

    public boolean isNewConsultation()
    {
        return newConsultation;
    }

    public void setNewConsultation(boolean newConsultation)
    {
        this.newConsultation = newConsultation;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getEnqueueName()
    {
        return enqueueName;
    }

    public void setEnqueueName(String enqueueName)
    {
        this.enqueueName = enqueueName;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public ChangeConsultationStatus getChangeConsultationStatus() {
        return changeConsultationStatus;
    }

    public void setChangeConsultationStatus(ChangeConsultationStatus changeConsultationStatus) {
        this.changeConsultationStatus = changeConsultationStatus;
    }
}
