package com.armedia.acm.plugins.consultation.service;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.consultation.dao.ChangeConsultationStatusDao;
import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.consultation.utility.ConsultationEventUtility;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class ChangeConsultationStateService
{
    private final Logger log = LogManager.getLogger(getClass());
    private ConsultationDao consultationDao;
    private ChangeConsultationStatusDao changeConsultationStatusDao;
    private ConsultationEventUtility consultationEventUtility;
    private PipelineManager<ChangeConsultationStatus, ConsultationPipelineContext> pipelineManager;

    @Transactional
    public void save(ChangeConsultationStatus form, Authentication auth, String mode) throws PipelineProcessException
    {
        ConsultationPipelineContext ctx = new ConsultationPipelineContext();
        ctx.setAuthentication(auth);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        ctx.setIpAddress(ipAddress);
        ctx.addProperty("mode", mode);
        ctx.addProperty("consultationResolution", form.getConsultationResolution());
        ctx.addProperty("changeDate", form.getChangeDate().toString());
        ctx.addProperty("changeConsultationFlow", form.isChangeConsultationStatusFlow());

        pipelineManager.executeOperation(form, ctx, () -> {

            ChangeConsultationStatus savedConsultationStatus = getChangeConsultationStatusDao().save(form);
            ctx.setChangeConsultationStatus(savedConsultationStatus);
            return savedConsultationStatus;
        });
    }

    public Consultation changeConsultationState(Authentication auth, Long consultationId, String newState, String ipAddress)
            throws AcmUserActionFailedException
    {
        try
        {
            log.info("Consultation ID : [{}] and incoming status is : [{}]", consultationId, newState);
            Consultation retval = getConsultationDao().find(consultationId);

            // do we need to do anything?
            if (retval.getStatus().equals(newState))
            {
                return retval;
            }

            Date now = new Date();

            retval.setStatus(newState);

            if ("CLOSED".equals(newState))
            {
                retval.setClosed(now);
            }

            retval = getConsultationDao().save(retval);

            log.info("Consultation ID : [{}] and saved status is : [{}]", consultationId, retval.getStatus());

            getConsultationEventUtility().raiseEvent(retval, newState, now, ipAddress, auth.getName(), auth);

            return retval;
        }
        catch (Exception e)
        {
            throw new AcmUserActionFailedException("Set consultation to " + newState, "Consultation", consultationId, e.getMessage(), e);
        }
    }

    public void handleChangeConsultationStatusApproved(Long consultationId, Long requestId, String userId, Date approvalDate, String ipAddress)
    {
        Consultation updatedConsultation = updateConsultationStatus(consultationId, requestId);

        updateConsultationStatusRequestToApproved(requestId);

        getConsultationEventUtility().raiseEvent(updatedConsultation, updatedConsultation.getStatus(), approvalDate, ipAddress, userId, null);
    }

    private Consultation updateConsultationStatus(Long consultationId, Long requestId)
    {
        ChangeConsultationStatus changeConsultationStatus = getChangeConsultationStatusDao().find(requestId);

        Consultation toSave = getConsultationDao().find(consultationId);
        toSave.setStatus(changeConsultationStatus.getStatus());

        Consultation updated = getConsultationDao().save(toSave);

        return updated;
    }

    private ChangeConsultationStatus updateConsultationStatusRequestToApproved(Long id)
    {
        ChangeConsultationStatus toSave = getChangeConsultationStatusDao().find(id);
        toSave.setStatus("APPROVED");

        ChangeConsultationStatus updated = getChangeConsultationStatusDao().save(toSave);

        return updated;
    }

    public ConsultationDao getConsultationDao() {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao) {
        this.consultationDao = consultationDao;
    }

    public ChangeConsultationStatusDao getChangeConsultationStatusDao() {
        return changeConsultationStatusDao;
    }

    public void setChangeConsultationStatusDao(ChangeConsultationStatusDao changeConsultationStatusDao) {
        this.changeConsultationStatusDao = changeConsultationStatusDao;
    }

    public ConsultationEventUtility getConsultationEventUtility() {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility) {
        this.consultationEventUtility = consultationEventUtility;
    }

    public PipelineManager<ChangeConsultationStatus, ConsultationPipelineContext> getPipelineManager() {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<ChangeConsultationStatus, ConsultationPipelineContext> pipelineManager) {
        this.pipelineManager = pipelineManager;
    }
}
