package com.armedia.acm.plugins.consultation.pipeline.presave;

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

import com.armedia.acm.plugins.consultation.dao.ConsultationDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class CheckChangeConsultationState implements PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{
    private Logger LOG = LogManager.getLogger(getClass());

    private ConsultationDao consultationDao;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx) throws PipelineProcessException
    {
        String mode = (String) ctx.getPropertyValue("mode");
        String message = "";

        if (form == null)
        {
            throw new PipelineProcessException("Cannot un marshall Close Consultation Form.");
        }

        // Get Consultation depends on the Consultation ID
        Consultation consultation = getConsultationDao().find(form.getConsultationId());

        if (consultation == null)
        {
            throw new PipelineProcessException(
                    String.format("Cannot find consultation file by given consultationId=%d", form.getConsultationId()));
        }

        // Skip if the consultation is already closed or in "in approval" and if it's not edit mode
        if (("IN APPROVAL".equals(consultation.getStatus())) && !"edit".equals(mode))
        {
            LOG.info("The consultation is already in '[{}]' mode. No further action will be taken.", consultation.getStatus());
            message = String.format("The consultation is already in '%s' mode. No further action will be taken.", consultation.getStatus());
        }

        if (!message.isEmpty())
        {
            throw new PipelineProcessException(message);
        }
    }

    @Override
    public void rollback(ChangeConsultationStatus entity, ConsultationPipelineContext pipelineContext)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public ConsultationDao getConsultationDao()
    {
        return consultationDao;
    }

    public void setConsultationDao(ConsultationDao consultationDao)
    {
        this.consultationDao = consultationDao;
    }
}
