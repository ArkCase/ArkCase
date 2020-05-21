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

import static com.armedia.acm.auth.AuthenticationUtils.getUserIpAddress;

import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStateContants;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatusEvent;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class CreateTaskChangeConsultationHandler
        implements ApplicationEventPublisherAware, PipelineHandler<ChangeConsultationStatus, ConsultationPipelineContext>
{
    private ApplicationEventPublisher applicationEventPublisher;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(ChangeConsultationStatus form, ConsultationPipelineContext ctx)
    {
        String mode = (String) ctx.getPropertyValue("mode");
        Consultation consultation = ctx.getConsultation();
        UploadedFiles uploadedFile = new UploadedFiles();

        EcmFile existing = ecmFileDao.findForContainerAttachmentFolderAndFileType(consultation.getContainer().getId(),
                consultation.getContainer().getAttachmentFolder().getId(), ChangeConsultationStateContants.CHANGE_CONSULTATION_STATUS);

        uploadedFile.setPdfRendition(existing);

        ChangeConsultationStatusEvent event = new ChangeConsultationStatusEvent(consultation.getConsultationNumber(), consultation.getId(), form,
                uploadedFile, mode, ctx.getAuthentication().getName(), getUserIpAddress(), true);
        getApplicationEventPublisher().publishEvent(event);
    }

    @Override
    public void rollback(ChangeConsultationStatus form, ConsultationPipelineContext ctx)
    {
        // nothing to do here, there is no rollback action to be executed
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public ApplicationEventPublisher getApplicationEventPublisher()
    {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
