package com.armedia.acm.plugins.consultation.pipeline.postsave;

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
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.users.service.ldap.AcmUserActionExecutor;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ChangeConsultationStateHandler
        implements PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{

    private AcmUserActionExecutor userActionExecutor;
    private ConsultationDao consultationDao;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");

        Consultation consultation = getConsultationDao().find(form.getConsultationId());

        // Update Status to "IN APPROVAL"
        if (ctx.getPropertyValue("changeConsultationStatusFlow").equals(false))
        {
            consultation.setStatus(form.getStatus());
        }
        else if (!consultation.getStatus().equals("IN APPROVAL") && !"edit".equals(mode))
        {
            consultation.setStatus("IN APPROVAL");

        }
        Consultation updatedConsultation = getConsultationDao().save(consultation);

        ctx.setConsultation(updatedConsultation);

    }

    @Override
    public void rollback(ChangeConsultationStatus entity, ConsultationPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public AcmUserActionExecutor getUserActionExecutor()
    {
        return userActionExecutor;
    }

    public void setUserActionExecutor(AcmUserActionExecutor userActionExecutor)
    {
        this.userActionExecutor = userActionExecutor;
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
