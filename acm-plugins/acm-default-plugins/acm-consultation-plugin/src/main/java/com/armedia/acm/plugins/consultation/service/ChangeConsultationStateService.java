package com.armedia.acm.plugins.consultation.service;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
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

    public void handleChangeConsultationStatusApproved(Long consultationId, Long requestId, String userId, Date approvalDate,
            String ipAddress)
    {
        Consultation updatedConsultation = updateConsultationStatus(consultationId, requestId);

        updateConsultationStatusRequestToApproved(requestId);

        getConsultationEventUtility().raiseEvent(updatedConsultation, updatedConsultation.getStatus(), approvalDate, ipAddress, userId,
                null);
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

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
    }

    public ChangeConsultationStatusDao getChangeConsultationStatusDao()
    {
        return changeConsultationStatusDao;
    }

    public void setChangeConsultationStatusDao(ChangeConsultationStatusDao changeConsultationStatusDao)
    {
        this.changeConsultationStatusDao = changeConsultationStatusDao;
    }

    public ConsultationEventUtility getConsultationEventUtility()
    {
        return consultationEventUtility;
    }

    public void setConsultationEventUtility(ConsultationEventUtility consultationEventUtility)
    {
        this.consultationEventUtility = consultationEventUtility;
    }

    public PipelineManager<ChangeConsultationStatus, ConsultationPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<ChangeConsultationStatus, ConsultationPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}
